package com.strikernz.tpreplacer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.IntPredicate;

/**
 * Teleport animation presets that can be selected in the plugin configuration.
 */
@Getter
public enum TeleportAnimation {
    NONE(-1, -1, -1, "None (Use Global)"),
    CUSTOM(-1, -1, -1, "Custom (Use IDs)"),
    RANDOM(-1, -1, -1, "Random"),
    COWBELL(AnimationConstants.COWBELL_TELEPORT, AnimationConstants.COWBELL_TELEPORT_GRAPHIC, -1,
            "Cowbell Amulet"),
    STANDARD(AnimationConstants.STANDARD_AND_JEWELLERY_TELEPORT, AnimationConstants.STANDARD_TELEPORT_GRAPHIC,
            AnimationConstants.STANDARD_TELEPORT_SOUND, "Standard / Jewellery"),
    EXPLORERS_RING(AnimationConstants.EXPLORERS_RING_TELEPORT, AnimationConstants.EXPLORERS_RING_TELEPORT_GRAPHIC,
            AnimationConstants.EXPLORERS_RING_TELEPORT_SOUND, "Explorer's Ring"),
    ARDOUGNE_FARMING(AnimationConstants.ARDOUGNE_FARMING_TELEPORT,
            AnimationConstants.ARDOUGNE_FARMING_TELEPORT_GRAPHIC,
            AnimationConstants.ARDOUGNE_FARMING_TELEPORT_SOUND, "Ardougne Farming"),
    ROYAL_SEED_POD(AnimationConstants.ROYAL_SEED_POD_TELEPORT,
            AnimationConstants.ROYAL_SEED_POD_TELEPORT_GRAPHIC,
            AnimationConstants.ROYAL_SEED_POD_TELEPORT_SOUND, "Royal Seed Pod",
            AnimationConstants.ROYAL_SEED_POD_TELEPORT_ARRIVAL, AnimationConstants.ROYAL_SEED_POD_TELEPORT_ARRIVAL_GRAPHIC),
    ANCIENT(AnimationConstants.ANCIENT_TELEPORT, AnimationConstants.ANCIENT_TELEPORT_GRAPHIC,
            AnimationConstants.ANCIENT_TELEPORT_SOUND, "Ancient"),
    ARCEUUS(AnimationConstants.ARCEUUS_TELEPORT, AnimationConstants.ARCEUUS_TELEPORT_GRAPHIC,
            AnimationConstants.ARCEUUS_TELEPORT_SOUND, "Arceuus"),
    XERIC_TALISMAN(AnimationConstants.XERIC_TALISMAN_TELEPORT, AnimationConstants.XERIC_TALISMAN_TELEPORT_GRAPHIC,
            AnimationConstants.XERIC_TALISMAN_TELEPORT_SOUND, "Xeric's Talisman"),
    LUNAR(AnimationConstants.LUNAR_TELEPORT, AnimationConstants.LUNAR_TELEPORT_GRAPHIC,
            AnimationConstants.LUNAR_TELEPORT_SOUND, "Lunar"),
    TAB(AnimationConstants.TAB_TELEPORT, AnimationConstants.TAB_TELEPORT_GRAPHIC,
            AnimationConstants.TAB_TELEPORT_SOUND, "Tab"),
    SCROLL(AnimationConstants.TELEPORT_SCROLLS, AnimationConstants.TELEPORT_SCROLLS_GRAPHIC,
            AnimationConstants.TELEPORT_SCROLLS_SOUND, "Scroll"),
    ECTOPHIAL(AnimationConstants.ECTOPHIAL_TELEPORT, AnimationConstants.ECTOPHIAL_TELEPORT_GRAPHIC,
            AnimationConstants.ECTOPHIAL_TELEPORT_SOUND, "Ectophial"),
    ARDOUGNE(AnimationConstants.ARDOUGNE_TELEPORT, AnimationConstants.ARDOUGNE_TELEPORT_GRAPHIC,
            AnimationConstants.ARDOUGNE_TELEPORT_SOUND, "Ardougne Cape"),
    DESERT_AMULET(AnimationConstants.DESERT_AMULET_TELEPORT, AnimationConstants.DESERT_AMULET_TELEPORT_GRAPHIC,
            AnimationConstants.DESERT_AMULET_TELEPORT_SOUND, "Desert Amulet"),
    MORYTANIA_LEGS(AnimationConstants.MORYTANIA_LEGS_TELEPORT, AnimationConstants.MORYTANIA_LEGS_TELEPORT_GRAPHIC,
            AnimationConstants.MORYTANIA_LEGS_TELEPORT_SOUND, "Morytania Legs"),
    GIANTSOUL_AMULET(AnimationConstants.GIANTSOUL_AMULET_TELEPORT,
            AnimationConstants.GIANTSOUL_AMULET_TELEPORT_GRAPHIC,
            AnimationConstants.GIANTSOUL_AMULET_TELEPORT_SOUND, "Giantsoul Amulet"),
    PHARAOHS_SCEPTRE(AnimationConstants.PHARAOHS_SCEPTRE_TELEPORT,
            AnimationConstants.PHARAOHS_SCEPTRE_TELEPORT_GRAPHIC,
            AnimationConstants.PHARAOHS_SCEPTRE_TELEPORT_SOUND, "Pharaoh's Sceptre"),
    PENDENT_OF_ATES(AnimationConstants.PENDENT_OF_ATES_TELEPORT,
            AnimationConstants.PENDENT_OF_ATES_TELEPORT_GRAPHIC,
            AnimationConstants.PENDENT_OF_ATES_TELEPORT_SOUND, "Pendent of Ates",
            AnimationConstants.PENDENT_OF_ATES_TELEPORT_ARRIVAL, AnimationConstants.PENDENT_OF_ATES_TELEPORT_ARRIVAL_GRAPHIC),
    RING_OF_SHADOWS_WHITE(AnimationConstants.RING_OF_SHADOWS_TELEPORT,
            AnimationConstants.RING_OF_SHADOWS_WHITE_GRAPHIC,
            AnimationConstants.RING_OF_SHADOWS_TELEPORT_SOUND, "Ring of Shadows (White)"),
    RING_OF_SHADOWS_RED(AnimationConstants.RING_OF_SHADOWS_TELEPORT,
            AnimationConstants.RING_OF_SHADOWS_RED_GRAPHIC,
            AnimationConstants.RING_OF_SHADOWS_TELEPORT_SOUND, "Ring of Shadows (Red)"),
    RING_OF_SHADOWS_BLACK(AnimationConstants.RING_OF_SHADOWS_TELEPORT,
            AnimationConstants.RING_OF_SHADOWS_BLACK_GRAPHIC,
            AnimationConstants.RING_OF_SHADOWS_TELEPORT_SOUND, "Ring of Shadows (Black)"),
    RING_OF_SHADOWS_GRAY(AnimationConstants.RING_OF_SHADOWS_TELEPORT,
            AnimationConstants.RING_OF_SHADOWS_GRAY_GRAPHIC,
            AnimationConstants.RING_OF_SHADOWS_TELEPORT_SOUND, "Ring of Shadows (Grey)"),
    RING_OF_SHADOWS_ALL(AnimationConstants.RING_OF_SHADOWS_TELEPORT,
            AnimationConstants.RING_OF_SHADOWS_ALL_GRAPHIC,
            AnimationConstants.RING_OF_SHADOWS_TELEPORT_SOUND, "Ring of Shadows (All)"),
    VOLCANIC_MINE(AnimationConstants.VOLCANIC_MINE_TELEPORT, AnimationConstants.VOLCANIC_MINE_TELEPORT_GRAPHIC,
            AnimationConstants.VOLCANIC_MINE_TELEPORT_SOUND, "Volcanic Mine Tab",
            AnimationConstants.VOLCANIC_MINE_TELEPORT_ARRIVAL, -1),
    GHOMMALS_HILT_1(AnimationConstants.GHOMMALS_HILT_1_TELEPORT, AnimationConstants.GHOMMALS_HILT_1_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_1_TELEPORT_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 1",
            AnimationConstants.GHOMMALS_HILT_1_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_1_TELEPORT_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_1_TELEPORT_RED_GRAPHIC, AnimationConstants.GHOMMALS_HILT_1_TELEPORT_RED_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_2(AnimationConstants.GHOMMALS_HILT_2_TELEPORT, AnimationConstants.GHOMMALS_HILT_2_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_2_TELEPORT_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 2",
            AnimationConstants.GHOMMALS_HILT_2_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_2_TELEPORT_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_2_TELEPORT_RED_GRAPHIC, AnimationConstants.GHOMMALS_HILT_2_TELEPORT_RED_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_3(AnimationConstants.GHOMMALS_HILT_3_TELEPORT, AnimationConstants.GHOMMALS_HILT_3_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_3_TELEPORT_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 3",
            AnimationConstants.GHOMMALS_HILT_3_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_3_TELEPORT_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_3_TELEPORT_RED_GRAPHIC, AnimationConstants.GHOMMALS_HILT_3_TELEPORT_RED_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_4(AnimationConstants.GHOMMALS_HILT_4_TELEPORT, AnimationConstants.GHOMMALS_HILT_4_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_4_TELEPORT_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 4",
            AnimationConstants.GHOMMALS_HILT_4_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_4_TELEPORT_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_4_TELEPORT_RED_GRAPHIC, AnimationConstants.GHOMMALS_HILT_4_TELEPORT_RED_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_5(AnimationConstants.GHOMMALS_HILT_5_TELEPORT, AnimationConstants.GHOMMALS_HILT_5_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_5_TELEPORT_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 5",
            AnimationConstants.GHOMMALS_HILT_5_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_5_TELEPORT_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_5_TELEPORT_RED_GRAPHIC, AnimationConstants.GHOMMALS_HILT_5_TELEPORT_RED_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_6(AnimationConstants.GHOMMALS_HILT_6_TELEPORT, AnimationConstants.GHOMMALS_HILT_6_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_6_TELEPORT_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 6",
            AnimationConstants.GHOMMALS_HILT_6_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_6_TELEPORT_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_6_TELEPORT_RED_GRAPHIC, AnimationConstants.GHOMMALS_HILT_6_TELEPORT_RED_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_5_AVERNIC(AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT, AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 5 Avernic",
            AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_RED_GRAPHIC, AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_RED_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_6_AVERNIC(AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT, AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 6 Avernic",
            AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_RED_GRAPHIC, AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_RED_ARRIVAL_GRAPHIC),
    // Red hilt teleports (Mor Ul Rek) share the normal tier's animations; detected by graphic
    GHOMMALS_HILT_5_RED(AnimationConstants.GHOMMALS_HILT_5_TELEPORT, AnimationConstants.GHOMMALS_HILT_5_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_5_TELEPORT_RED_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 5 (Red)",
            AnimationConstants.GHOMMALS_HILT_5_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_5_TELEPORT_RED_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_5_TELEPORT_GRAPHIC, AnimationConstants.GHOMMALS_HILT_5_TELEPORT_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_6_RED(AnimationConstants.GHOMMALS_HILT_6_TELEPORT, AnimationConstants.GHOMMALS_HILT_6_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_6_TELEPORT_RED_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 6 (Red)",
            AnimationConstants.GHOMMALS_HILT_6_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_6_TELEPORT_RED_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_6_TELEPORT_GRAPHIC, AnimationConstants.GHOMMALS_HILT_6_TELEPORT_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_5_AVERNIC_RED(AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT, AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_RED_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 5 Avernic (Red)",
            AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_RED_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_GRAPHIC, AnimationConstants.GHOMMALS_HILT_5_AVERNIC_TELEPORT_ARRIVAL_GRAPHIC),
    GHOMMALS_HILT_6_AVERNIC_RED(AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT, AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_FEMALE, AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_RED_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_TELEPORT_SOUND, "Ghommal's Hilt 6 Avernic (Red)",
            AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_ARRIVAL, AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_RED_ARRIVAL_GRAPHIC,
            AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_GRAPHIC, AnimationConstants.GHOMMALS_HILT_6_AVERNIC_TELEPORT_ARRIVAL_GRAPHIC),
    QUETZAL(AnimationConstants.QUETZAL_TELEPORT, AnimationConstants.QUETZAL_TELEPORT_GRAPHIC,
            AnimationConstants.QUETZAL_TELEPORT_SOUND, "Quetzal");

    // Keep Random from favouring the hilt: only tiers 5 and 6 (normal and red) are picked
    private static final Set<TeleportAnimation> EXCLUDED_FROM_RANDOM = EnumSet.of(
            GHOMMALS_HILT_1, GHOMMALS_HILT_2, GHOMMALS_HILT_3, GHOMMALS_HILT_4,
            GHOMMALS_HILT_5_AVERNIC, GHOMMALS_HILT_6_AVERNIC,
            GHOMMALS_HILT_5_AVERNIC_RED, GHOMMALS_HILT_6_AVERNIC_RED);
    private static final Map<Integer, TeleportAnimation> BY_ANIMATION_ID = new HashMap<>();
    private static final Map<Integer, TeleportAnimation> BY_ARRIVAL_ANIMATION_ID = new HashMap<>();

    static {
        for (TeleportAnimation ta : values()) {
            // Quetzal is replacement-only: the real whistle teleport must not be detected as a source
            if (ta.animationId > 0 && ta != QUETZAL) {
                BY_ANIMATION_ID.putIfAbsent(ta.animationId, ta);
            }
            if (ta.femaleAnimationId > 0) {
                BY_ANIMATION_ID.putIfAbsent(ta.femaleAnimationId, ta);
            }
            if (ta.arrivalAnimationId > 0) {
                BY_ARRIVAL_ANIMATION_ID.putIfAbsent(ta.arrivalAnimationId, ta);
            }
        }

        // Multiple Ring of Shadows enums share the same animation id; explicitly map that id
        // to a sensible default source (white variant) so detection is consistent.
        BY_ANIMATION_ID.put(AnimationConstants.EXPLORERS_RING_TELEPORT, TeleportAnimation.EXPLORERS_RING);
        BY_ANIMATION_ID.put(AnimationConstants.ARDOUGNE_TELEPORT, TeleportAnimation.ARDOUGNE);
        BY_ANIMATION_ID.put(AnimationConstants.ARCEUUS_TELEPORT, TeleportAnimation.ARCEUUS);
        BY_ANIMATION_ID.put(AnimationConstants.RING_OF_SHADOWS_TELEPORT, TeleportAnimation.RING_OF_SHADOWS_WHITE);
    }

    private final int animationId;
    /**
     * Cast animation for female body types when it differs from {@link #animationId} ({@code -1} = same).
     */
    private final int femaleAnimationId;
    private final int graphicId;
    private final int soundId;
    private final String name;
    /**
     * Landing animation and graphic of a two-phase teleport ({@code -1} = none).
     */
    private final int arrivalAnimationId;
    private final int arrivalGraphicId;
    /**
     * Other colours of this teleport's graphics, suppressed along with the main ones.
     */
    private final int[] alternateGraphicIds;

    TeleportAnimation(int animationId, int graphicId, int soundId, String name) {
        this(animationId, graphicId, soundId, name, -1, -1);
    }

    TeleportAnimation(int animationId, int graphicId, int soundId, String name,
                      int arrivalAnimationId, int arrivalGraphicId, int... alternateGraphicIds) {
        this(animationId, -1, graphicId, soundId, name, arrivalAnimationId, arrivalGraphicId, alternateGraphicIds);
    }

    TeleportAnimation(int animationId, int femaleAnimationId, int graphicId, int soundId, String name,
                      int arrivalAnimationId, int arrivalGraphicId, int... alternateGraphicIds) {
        this.animationId = animationId;
        this.femaleAnimationId = femaleAnimationId;
        this.graphicId = graphicId;
        this.soundId = soundId;
        this.name = name;
        this.arrivalAnimationId = arrivalAnimationId;
        this.arrivalGraphicId = arrivalGraphicId;
        this.alternateGraphicIds = alternateGraphicIds;
    }

    public static TeleportAnimation fromAnimationId(int animationId) {
        return BY_ANIMATION_ID.get(animationId);
    }

    /**
     * Like {@link #fromAnimationId}, but prefers a preset sharing that animation whose graphic is showing.
     */
    public static TeleportAnimation fromAnimationId(int animationId, IntPredicate hasGraphic) {
        TeleportAnimation primary = BY_ANIMATION_ID.get(animationId);
        if (primary == null) {
            return null;
        }

        for (TeleportAnimation ta : values()) {
            if (ta != primary && (ta.animationId == animationId || ta.femaleAnimationId == animationId)
                    && ta.graphicId > 0 && hasGraphic.test(ta.graphicId)) {
                return ta;
            }
        }

        return primary;
    }

    public int getAnimationId(boolean female) {
        return female && femaleAnimationId != -1 ? femaleAnimationId : animationId;
    }

    public static TeleportAnimation fromArrivalAnimationId(int animationId) {
        return BY_ARRIVAL_ANIMATION_ID.get(animationId);
    }

    public boolean isTwoPhase() {
        return arrivalAnimationId != -1 || arrivalGraphicId != -1;
    }

    public static TeleportAnimation randomReplacement(Random random, TeleportAnimation source) {
        List<TeleportAnimation> choices = new ArrayList<>();
        for (TeleportAnimation ta : values()) {
            if (ta.isRandomChoice() && ta != source) {
                choices.add(ta);
            }
        }

        if (choices.isEmpty()) {
            for (TeleportAnimation ta : values()) {
                if (ta.isRandomChoice()) {
                    choices.add(ta);
                }
            }
        }

        return choices.get(random.nextInt(choices.size()));
    }

    private boolean isRandomChoice() {
        return this != NONE && this != CUSTOM && this != RANDOM && !EXCLUDED_FROM_RANDOM.contains(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
