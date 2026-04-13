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
  - Giantsoul Amulet teleport
  - Explorer's ring teleport - spawns the graphic under the player instead of the usual animation
  - Pharoh's Sceptre teleport
  - Ring of Shadows colour variants

## Default behavior
By default the plugin replaces all teleports with the Cowbell Amulet animation.

## Configuration
Open the plugin settings in RuneLite and configure:
- **Override All** — the global animation applied to all teleports.
- **Per Teleport** section — set a specific animation per teleport type; "None (Use Global)" falls back to the global setting
- **Custom Options** — Advanced users can specify custom animation, graphic and sound IDs for each teleport type, allowing for anything, to be used as a replacement.

## Notes
- This plugin is cosmetic only — it does not change teleport mechanics.

## Version - see changelog for changes
Current version 1.0.1
