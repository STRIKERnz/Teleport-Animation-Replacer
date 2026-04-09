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

	@ConfigItem(keyName = "per_overrideExplorersRing", name = "Explorers Ring", description = "Per-teleport animation (None uses global setting)", position = 9, section = perSection)
	default TeleportAnimation perOverrideExplorersRing() { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overridePharaohsSceptre", name = "Pharaoh's Sceptre", description = "Per-teleport animation (None uses global setting)", position = 10, section = perSection)
	default TeleportAnimation perOverridePharaohsSceptre() { return TeleportAnimation.NONE; }

	@ConfigItem(keyName = "per_overrideGiantsoulAmulet", name = "Giantsoul Amulet", description = "Per-teleport animation (None uses global setting)", position = 11, section = perSection)
	default TeleportAnimation perOverrideGiantsoulAmulet() { return TeleportAnimation.NONE; }
}
