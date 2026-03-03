# Teleport Animation Replacer
A RuneLite plugin that replaces many teleport animations with an alternative animation (default: the Cowbell Amulet teleport) and provides fine-grained configuration over which teleports are affected.

## Features
- Replace teleport animations with a selected animation (default: Cowbell Amulet).
- Per-teleport-type toggles so you can choose exactly which teleports to override:
  - Normal spellbook teleports and jewellery teleports (rings/amulets)
  - Ancient spellbook teleports
  - Arceuus spellbook teleports
  - Lunar spellbook teleports
  - Teleport tabs
  - Teleport scrolls
  - Ectophial teleport
  - Ardougne cape teleport
  - Desert Amulet teleport
- Option to mute the original teleport sound when an override occurs (prevents double sounds).

## Default behavior
- By default the plugin is enabled and set to replace teleports with the Cowbell Amulet animation. mute is enabled by default.

## Configuration
Open the plugin settings in RuneLite and configure:
- "Override With" — pick which animation to use (None, Cowbell Amulet, Standard/Jewellery, Ancient, etc.)
- Individual toggles for each teleport type (Normal/Jewellery, Ancient, Arceuus, Lunar, Tabs, Scrolls, Ectophial, Ardougne, Desert Amulet)
- "Mute Teleport Sound" — suppresses the original teleport sound when the plugin overrides the animation

## Notes
- This plugin is intended for cosmetic changes only. It does not change teleport mechanics.
- If you see overlapping sounds, enable the "Mute Teleport Sound" option in the plugin settings.
