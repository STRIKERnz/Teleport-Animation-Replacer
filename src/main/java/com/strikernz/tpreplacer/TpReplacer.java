package com.strikernz.tpreplacer;

import com.google.inject.Provides;
import net.runelite.api.ActorSpotAnim;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "Teleport Animation Replacer",
        description = "Replace teleport animations with different animations, graphics, and sounds",
        tags = {"animation", "teleport", "sound", "graphic", "customization", "cowbell", "cosmetic"}
)
public class TpReplacer extends Plugin {
    private static final int NO_ID = -1;
    private static final int ARRIVAL_SOUND_DELAY_TICKS = 2;
    private static final int ORIGINAL_SOUND_MUTE_TICKS = 3;
    private static final int ORIGINAL_GRAPHIC_SUPPRESS_TICKS = 3;
    private static final int ARRIVAL_ANIMATION_RESET_TICKS = 1;

    /**
     * Known teleport sound IDs to matching presets, built once for fast sound suppression.
     */
    private static final Map<Integer, Set<TeleportAnimation>> TELEPORTS_BY_SOUND = new HashMap<>();

    static {
        for (TeleportAnimation ta : TeleportAnimation.values()) {
            if (ta.getSoundId() != NO_ID) {
                TELEPORTS_BY_SOUND.computeIfAbsent(ta.getSoundId(), ignored -> new HashSet<>()).add(ta);
            }
        }
    }

    /**
     * Original-sound-id to tick at which the mute expires (inclusive).
     */
    private final Map<Integer, Integer> mutedSoundUntilTick = new HashMap<>();
    private final Map<Integer, Integer> suppressedGraphicUntilTick = new HashMap<>();
    @Inject
    private Client client;
    @Inject
    private TpreplacerConfig config;
    /**
     * Whether the local player is currently in a two-phase teleport (waiting for landing).
     */
    private boolean teleporting;
    /**
     * Ticks remaining before the arrival sound should play ({@code -1} = inactive).
     */
    private int arrivalSoundTicksRemaining = NO_ID;
    /**
     * Ticks remaining before a client-only arrival animation is cleared.
     */
    private int arrivalAnimationResetTicksRemaining = NO_ID;
    /**
     * Tracks the last sound the plugin played so we don't suppress our own effects.
     */
    private int lastPlayedSoundId = NO_ID;
    private int lastPlayedSoundTick = NO_ID;
    /**
     * Arrival specifics for two-phase teleports (animation, graphic, sound, delay in ticks).
     */
    private int arrivalAnimationId = NO_ID;
    private int activeArrivalAnimationId = NO_ID;
    private int arrivalGraphicId = NO_ID;
    private int arrivalSoundId = NO_ID;
    private int arrivalSoundDelay = ARRIVAL_SOUND_DELAY_TICKS;

    private static CustomTeleportIds parseCustomIds(String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            return CustomTeleportIds.EMPTY;
        }

        String[] parts = ids.split(",", -1);
        try {
            return new CustomTeleportIds(
                    parseCustomId(parts, 0),
                    parseCustomId(parts, 1),
                    parseCustomId(parts, 2)
            );
        } catch (NumberFormatException ex) {
            return CustomTeleportIds.EMPTY;
        }
    }

    private static int parseCustomId(String[] parts, int index) {
        if (index >= parts.length || parts[index].trim().isEmpty()) {
            return NO_ID;
        }

        return Integer.parseInt(parts[index].trim());
    }

    @Override
    protected void startUp() {
        resetState();
    }

    @Override
    protected void shutDown() {
        resetState();
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Player player = client.getLocalPlayer();
        if (player == null || event.getActor() != player) {
            return;
        }

        int animationId = player.getAnimation();

        // Two-phase teleport arrival: play landing effects when the cast animation ends.
        if (teleporting && animationId == NO_ID) {
            teleporting = false;
            applyArrivalEffects(player);
            return;
        }

        // Suppress the Pendent of Ates arrival animation when the pendent is being overridden
        if (animationId == AnimationConstants.PENDENT_OF_ATES_TELEPORT_ARRIVAL) {
            TeleportAnimation pendentSelected = getSelectedForAnimation(AnimationConstants.PENDENT_OF_ATES_TELEPORT);
            if (pendentSelected != TeleportAnimation.NONE && pendentSelected != TeleportAnimation.PENDENT_OF_ATES) {
                player.setAnimation(NO_ID);
                return;
            }
        }

        if (!AnimationConstants.isTeleportAnimation(animationId)) {
            return;
        }

        TeleportAnimation selected = getSelectedForAnimation(animationId);

        // NONE means "don't override"
        if (selected == TeleportAnimation.NONE) {
            return;
        }

        CustomTeleportIds customIds = selected == TeleportAnimation.CUSTOM
                ? parseCustomIds(config.customIds())
                : CustomTeleportIds.EMPTY;
        int selectedAnimationId = selected == TeleportAnimation.CUSTOM
                ? customIds.animationId
                : selected.getAnimationId();

        // Already playing the target animation AND graphic — nothing to override
        int selectedGraphicId = selected == TeleportAnimation.CUSTOM ? customIds.graphicId : selected.getGraphicId();
        boolean sameAnimation = selectedAnimationId != NO_ID && animationId == selectedAnimationId;
        boolean sameGraphic = selectedGraphicId != NO_ID && player.hasSpotAnim(selectedGraphicId);
        if (sameAnimation && sameGraphic) {
            return;
        }

        // Mute the original teleport sound so it doesn't double-play
        TeleportAnimation original = TeleportAnimation.fromAnimationId(animationId);
        int originalSound = (original != null) ? original.getSoundId() : NO_ID;
        int originalGraphic = (original != null) ? original.getGraphicId() : NO_ID;

        if (originalSound != NO_ID) {
            mutedSoundUntilTick.put(originalSound, client.getTickCount() + ORIGINAL_SOUND_MUTE_TICKS);
        }

        suppressOriginalGraphic(player, originalGraphic, selectedGraphicId);

        clearPendingArrival();

        // Cowbell has a special two-phase animation (cast + landing)
        if (selected == TeleportAnimation.COWBELL) {
            teleporting = true;
            player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
            playSpotAnim(player, AnimationConstants.COWBELL_TELEPORT_GRAPHIC);

            // Cowbell landing is graphic/sound-only. Forcing an arrival animation can leave
            // the local actor in a client-only pose after the player has already moved.
            arrivalAnimationId = NO_ID;
            arrivalGraphicId = AnimationConstants.COWBELL_TELEPORT_GRAPHIC;
            arrivalSoundId = AnimationConstants.COWBELL_ARRIVAL_SOUND;
            arrivalSoundDelay = ARRIVAL_SOUND_DELAY_TICKS;

            return;
        }

        // Pendent of Ates: two-phase teleport with a specific arrival animation/graphic and same sound on arrival
        if (selected == TeleportAnimation.PENDENT_OF_ATES) {
            teleporting = true;
            player.setAnimation(AnimationConstants.PENDENT_OF_ATES_TELEPORT);
            playSpotAnim(player, AnimationConstants.PENDENT_OF_ATES_TELEPORT_GRAPHIC);

            // Configure arrival specifics for pendent — play sound immediately on landing (0 tick delay)
            arrivalAnimationId = AnimationConstants.PENDENT_OF_ATES_TELEPORT_ARRIVAL;
            arrivalGraphicId = AnimationConstants.PENDENT_OF_ATES_TELEPORT_ARRIVAL_GRAPHIC;
            arrivalSoundId = AnimationConstants.PENDENT_OF_ATES_TELEPORT_SOUND;
            arrivalSoundDelay = 0;

            // Play the initial teleport sound immediately for the pendent cast
            if (arrivalSoundId != NO_ID) {
                playSoundOnce(arrivalSoundId);
            }

            return;
        }

        // Handle custom override (uses numeric IDs from single comma-separated config field)
        if (selected == TeleportAnimation.CUSTOM) {
            if (customIds.animationId != NO_ID) {
                player.setAnimation(customIds.animationId);
            }

            if (customIds.graphicId != NO_ID) {
                playSpotAnim(player, customIds.graphicId);
            }

            if (customIds.soundId != NO_ID) {
                playSoundOnce(customIds.soundId);
            }

            return;
        }

        // Generic override path for enum-based presets
        player.setAnimation(selected.getAnimationId());

        if (selected.getGraphicId() != NO_ID) {
            playSpotAnim(player, selected.getGraphicId());
        }

        if (selected.getSoundId() != NO_ID) {
            playSoundOnce(selected.getSoundId());
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getLocalPlayer() == null) {
            return;
        }

        // Expire old mute entries
        int tick = client.getTickCount();
        mutedSoundUntilTick.values().removeIf(expire -> expire < tick);
        suppressedGraphicUntilTick.values().removeIf(expire -> expire < tick);
        removeSuppressedSpotAnims(client.getLocalPlayer());

        updateArrivalAnimationReset(client.getLocalPlayer());

        if (arrivalSoundTicksRemaining == NO_ID) {
            return;
        }

        if (arrivalSoundTicksRemaining == 0) {
            playSoundOnce(arrivalSoundId);
            arrivalSoundTicksRemaining = NO_ID;
        } else {
            arrivalSoundTicksRemaining--;
        }
    }

    @Subscribe
    public void onSoundEffectPlayed(SoundEffectPlayed event) {
        int soundId = event.getSoundId();
        int tick = client.getTickCount();

        // Don't suppress a sound the plugin itself just triggered
        if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick) {
            return;
        }

        boolean isLocalPlayer = event.getSource() == client.getLocalPlayer();

        // Mute map only applies to sounds sourced to the local player
        if (isLocalPlayer && mutedSoundUntilTick.getOrDefault(soundId, NO_ID) >= tick) {
            event.consume();
            return;
        }

        if (shouldSuppressKnownTeleportSound(soundId)) {
            event.consume();
        }
    }

    @Subscribe
    public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event) {

        int soundId = event.getSoundId();
        int tick = client.getTickCount();

        // Don't suppress a sound the plugin itself just triggered
        if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick) {
            return;
        }

        // First, honor explicit mutes we added when replacing teleports. This avoids races where
        // area sounds arrive before other logic and ensures replaced teleport sounds are consumed.
        if (mutedSoundUntilTick.getOrDefault(soundId, NO_ID) >= tick) {
            event.consume();
            return;
        }

        if (shouldSuppressKnownTeleportSound(soundId)) {
            event.consume();
        }

    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged event) {
        Player player = client.getLocalPlayer();
        if (player == null || event.getActor() != player) {
            return;
        }

        removeSuppressedSpotAnims(player);
    }

    @Provides
    TpreplacerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TpreplacerConfig.class);
    }

    private void resetState() {
        teleporting = false;
        clearPendingArrival();
        lastPlayedSoundId = NO_ID;
        lastPlayedSoundTick = NO_ID;
        mutedSoundUntilTick.clear();
        suppressedGraphicUntilTick.clear();
    }

    private void clearPendingArrival() {
        arrivalSoundTicksRemaining = NO_ID;
        arrivalAnimationResetTicksRemaining = NO_ID;
        arrivalAnimationId = NO_ID;
        activeArrivalAnimationId = NO_ID;
        arrivalGraphicId = NO_ID;
        arrivalSoundId = NO_ID;
        arrivalSoundDelay = ARRIVAL_SOUND_DELAY_TICKS;
    }

    private void applyArrivalEffects(Player player) {
        if (arrivalAnimationId != NO_ID) {
            player.setAnimation(arrivalAnimationId);
            activeArrivalAnimationId = arrivalAnimationId;
            arrivalAnimationResetTicksRemaining = ARRIVAL_ANIMATION_RESET_TICKS;
        }

        if (arrivalGraphicId != NO_ID) {
            playSpotAnim(player, arrivalGraphicId);
        }

        arrivalSoundTicksRemaining = arrivalSoundId == NO_ID ? NO_ID : arrivalSoundDelay;
    }

    private void updateArrivalAnimationReset(Player player) {
        if (arrivalAnimationResetTicksRemaining == NO_ID) {
            return;
        }

        if (arrivalAnimationResetTicksRemaining == 0) {
            if (activeArrivalAnimationId != NO_ID && player.getAnimation() == activeArrivalAnimationId) {
                player.setAnimation(NO_ID);
            }

            activeArrivalAnimationId = NO_ID;
            arrivalAnimationResetTicksRemaining = NO_ID;
            return;
        }

        arrivalAnimationResetTicksRemaining--;
    }

    private boolean shouldSuppressKnownTeleportSound(int soundId) {
        for (TeleportAnimation ta : TELEPORTS_BY_SOUND.getOrDefault(soundId, Collections.emptySet())) {
            TeleportAnimation selected = getSelectedForAnimation(ta.getAnimationId());
            if (selected != TeleportAnimation.NONE && selected != ta) {
                return true;
            }
        }

        return false;
    }

    private void playSpotAnim(Player player, int spotAnimId) {
        if (spotAnimId != NO_ID) {
            player.createSpotAnim(spotAnimId, spotAnimId, 0, 0);
        }
    }

    private void suppressOriginalGraphic(Player player, int originalGraphicId, int replacementGraphicId) {
        if (originalGraphicId == NO_ID || originalGraphicId == replacementGraphicId) {
            return;
        }

        suppressedGraphicUntilTick.put(originalGraphicId, client.getTickCount() + ORIGINAL_GRAPHIC_SUPPRESS_TICKS);
        removeSpotAnim(player, originalGraphicId);
    }

    private void removeSuppressedSpotAnims(Player player) {
        int tick = client.getTickCount();
        for (int graphicId : suppressedGraphicUntilTick.keySet()) {
            if (suppressedGraphicUntilTick.getOrDefault(graphicId, NO_ID) >= tick) {
                removeSpotAnim(player, graphicId);
            }
        }
    }

    private void removeSpotAnim(Player player, int spotAnimId) {
        if (spotAnimId == NO_ID) {
            return;
        }

        List<Integer> keysToRemove = new ArrayList<>();
        for (ActorSpotAnim spotAnim : player.getSpotAnims()) {
            if (spotAnim.getId() == spotAnimId) {
                keysToRemove.add((int) spotAnim.getHash());
            }
        }

        for (int key : keysToRemove) {
            player.removeSpotAnim(key);
        }
    }

    /**
     * Resolves the override animation for a given source animation ID.
     * Per-teleport settings take priority; if set to NONE the global setting is used.
     */
    private TeleportAnimation getSelectedForAnimation(int animationId) {
        TeleportAnimation source = TeleportAnimation.fromAnimationId(animationId);
        if (source == null) {
            return config.teleportAnimation();
        }

        TeleportAnimation perOverride;
        switch (source) {
            case STANDARD:
                perOverride = config.perOverrideNormal();
                break;
            case ANCIENT:
                perOverride = config.perOverrideAncient();
                break;
            case ARCEUUS:
                perOverride = config.perOverrideArceuus();
                break;
            case LUNAR:
                perOverride = config.perOverrideLunar();
                break;
            case TAB:
                perOverride = config.perOverrideTabs();
                break;
            case SCROLL:
                perOverride = config.perOverrideScrolls();
                break;
            case ECTOPHIAL:
                perOverride = config.perOverrideEctophial();
                break;
            case ARDOUGNE:
                perOverride = config.perOverrideArdougne();
                break;
            case DESERT_AMULET:
                perOverride = config.perOverrideDesertAmulet();
                break;
            case PENDENT_OF_ATES:
                perOverride = config.perOverridePendentOfAtes();
                break;
            case RING_OF_SHADOWS_WHITE:
            case RING_OF_SHADOWS_RED:
            case RING_OF_SHADOWS_BLACK:
            case RING_OF_SHADOWS_GRAY:
            case RING_OF_SHADOWS_ALL:
                perOverride = config.perOverrideRingOfShadows();
                break;
            case PHARAOHS_SCEPTRE:
                perOverride = config.perOverridePharaohsSceptre();
                break;
            case GIANTSOUL_AMULET:
                perOverride = config.perOverrideGiantsoulAmulet();
                break;
            default:
                return config.teleportAnimation();
        }

        return (perOverride != TeleportAnimation.NONE) ? perOverride : config.teleportAnimation();
    }

    /**
     * Plays a sound effect, de-duplicating within the same game tick.
     */
    private void playSoundOnce(int soundId) {
        if (soundId == NO_ID) {
            return;
        }

        int tick = client.getTickCount();
        if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick) {
            return;
        }

        lastPlayedSoundId = soundId;
        lastPlayedSoundTick = tick;
        client.playSoundEffect(soundId);
    }

    private static final class CustomTeleportIds {
        private static final CustomTeleportIds EMPTY = new CustomTeleportIds(NO_ID, NO_ID, NO_ID);

        private final int animationId;
        private final int graphicId;
        private final int soundId;

        private CustomTeleportIds(int animationId, int graphicId, int soundId) {
            this.animationId = animationId;
            this.graphicId = graphicId;
            this.soundId = soundId;
        }
    }
}
