# Changelog

## 1.0.7

- Added `/gid setdeluxemenu <id> <menu>` for DeluxeMenus compatibility.
- DeluxeMenus helper saves `dm open <menu> %player%` as a console command.
- Added `settings.command-dispatch-delay-ticks` so GUI-opening commands run one tick after the click event.
- Increased default Display interpolation duration to 6 for smoother rotation.

## 1.0.6

- Centered the invisible `Interaction` hitbox vertically around the display.
- Added `settings.center-interaction-hitbox`.
- Added a no-command-configured message when clicking a display without a command.
- Shows `command-error` when Bukkit rejects a configured click command.
- Prevented immediate duplicate click events from showing misleading cooldown messages.

## 1.0.5

- Added a fallback entity interaction listener so clicks on `Interaction` hitboxes are detected more reliably across Paper versions.
- Ignored off-hand interaction events to prevent accidental duplicate command execution.
- Normalized command values that start with `/` or `./` before dispatching them.
- Increased the default Display interpolation duration for smoother rotation.
- Automatically migrates older animation config values to smoother defaults.

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
