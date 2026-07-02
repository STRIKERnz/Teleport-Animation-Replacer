package com.strikernz.tpreplacer;

import com.google.inject.Provides;
import net.runelite.api.ActorSpotAnim;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
    private static final int SCROLL_VISUAL_SUPPRESS_TICKS = 3;
    private static final int LOCAL_TILE_SIZE = 128;
    private static final int ARRIVAL_ANIMATION_RESET_TICKS = 1;
    private static final int EXPLORERS_RING_GRAPHIC_HEIGHT = 96;
    private static final int ARDOUGNE_FARMING_GRAPHIC_HEIGHT = 64;
    private static final int DESERT_AMULET_GRAPHIC_HEIGHT = 80;
    private static final int MORYTANIA_LEGS_GRAPHIC_HEIGHT = 96;

    /**
     * Known teleport sound IDs to matching presets, built once for fast sound suppression.
     */
    private static final Map<Integer, Set<TeleportAnimation>> TELEPORTS_BY_SOUND = new HashMap<>();
    private static final Map<TeleportAnimation, PerTeleportOverride> PER_TELEPORT_OVERRIDES =
            new EnumMap<>(TeleportAnimation.class);

    static {
        for (TeleportAnimation ta : TeleportAnimation.values()) {
            if (ta.getSoundId() != NO_ID) {
                TELEPORTS_BY_SOUND.computeIfAbsent(ta.getSoundId(), ignored -> new HashSet<>()).add(ta);
            }
        }

        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.STANDARD, config -> config.perOverrideNormal());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.EXPLORERS_RING, config -> config.perOverrideExplorersRing());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.ARDOUGNE_FARMING, config -> config.perOverrideArdougneFarming());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.ROYAL_SEED_POD, config -> config.perOverrideRoyalSeedPod());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.ANCIENT, config -> config.perOverrideAncient());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.ARCEUUS, config -> config.perOverrideArceuus());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.XERIC_TALISMAN, config -> config.perOverrideXericTalisman());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.LUNAR, config -> config.perOverrideLunar());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.TAB, config -> config.perOverrideTabs());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.SCROLL, config -> config.perOverrideScrolls());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.ECTOPHIAL, config -> config.perOverrideEctophial());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.ARDOUGNE, config -> config.perOverrideArdougne());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.DESERT_AMULET, config -> config.perOverrideDesertAmulet());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.MORYTANIA_LEGS, config -> config.perOverrideMorytaniaLegs());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.PENDENT_OF_ATES, config -> config.perOverridePendentOfAtes());
        putPerOverride(config -> config.perOverrideRingOfShadows(),
                TeleportAnimation.RING_OF_SHADOWS_WHITE,
                TeleportAnimation.RING_OF_SHADOWS_RED,
                TeleportAnimation.RING_OF_SHADOWS_BLACK,
                TeleportAnimation.RING_OF_SHADOWS_GRAY,
                TeleportAnimation.RING_OF_SHADOWS_ALL);
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.PHARAOHS_SCEPTRE, config -> config.perOverridePharaohsSceptre());
        PER_TELEPORT_OVERRIDES.put(TeleportAnimation.GIANTSOUL_AMULET, config -> config.perOverrideGiantsoulAmulet());
    }

    private static void putPerOverride(PerTeleportOverride override, TeleportAnimation... sources) {
        for (TeleportAnimation source : sources) {
            PER_TELEPORT_OVERRIDES.put(source, override);
        }
    }

    /**
     * Original-sound-id to tick at which the mute expires (inclusive).
     */
    private final Random random = new Random();
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
    private int scrollVisualSuppressUntilTick = NO_ID;
    private int ignoredReplacementAnimationId = NO_ID;
    private int ignoredReplacementAnimationTick = NO_ID;

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

        if (shouldIgnoreReplacementAnimation(animationId)) {
            return;
        }

        if (shouldSuppressScrollVisual() && animationId == AnimationConstants.TELEPORT_SCROLLS) {
            clearScrollAnimation(player);
            return;
        }

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

        if (animationId == AnimationConstants.ROYAL_SEED_POD_TELEPORT_ARRIVAL) {
            TeleportAnimation seedPodSelected = getSelectedForAnimation(AnimationConstants.ROYAL_SEED_POD_TELEPORT);
            if (seedPodSelected != TeleportAnimation.NONE && seedPodSelected != TeleportAnimation.ROYAL_SEED_POD) {
                player.setAnimation(NO_ID);
                return;
            }
        }

        TeleportAnimation original = getSourceForPlayerAnimation(player, animationId);
        if (original == null) {
            return;
        }

        TeleportAnimation selected = getSelectedForSource(original);

        // NONE means "don't override"
        if (selected == TeleportAnimation.NONE) {
            return;
        }

        if (selected == original) {
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

        int originalSound = original.getSoundId();
        if (originalSound != NO_ID) {
            mutedSoundUntilTick.put(originalSound, client.getTickCount() + ORIGINAL_SOUND_MUTE_TICKS);
        }

        suppressOriginalGraphics(player, original, selectedGraphicId);
        suppressSourceAnimationGraphic(player, original, selectedAnimationId);

        clearPendingArrival();

        // Cowbell has a special two-phase animation (cast + landing)
        if (selected == TeleportAnimation.COWBELL) {
            // Cowbell landing is graphic/sound-only. Forcing an arrival animation can leave
            // the local actor in a client-only pose after the player has already moved.
            startTwoPhaseTeleport(player,
                    AnimationConstants.COWBELL_TELEPORT,
                    AnimationConstants.COWBELL_TELEPORT_GRAPHIC,
                    NO_ID,
                    AnimationConstants.COWBELL_TELEPORT_GRAPHIC,
                    AnimationConstants.COWBELL_ARRIVAL_SOUND,
                    ARRIVAL_SOUND_DELAY_TICKS);
            return;
        }

        // Pendent of Ates: two-phase teleport with a specific arrival animation/graphic and same sound on arrival
        if (selected == TeleportAnimation.PENDENT_OF_ATES) {
            // Configure arrival specifics for pendent — play sound immediately on landing (0 tick delay)
            startTwoPhaseTeleport(player,
                    AnimationConstants.PENDENT_OF_ATES_TELEPORT,
                    AnimationConstants.PENDENT_OF_ATES_TELEPORT_GRAPHIC,
                    AnimationConstants.PENDENT_OF_ATES_TELEPORT_ARRIVAL,
                    AnimationConstants.PENDENT_OF_ATES_TELEPORT_ARRIVAL_GRAPHIC,
                    AnimationConstants.PENDENT_OF_ATES_TELEPORT_SOUND,
                    0);

            // Play the initial teleport sound immediately for the pendent cast
            if (arrivalSoundId != NO_ID) {
                playSoundOnce(arrivalSoundId);
            }

            return;
        }

        if (selected == TeleportAnimation.ROYAL_SEED_POD) {
            startTwoPhaseTeleport(player,
                    AnimationConstants.ROYAL_SEED_POD_TELEPORT,
                    AnimationConstants.ROYAL_SEED_POD_TELEPORT_GRAPHIC,
                    AnimationConstants.ROYAL_SEED_POD_TELEPORT_ARRIVAL,
                    AnimationConstants.ROYAL_SEED_POD_TELEPORT_ARRIVAL_GRAPHIC,
                    NO_ID,
                    0);
            return;
        }

        // Handle custom override (uses numeric IDs from single comma-separated config field)
        if (selected == TeleportAnimation.CUSTOM) {
            applyDirectReplacement(player, customIds.animationId, customIds.graphicId, customIds.soundId);
            return;
        }

        // Generic override path for enum-based presets
        applyDirectReplacement(player, selected.getAnimationId(), selected.getGraphicId(), selected.getSoundId());
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player player = client.getLocalPlayer();
        if (player == null) {
            return;
        }

        int tick = client.getTickCount();
        mutedSoundUntilTick.values().removeIf(expire -> expire < tick);
        suppressedGraphicUntilTick.values().removeIf(expire -> expire < tick);
        removeSuppressedSpotAnims(player);
        if (scrollVisualSuppressUntilTick < tick) {
            scrollVisualSuppressUntilTick = NO_ID;
        }
        expireIgnoredReplacementAnimation(tick);
        if (shouldSuppressScrollVisual() && player.getAnimation() == AnimationConstants.TELEPORT_SCROLLS) {
            clearScrollAnimation(player);
        }

        updateArrivalAnimationReset(player);

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
        if (isPluginPlayedSound(soundId, tick)) {
            return;
        }

        // Mute map only applies to sounds sourced to the local player
        if (event.getSource() == client.getLocalPlayer() && shouldMuteSound(soundId, tick)) {
            event.consume();
            return;
        }

        if (shouldSuppressKnownTeleportSound(soundId, client.getLocalPlayer())) {
            event.consume();
        }
    }

    @Subscribe
    public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event) {
        int soundId = event.getSoundId();
        int tick = client.getTickCount();

        // Don't suppress a sound the plugin itself just triggered
        if (isPluginPlayedSound(soundId, tick)) {
            return;
        }

        // First, honor explicit mutes we added when replacing teleports. This avoids races where
        // area sounds arrive before other logic and ensures replaced teleport sounds are consumed.
        if (shouldMuteSound(soundId, tick)) {
            event.consume();
            return;
        }

        if (shouldSuppressKnownTeleportSound(soundId, client.getLocalPlayer())) {
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

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        Player player = client.getLocalPlayer();
        if (player == null) {
            return;
        }

        int graphicId = event.getGraphicsObject().getId();
        if ((isSuppressedGraphic(graphicId) || shouldSuppressScrollVisual())
                && isNearLocalPlayer(player, event.getGraphicsObject().getLocation())) {
            event.getGraphicsObject().setFinished(true);
        }
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
        scrollVisualSuppressUntilTick = NO_ID;
        clearIgnoredReplacementAnimation();
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
        playReplacementAnimation(player, arrivalAnimationId);
        activeArrivalAnimationId = arrivalAnimationId;
        arrivalAnimationResetTicksRemaining = arrivalAnimationId == NO_ID
                ? NO_ID
                : ARRIVAL_ANIMATION_RESET_TICKS;

        playSpotAnim(player, arrivalGraphicId);

        arrivalSoundTicksRemaining = arrivalSoundId == NO_ID ? NO_ID : arrivalSoundDelay;
    }

    private void startTwoPhaseTeleport(Player player, int castAnimationId, int castGraphicId,
                                       int landingAnimationId, int landingGraphicId,
                                       int landingSoundId, int landingSoundDelay) {
        teleporting = true;
        playReplacementAnimation(player, castAnimationId);
        playSpotAnim(player, castGraphicId);

        arrivalAnimationId = landingAnimationId;
        arrivalGraphicId = landingGraphicId;
        arrivalSoundId = landingSoundId;
        arrivalSoundDelay = landingSoundDelay;
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

    private void applyDirectReplacement(Player player, int animationId, int graphicId, int soundId) {
        playReplacementAnimation(player, animationId);
        playSpotAnim(player, graphicId);
        playSoundOnce(soundId);
    }

    private void playReplacementAnimation(Player player, int animationId) {
        if (animationId == NO_ID) {
            return;
        }

        ignoreNextReplacementAnimation(animationId);
        player.setAnimation(animationId);
    }

    private void ignoreNextReplacementAnimation(int animationId) {
        if (animationId == NO_ID) {
            return;
        }

        ignoredReplacementAnimationId = animationId;
        ignoredReplacementAnimationTick = client.getTickCount();
    }

    private boolean shouldIgnoreReplacementAnimation(int animationId) {
        if (animationId == NO_ID || animationId != ignoredReplacementAnimationId) {
            return false;
        }

        clearIgnoredReplacementAnimation();
        return true;
    }

    private void expireIgnoredReplacementAnimation(int tick) {
        if (ignoredReplacementAnimationTick != NO_ID && ignoredReplacementAnimationTick < tick - 1) {
            clearIgnoredReplacementAnimation();
        }
    }

    private void clearIgnoredReplacementAnimation() {
        ignoredReplacementAnimationId = NO_ID;
        ignoredReplacementAnimationTick = NO_ID;
    }

    private boolean shouldSuppressKnownTeleportSound(int soundId, Player player) {
        if (player == null) {
            return false;
        }

        TeleportAnimation activeSource = getSourceForPlayerAnimation(player, player.getAnimation());
        if (activeSource == null) {
            return false;
        }

        Set<TeleportAnimation> sourcesWithSound = TELEPORTS_BY_SOUND.get(soundId);
        return sourcesWithSound != null
                && sourcesWithSound.contains(activeSource)
                && isOverridden(activeSource);
    }

    private boolean isPluginPlayedSound(int soundId, int tick) {
        return soundId == lastPlayedSoundId && tick == lastPlayedSoundTick;
    }

    private boolean shouldMuteSound(int soundId, int tick) {
        return mutedSoundUntilTick.getOrDefault(soundId, NO_ID) >= tick;
    }

    private void playSpotAnim(Player player, int spotAnimId) {
        if (spotAnimId != NO_ID) {
            player.createSpotAnim(spotAnimId, spotAnimId, getSpotAnimHeight(spotAnimId), 0);
        }
    }

    private int getSpotAnimHeight(int spotAnimId) {
        if (spotAnimId == AnimationConstants.EXPLORERS_RING_TELEPORT_GRAPHIC) {
            return EXPLORERS_RING_GRAPHIC_HEIGHT;
        }

        if (spotAnimId == AnimationConstants.ARDOUGNE_FARMING_TELEPORT_GRAPHIC) {
            return ARDOUGNE_FARMING_GRAPHIC_HEIGHT;
        }

        if (spotAnimId == AnimationConstants.DESERT_AMULET_TELEPORT_GRAPHIC) {
            return DESERT_AMULET_GRAPHIC_HEIGHT;
        }

        if (spotAnimId == AnimationConstants.MORYTANIA_LEGS_TELEPORT_GRAPHIC) {
            return MORYTANIA_LEGS_GRAPHIC_HEIGHT;
        }

        return 0;
    }

    private void suppressOriginalGraphics(Player player, TeleportAnimation original, int replacementGraphicId) {
        int originalGraphicId = original.getGraphicId();
        if (originalGraphicId == NO_ID || originalGraphicId == replacementGraphicId) {
            return;
        }

        suppressedGraphicUntilTick.put(originalGraphicId, client.getTickCount() + ORIGINAL_GRAPHIC_SUPPRESS_TICKS);
        removeSpotAnim(player, originalGraphicId);
    }

    private void suppressSourceAnimationGraphic(Player player, TeleportAnimation original, int replacementAnimationId) {
        if (original != TeleportAnimation.SCROLL || replacementAnimationId == original.getAnimationId()) {
            return;
        }

        scrollVisualSuppressUntilTick = client.getTickCount() + SCROLL_VISUAL_SUPPRESS_TICKS;
        clearScrollAnimation(player);
    }

    private boolean shouldSuppressScrollVisual() {
        return scrollVisualSuppressUntilTick >= client.getTickCount();
    }

    private boolean isSuppressedGraphic(int graphicId) {
        return suppressedGraphicUntilTick.getOrDefault(graphicId, NO_ID) >= client.getTickCount();
    }

    private boolean isNearLocalPlayer(Player player, LocalPoint graphicLocation) {
        LocalPoint playerLocation = player.getLocalLocation();
        return playerLocation != null
                && graphicLocation != null
                && playerLocation.distanceTo(graphicLocation) <= LOCAL_TILE_SIZE;
    }

    private void clearScrollAnimation(Player player) {
        player.setAnimation(NO_ID);
        player.setAnimationFrame(0);
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
        return getSelectedForSource(TeleportAnimation.fromAnimationId(animationId));
    }

    private TeleportAnimation getSourceForPlayerAnimation(Player player, int animationId) {
        if (player.hasSpotAnim(AnimationConstants.DESERT_AMULET_TELEPORT_GRAPHIC)) {
            return TeleportAnimation.DESERT_AMULET;
        }

        if (animationId == AnimationConstants.XERIC_TALISMAN_TELEPORT
                && player.hasSpotAnim(AnimationConstants.XERIC_TALISMAN_TELEPORT_GRAPHIC)) {
            return TeleportAnimation.XERIC_TALISMAN;
        }

        if (animationId == AnimationConstants.ARDOUGNE_FARMING_TELEPORT
                && player.hasSpotAnim(AnimationConstants.ARDOUGNE_FARMING_TELEPORT_GRAPHIC)) {
            return TeleportAnimation.ARDOUGNE_FARMING;
        }

        if (animationId == AnimationConstants.EXPLORERS_RING_TELEPORT
                && player.hasSpotAnim(AnimationConstants.EXPLORERS_RING_TELEPORT_GRAPHIC)) {
            return TeleportAnimation.EXPLORERS_RING;
        }

        if (animationId == AnimationConstants.ANCIENT_TELEPORT) {
            return player.hasSpotAnim(AnimationConstants.ANCIENT_TELEPORT_GRAPHIC)
                    ? TeleportAnimation.ANCIENT
                    : null;
        }

        return TeleportAnimation.fromAnimationId(animationId);
    }

    private TeleportAnimation getSelectedForSource(TeleportAnimation source) {
        TeleportAnimation perOverride = getPerOverrideForSource(source);
        TeleportAnimation selected = (perOverride != TeleportAnimation.NONE) ? perOverride : config.teleportAnimation();
        return resolveRandomSelection(selected, source);
    }

    private TeleportAnimation getPerOverrideForSource(TeleportAnimation source) {
        PerTeleportOverride override = source == null ? null : PER_TELEPORT_OVERRIDES.get(source);
        return override == null ? TeleportAnimation.NONE : override.get(config);
    }

    private TeleportAnimation resolveRandomSelection(TeleportAnimation selected, TeleportAnimation source) {
        if (selected != TeleportAnimation.RANDOM) {
            return selected;
        }

        return TeleportAnimation.randomReplacement(random, source);
    }

    private boolean isOverridden(TeleportAnimation source) {
        TeleportAnimation selected = getSelectedForSource(source);
        return selected != TeleportAnimation.NONE && selected != source;
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

    private interface PerTeleportOverride {
        TeleportAnimation get(TpreplacerConfig config);
    }
}
