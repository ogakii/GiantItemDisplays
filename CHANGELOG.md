# Changelog

## 1.0.4

- Made display animation smoother by updating every tick by default.
- Added Display interpolation through `settings.display-interpolation-duration`.
- Preserved the previous visual spin/bob speed while increasing animation update smoothness.

## 1.0.3

- Added `lang-en.yml` as a separate English template.
- Kept `lang.yml` as the default English language file for normal downloads.
- Renamed the Portuguese release asset to `lang-ptbr.yml` to avoid confusion.

## 1.0.2

- Added a Brazilian Portuguese language template as `lang-ptbr.yml`.
- Documented how to switch the server messages to Portuguese.

## 1.0.1

- Translated in-game messages, build output and internal logs to English.
- Kept Ogaki credits in English across the plugin and repository.

## 1.0.0

- Initial public release of GiantItemDisplays.
- Giant item visuals powered by Paper `ItemDisplay`.
- Invisible `Interaction` hitbox for reliable clicks.
- Optional fake physical collision using tracked barrier blocks.
- Per-display commands, permissions, scale, rotation, glow, spin and bobbing.
- YAML storage through `config.yml`, `lang.yml` and `displays.yml`.
- In-game credits for Ogaki with `/gid credits`.
