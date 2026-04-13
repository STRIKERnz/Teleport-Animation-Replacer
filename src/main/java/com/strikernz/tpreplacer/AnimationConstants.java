package com.strikernz.tpreplacer;

/**
 * Animation, graphic, and sound-effect IDs for all supported teleport types.
 */
public final class AnimationConstants
{
	public static final int COWBELL_TELEPORT = 13811;
	public static final int COWBELL_TELEPORT_GRAPHIC = 3603;
	public static final int COWBELL_ARRIVAL_SOUND = 11286;

	public static final int STANDARD_AND_JEWELLERY_TELEPORT = 714;
	public static final int STANDARD_TELEPORT_GRAPHIC = 111;
	public static final int STANDARD_TELEPORT_SOUND = 200;

	public static final int ANCIENT_TELEPORT = 1979;
	public static final int ANCIENT_TELEPORT_GRAPHIC = 392;
	public static final int ANCIENT_TELEPORT_SOUND = 197;

	public static final int ARCEUUS_TELEPORT = 3865;
	public static final int ARCEUUS_TELEPORT_GRAPHIC = 1296;
	public static final int ARCEUUS_TELEPORT_SOUND = 200;

	public static final int LUNAR_TELEPORT = 1816;
	public static final int LUNAR_TELEPORT_GRAPHIC = 747;
	public static final int LUNAR_TELEPORT_SOUND = 200;

	public static final int TAB_TELEPORT = 4071;
	public static final int TAB_TELEPORT_GRAPHIC = 678;
	public static final int TAB_TELEPORT_SOUND = 965;

	public static final int TELEPORT_SCROLLS = 3864;
	public static final int TELEPORT_SCROLLS_GRAPHIC = 111;
	public static final int TELEPORT_SCROLLS_SOUND = 200;

	public static final int ECTOPHIAL_TELEPORT = 878;
	public static final int ECTOPHIAL_TELEPORT_GRAPHIC = 1273;
	public static final int ECTOPHIAL_TELEPORT_SOUND = 2401;

	public static final int ARDOUGNE_TELEPORT = 3872;
	public static final int ARDOUGNE_TELEPORT_GRAPHIC = 1237;
	public static final int ARDOUGNE_TELEPORT_SOUND = 200;

	public static final int DESERT_AMULET_TELEPORT = 9606;
	public static final int DESERT_AMULET_TELEPORT_GRAPHIC = 284;
	public static final int DESERT_AMULET_TELEPORT_SOUND = 200;

	public static final int GIANTSOUL_AMULET_TELEPORT = 12016;
	public static final int GIANTSOUL_AMULET_TELEPORT_GRAPHIC = 3226;
	public static final int GIANTSOUL_AMULET_TELEPORT_SOUND = 10193;

	public static final int EXPLORERS_RING_TELEPORT = 3869;
	public static final int EXPLORERS_RING_TELEPORT_GRAPHIC = 285;

	public static final int PHARAOHS_SCEPTRE_TELEPORT = 2881;
	public static final int PHARAOHS_SCEPTRE_TELEPORT_GRAPHIC = 715;
	public static final int PHARAOHS_SCEPTRE_TELEPORT_SOUND = 200;

	public static final int RING_OF_SHADOWS_TELEPORT = 10134;
	public static final int RING_OF_SHADOWS_WHITE_GRAPHIC = 2420;
	public static final int RING_OF_SHADOWS_RED_GRAPHIC= 2419;
	public static final int RING_OF_SHADOWS_BLACK_GRAPHIC= 2421;
	public static final int RING_OF_SHADOWS_GRAY_GRAPHIC= 2418;
	public static final int RING_OF_SHADOWS_ALL_GRAPHIC= 2417;
	public static final int RING_OF_SHADOWS_TELEPORT_SOUND = 200;



	private AnimationConstants() {}

	public static boolean isTeleportAnimation(int animationId)
	{
		return TeleportAnimation.fromAnimationId(animationId) != null;
	}
}
