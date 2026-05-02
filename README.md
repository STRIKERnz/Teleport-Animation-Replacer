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
  - Pharoh's Sceptre teleport
  - Ring of Shadows colour variants
  - Pendent of Ates teleport

## Default behavior
By default, the plugin replaces all teleports with the Cowbell Amulet animation.

## Configuration
Open the plugin settings in RuneLite and configure:
- **Override All** — the global animation applied to all teleports.
- **Per Teleport** section — set a specific animation per teleport type; "None (Use Global)" falls back to the global setting
- **Custom Options** — Advanced users can specify custom animation, graphic and sound IDs for each teleport type, allowing for anything, to be used as a replacement.

## Notes
- This plugin is cosmetic only — it does not change teleport mechanics.

## Version history

# Changelog

All notable changes to this project will be documented here

## [1.0.4] - 2026-05-02
### Fixed
- Arrival animations, graphics, and sounds are now suppressed inside the (Corrupted) Gauntlet to prevent the character from getting stuck.

## [1.0.3] - 2026-04-28
### Added
- Pendent of Ates teleport preset (cast + arrival): animation, graphic, and sound IDs added and usable as an override.

### Changed
- Improved override suppression so original teleport sounds (including Pendent of Ates and Ring of Shadows) are muted when overridden.
- Fixed per-variant Ring of Shadows overrides so changing colour correctly updates the graphic even though the animation id is shared.


## [1.0.2] - 2026-03-20
- added Pharaoh's sceptre, Ring of Shadows and a Custom option

## [1.0.1] - 2026-03-19
### Fixed
- Cowbell teleport arrival replay: the cowbell teleport previously replayed the full cowbell animation on arrival, causing a duplicate animation; the arrival now only plays the arrival graphic and sound.

## [1.0 Release]
- Initial release on to plugin hub
