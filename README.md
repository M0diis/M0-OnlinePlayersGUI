<!-- Variables -->

[resourceId]: 86813

[banner]: https://i.imgur.com/IufJw5D.png
[ratingImage]: https://img.shields.io/badge/dynamic/json.svg?color=brightgreen&label=rating&query=%24.rating.average&suffix=%20%2F%205&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F86813
[buildImage]: https://github.com/M0diis/M0-OnlinePlayersGUI/actions/workflows/gradle-j21.yml/badge.svg
[releaseImage]: https://img.shields.io/github/v/release/M0diis/M0-OnlinePlayersGUI.svg?label=github%20release
[downloadsImage]: https://img.shields.io/badge/dynamic/json.svg?color=brightgreen&label=downloads%20%28spigotmc.org%29&query=%24.downloads&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F86813
[licenseImage]: https://img.shields.io/github/license/M0diis/M0-OnlinePlayersGUI.svg

<!-- End of variables block -->

![build][buildImage] ![release][releaseImage] ![license][licenseImage]  
![downloads][downloadsImage] ![rating][ratingImage]

![Banner][banner]

## M0-OnlinePlayersGUI
A simple minecraft Online Player GUI plugin.  

Commands and permissions can be found in [/src/main/resources/plugin.yml](https://github.com/M0diis/M0-OnlinePlayersGUI/blob/main/src/main/resources/plugin.yml).

### Building

To build the plugin you need JDK 8 or higher and Gradle installed on your system.

Clone the repository or download the source code from releases.
Run `gradlew shadowjar` to build the jar.
The jar will be created in `/build/libs/` folder.

```
git clone https://github.com/M0diis/M0-OnlinePlayersGUI.git
cd M0-OnlinePlayersGUI
gradlew shadowjar
```

### Configuration

```yaml

messages:
  reload: '&2Configuration has been reloaded.'
  no-permission: '&2You do not have permission to this command.'
  no-permission-conditional: '&2You do not have permission to view this GUI'
  toggle-visibility: '&2You have toggled your visibility in Online GUI.'

# Whether to hide buttons if there are not enough players
# to fill up more than one page
buttons-always-visible: false
# Hook to EssentialsX which will hide players
# in the GUI if they are vanished
essentialsx-hook: false
# Conditional display
# It will check whether the placeholder applies (is true) for the
# player and if not, it will exclude the player from the GUI
# https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders
condition:
  required: false
  # You can also use ==, !=, <, >, <=, >= for placeholders that return numbers
  # Ex.: %player_health% == 20
  placeholder: "%checkitem_mat:DIRT%" # Requires CheckItem PAPI extension
  permission:
    required: false
    node: "permission.to.include.player"

player-display-material: 'PLAYER_HEAD'

previous-button:
  material: ENCHANTED_BOOK
  name: '&cPrevious Page'
  slot: 21
  lore:
    - '&7Click to open previous page.'

next-button:
  material: ENCHANTED_BOOK
  name: '&aNext Page'
  slot: 23
  lore:
    - '&7Click to open next page.'
# Player display configuration
# Every section supports placeholders
player-display:
  name: '&6%player_name%'
  lore:
    - '&2Right click to &ateleport&2 to the player.'
    - '&2Left click to &asay hi&2.'
  commands:
    left-click:
      - '[PLAYER] tp %player_name%'
    right-click:
      - '[PLAYER] msg %player_name% Hello, my name is %sender_name%.'
      - '[CONSOLE] msg %player_name% Hello! '
      - '[CLOSE]' # This will close the GUI.
    middle-click:
      - '[CLOSE]'

# GUI Slots:
# --------------------------
# 0  1  2  3  4  5  6  7  8
# 9  10 11 12 13 14 15 16 17
# 18 19 20 21 22 23 24 25 26
# 27 28 29 22 31 32 33 34 35
# 36 37 38 39 40 41 42 43 44
# 45 46 47 48 49 50 51 52 53
# --------------------------

# Size should be in increments of 9 and not
# lower than 9 or higher than 54
# 9 slots is one row
gui:
  size: 27
  title: '&2Online Players'
  update-on:
    join: true
    leave: true

# Custom Items
# These items will be displayed in the same row as
# the previous and next buttons are
custom-items:
  '1':
    material: RED_STAINED_GLASS_PANE
    slots:
      start: 0
      end: 8
    name: '&r'
    lore: []
    commands:
      left-click:
        - '[CLOSE]'
      right-click:
        - '[CLOSE]'
      middle-click:
        - '[CLOSE]'
  '2':
    material: RED_STAINED_GLASS_PANE
    slots:
      - 9
      - 17
      - 18
      - 19
      - 20
      - 22
      - 24
      - 25
      - 26
    name: '&r'
    lore: []

debug: false
```

### Spigot

You can find the resource on spigot:  
https://www.spigotmc.org/resources/m0-onlineplayersgui.86813

### Dev Builds

You can find all the developer builds [here](https://github.com/M0diis/M0-OnlinePlayersGUI/actions) under the artifacts section.

### Links:

- [Spigot](https://www.spigotmc.org/)
- [PaperMC](https://papermc.io/)
