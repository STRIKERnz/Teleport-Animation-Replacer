# Teleport Animation Replacer
A RuneLite plugin that replaces teleport animations with an alternative animation (default: the Cowbell Amulet teleport) and provides fine-grained configuration over which teleports are affected.

## Features
- Replace teleport animations with a selected preset animation (default: Cowbell Amulet).
- Per-teleport-type overrides so you can choose exactly which animation each teleport uses:
  - Normal spellbook teleports and jewellery teleports (rings/amulets)
  - Ancient spellbook teleports
  - Arceuus spellbook teleports
  - Lunar spellbook teleports
  - Teleport tabs
  - Teleport scrolls
  - Ectophial teleport
  - Ardougne Cape teleport
  - Desert Amulet teleport
  - Cowbell Amulet teleport
- Option to mute the original teleport sound when an override occurs (prevents double sounds).

## Default behavior
By default the plugin replaces all teleports with the Cowbell Amulet animation with mute enabled.

## Configuration
Open the plugin settings in RuneLite and configure:
- **Override All** — the global animation applied to all teleports (Cowbell Amulet, Standard/Jewellery, Ancient, Arceuus, Lunar, Tab, Scroll, Ectophial, Ardougne Cape, Desert Amulet)
- **Mute Teleport Sound** — suppresses the original teleport sound when the plugin overrides the animation
- **Per Teleport** section — set a specific animation per teleport type; "None (Use Global)" falls back to the global setting

## Notes
- This plugin is cosmetic only — it does not change teleport mechanics.
- If you hear overlapping sounds, make sure **Mute Teleport Sound** is enabled.
