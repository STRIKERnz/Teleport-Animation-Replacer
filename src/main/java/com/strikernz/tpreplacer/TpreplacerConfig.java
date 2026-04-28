package com.strikernz.tpreplacer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("cowbell")
public interface TpreplacerConfig extends Config
{
	@ConfigSection(
		name = "All",
		description = "Global override applied to all teleports unless overridden per-teleport",
		position = 0
	)
	String allSection = "all";

	@ConfigSection(name = "Per Teleport",
		description = "Per-teleport overrides. Set to 'None (Use Global)' to fall back to the global setting",
		position = 1)
	String perSection = "per";

	@ConfigSection(name = "Custom",
		description = "Custom IDs for testing (animation, graphic, sound)",
			position = 2,
			closedByDefault = true)
	String customSection = "custom";


	// ---- Global ----

	@ConfigItem(
		keyName = "overrideAnimationType",
		name = "Override all",
		description = "The global teleport animation applied to all teleports",
		position = 0,
		section = allSection
	)
	default TeleportAnimation teleportAnimation()
	{
		return TeleportAnimation.COWBELL;
	}

	@ConfigItem(
		keyName = "customIds",
		name = "Custom IDs",
		description = "Comma-separated animation,graphic,sound ids to use when 'Custom' is selected. Use -1 to disable an entry (e.g. 714,111,200)",
		position = 0,
		section = customSection
	)
	default String customIds()
	{
		return "-1,-1,-1";
	}


	// ---- Per Teleport ----

	@ConfigItem(keyName = "per_overrideNormal",       name = "Normal and jewellery", description = "Per-teleport animation (None uses global setting)", position = 0, section = perSection)
	default TeleportAnimation perOverrideNormal()       { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideAncient",      name = "Ancient",              description = "Per-teleport animation (None uses global setting)", position = 1, section = perSection)
	default TeleportAnimation perOverrideAncient()      { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideArceuus",      name = "Arceuus",              description = "Per-teleport animation (None uses global setting)", position = 2, section = perSection)
	default TeleportAnimation perOverrideArceuus()      { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideLunar",        name = "Lunar",                description = "Per-teleport animation (None uses global setting)", position = 3, section = perSection)
	default TeleportAnimation perOverrideLunar()        { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideTabs",         name = "Tabs",                 description = "Per-teleport animation (None uses global setting)", position = 4, section = perSection)
	default TeleportAnimation perOverrideTabs()         { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideScrolls",      name = "Scrolls",              description = "Per-teleport animation (None uses global setting)", position = 5, section = perSection)
	default TeleportAnimation perOverrideScrolls()      { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideEctophial",    name = "Ectophial",            description = "Per-teleport animation (None uses global setting)", position = 6, section = perSection)
	default TeleportAnimation perOverrideEctophial()    { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideArdougne",     name = "Ardougne Cape",        description = "Per-teleport animation (None uses global setting)", position = 7, section = perSection)
	default TeleportAnimation perOverrideArdougne()     { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideDesertAmulet", name = "Desert Amulet",        description = "Per-teleport animation (None uses global setting)", position = 8, section = perSection)
	default TeleportAnimation perOverrideDesertAmulet() { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overridePendentOfAtes", name = "Pendent of Ates", description = "Per-teleport animation (None uses global setting)", position = 9, section = perSection)
	default TeleportAnimation perOverridePendentOfAtes() { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideRingOfShadows", name = "Ring of Shadows", description = "Per-teleport animation (None uses global setting)", position = 10, section = perSection)
	default TeleportAnimation perOverrideRingOfShadows() { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overridePharaohsSceptre", name = "Pharaoh's Sceptre", description = "Per-teleport animation (None uses global setting)", position = 11, section = perSection)
	default TeleportAnimation perOverridePharaohsSceptre() { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideGiantsoulAmulet", name = "Giantsoul Amulet", description = "Per-teleport animation (None uses global setting)", position = 12, section = perSection)
	default TeleportAnimation perOverrideGiantsoulAmulet() { return TeleportAnimation.NONE; }
}
