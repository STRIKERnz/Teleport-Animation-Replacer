# Teleport Animation Replacer
A RuneLite plugin that replaces teleport animations with an alternative animation (default: the Cowbell Amulet teleport) and provides fine-grained configuration over which teleports are affected.

## Features
- Replace teleport animations with a selected preset animation (default: Cowbell Amulet).
- Choose **Random** globally or per teleport type to pick a replacement preset at random each time.
- Per-teleport-type overrides so you can choose exactly which animation each teleport uses:
  - Normal spellbook teleports and jewellery teleports (rings/amulets)
  - Explorer's Ring teleport
  - Ardougne Farming teleport
  - Royal Seed Pod teleport
  - Ancient spellbook teleports
  - Arceuus spellbook teleports
  - Xeric's Talisman teleport
  - Lunar spellbook teleports
  - Teleport tabs
  - Teleport scrolls
  - Ectophial teleport
  - Ardougne Cape teleport
  - Desert Amulet teleport
  - Morytania Legs teleport
  - Giantsoul Amulet teleport
  - Pharaoh's Sceptre teleport
  - Ring of Shadows colour variants
  - Pendent of Ates teleport

## Default behavior
By default, the plugin replaces all teleports with the Cowbell Amulet animation.

## Configuration
Open the plugin settings in RuneLite and configure:
- **Override All** — the global animation applied to all teleports.
- **Per Teleport** section — set a specific animation per teleport type; "None (Use Global)" falls back to the global setting
- **Random** — available in both global and per-teleport selectors; picks from the built-in presets and skips None, Custom, and Random itself.
- **Custom Options** — Advanced users can specify custom animation, graphic and sound IDs for each teleport type, allowing for anything, to be used as a replacement.

## Notes
- This plugin is cosmetic only — it does not change teleport mechanics.

## Version history

# Changelog

All notable changes to this project will be documented here


## [1.0.9] - 2026-07-03
### Fixed
- Corrected the Desert Amulet teleport animation ID and detection so its original graphic is suppressed when overridden.

### Added
- Morytania Legs teleport preset and per-teleport override support.

## [1.0.8] - 2026-06-26
### Fixed
- Prevented Ancient teleport overrides from triggering on non-teleport animations that share animation ID unless the Ancient teleport graphic is present.
- Improved original graphic suppression by also finishing matching graphics objects near the local player.

### Changed
- Tuned the Desert Amulet replacement graphic height.
- Cleaned up teleport override selection and two-phase teleport setup code.

## [1.0.7] - 2026-05-26
### Added
- Random option for the global override and per-teleport overrides.
- Xeric's Talisman per-teleport override setting.
- Explorer's Ring per-teleport override setting.
- Ardougne Farming per-teleport override setting.
- Royal Seed Pod per-teleport override setting.

### Fixed
- Prevented shared teleport sound IDs from muting unrelated sounds, such as drinking sounds.

## [1.0.6] - 2026-05-06
### Fixed
- graphic suppression handling for teleport animations to prevent visual overlap

## [1.0.5] - 2026-05-04
### Fixed
- Prevented client-only arrival animations from leaving the local player in a stuck visual state after teleporting.
- Replaced deprecated graphic APIs with RuneLite spot animation APIs.

### Changed
- Cleaned up teleport override state handling, custom ID parsing, and sound suppression lookups.
- Reformatted config and preset declarations for easier maintenance.

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
