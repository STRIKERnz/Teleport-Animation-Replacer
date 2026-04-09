package com.strikernz.tpreplacer;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Teleport animation presets that can be selected in the plugin configuration.
 */
@Getter
@RequiredArgsConstructor
public enum TeleportAnimation
{
	NONE(-1, -1, -1, "None (Use Global)"),
	COWBELL(AnimationConstants.COWBELL_TELEPORT, AnimationConstants.COWBELL_TELEPORT_GRAPHIC, -1, "Cowbell Amulet"),
	STANDARD(AnimationConstants.STANDARD_AND_JEWELLERY_TELEPORT, AnimationConstants.STANDARD_TELEPORT_GRAPHIC, AnimationConstants.STANDARD_TELEPORT_SOUND, "Standard / Jewellery"),
	ANCIENT(AnimationConstants.ANCIENT_TELEPORT, AnimationConstants.ANCIENT_TELEPORT_GRAPHIC, AnimationConstants.ANCIENT_TELEPORT_SOUND, "Ancient"),
	ARCEUUS(AnimationConstants.ARCEUUS_TELEPORT, AnimationConstants.ARCEUUS_TELEPORT_GRAPHIC, AnimationConstants.ARCEUUS_TELEPORT_SOUND, "Arceuus"),
	LUNAR(AnimationConstants.LUNAR_TELEPORT, AnimationConstants.LUNAR_TELEPORT_GRAPHIC, AnimationConstants.LUNAR_TELEPORT_SOUND, "Lunar"),
	TAB(AnimationConstants.TAB_TELEPORT, AnimationConstants.TAB_TELEPORT_GRAPHIC, AnimationConstants.TAB_TELEPORT_SOUND, "Tab"),
	SCROLL(AnimationConstants.TELEPORT_SCROLLS, AnimationConstants.TELEPORT_SCROLLS_GRAPHIC, AnimationConstants.TELEPORT_SCROLLS_SOUND, "Scroll"),
	ECTOPHIAL(AnimationConstants.ECTOPHIAL_TELEPORT, AnimationConstants.ECTOPHIAL_TELEPORT_GRAPHIC, AnimationConstants.ECTOPHIAL_TELEPORT_SOUND, "Ectophial"),
	ARDOUGNE(AnimationConstants.ARDOUGNE_TELEPORT, AnimationConstants.ARDOUGNE_TELEPORT_GRAPHIC, AnimationConstants.ARDOUGNE_TELEPORT_SOUND, "Ardougne Cape"),
	DESERT_AMULET(AnimationConstants.DESERT_AMULET_TELEPORT, AnimationConstants.DESERT_AMULET_TELEPORT_GRAPHIC, AnimationConstants.DESERT_AMULET_TELEPORT_SOUND, "Desert Amulet"),
		GIANTSOUL_AMULET(AnimationConstants.GIANTSOUL_AMULET_TELEPORT, AnimationConstants.GIANTSOUL_AMULET_TELEPORT_GRAPHIC, AnimationConstants.GIANTSOUL_AMULET_TELEPORT_SOUND, "Giantsoul Amulet"),
		PHARAOHS_SCEPTRE(AnimationConstants.PHARAOHS_SCEPTRE_TELEPORT, AnimationConstants.PHARAOHS_SCEPTRE_TELEPORT_GRAPHIC, AnimationConstants.PHARAOHS_SCEPTRE_TELEPORT_SOUND, "Pharaoh's Sceptre"),
		EXPLORERS_RING(AnimationConstants.EXPLORERS_RING_TELEPORT, AnimationConstants.EXPLORERS_RING_TELEPORT_GRAPHIC, -1, "Explorers Ring");

	private static final Map<Integer, TeleportAnimation> BY_ANIMATION_ID = new HashMap<>();

	static
	{
		for (TeleportAnimation ta : values())
		{
			if (ta.animationId > 0)
			{
				BY_ANIMATION_ID.put(ta.animationId, ta);
			}
		}
	}

	private final int animationId;
	private final int graphicId;
	private final int soundId;
	private final String name;

	public static TeleportAnimation fromAnimationId(int animationId)
	{
		return BY_ANIMATION_ID.get(animationId);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
