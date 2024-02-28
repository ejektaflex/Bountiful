# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as closely as it can.

## [7.0.1] for 1.20.4 - 2024-02-28

### Fixed
- Fixed a crash on startup when using Fabric, caused by an unlinked refmap.

## [7.0.0] for 1.20.4 - 2024-02-27

7.0.0 is a big immersion and quality of life update for Bountiful, adding new ways for the Bounty Board to interact with Villagers.
A tweaked configuration system makes it easier for modpack makers to configure Bountiful. As such,
old config files and datapacks that overwrote existing Bountiful data may not function correctly in this version.

### Added

#### Gameplay
- Added processor lists, so that bounty boards can generate with a different look, depending on which village it generates in.
- After turning in a bounty, a villager (preferably one who matches the profession of the items you sold them), if available, will walk up to the board and pick up their goods.
  - The villager will receive some trade XP for doing this - even more if their profession did match, and based on board reputation.
  - If the villager is currently busy, this will happen once they are free.
- The way the board's bounties are refreshed has been updated - now, bounties that have been on the board the longest have the highest chances of being removed first.
  - This should reduce the number of old bounties that 'happen' to stay on the board through dumb luck (or rather, misfortune)
- Added advancements! There are now `8` advancements.
- The board will now appear to update in the time you've been gone even when the chunk is unloaded. It does this by playing catch-up when the chunk reloads.
- Potions, Tipped Arrows and probably a few other items with dynamic names will now be displayed correctly in bounty tooltips.
- Enchantments on enchanted books are now displayed in bounty tooltips.
- Decrees and Bounties can now be composted in NeoForge (previously a Fabric-only feature), bringing seemingly full parity between Fabric and NeoForge versions.
- Decrees will now show up rarely in the list of trades offered by Wandering Traders.

#### Configuration
- Decrees now have three new, optional fields:
  - `canSpawn` determines whether a bounty board can spawn with this Decree in it (default: true)
  - `canReveal` determines whether a blank Decree can be revealed to be this Decree when placed on the board (default: true)
  - `canWanderBuy` determines whether this Decree can be found in the stocks of Wandering Traders (default: true)
- Added a new config option, `board.canBreak`, that determines whether bounty boards can be broken (default: true)
- Added a new config option, `bounty.allowDecreeMixing` (default: true)
  - When enabled, will pull randomly from all Decrees on a board when creating a bounty. 
  - When disabled, it will pull from only one Decree at random when creating a bounty.
- Added a new config option, `bounty.reverseMatchingAlgorithm` (default: false).
  - By default, Bountiful picks rewards randomly and then picks a number of objectives that total up to the same value as the rewards.
  - If enabled, the reverse happens: Bountiful picks objectives randomly then finds rewards that total up to the same value as the objectives.
  - This also means that, effectively, `maxNumRewards` suddenly limits the number of objectives instead, and `objectiveDifficultyModifierPercent` suddenly affects how good of rewards you get instead.
- Added a new modpack maker tool, the Decree Analyzer. It can be accessed via `/bo util analyzer`
  - This new tool allows users to find 'holes' Decrees, where rewards might not have suitable objectives and vice-versa.
- Added warning log messages to help notify the user when their config data seems erroneous.
- Added a new command, `/bo util configToDataPack [packFileName] [packDescInQuotes]` that creates a datapack from Bountiful's current config folder. It gets placed into the config folder as `[packFileName].zip`.

### Changed
- The board layout for objectives and rewards is now centered, with an arrow dividing the objectives and rewards. 
  - Hopefully this is a visually pleasing change, I will be looking at user feedback to see how it is received.
- Much better handling and notification of possible config file errors via the log.
- Reformatted the config, making it easier for users without ClothConfig to understand.
- Updated translations. `TODO("Add new localization listing and authors)`
- The Forge release distribution has been changed to NeoForge for the foreseeable future.
  - We may re-evaluate this in the future, but for right now, it seems to be the trend.

### Fixed
- Fixed an extremely rare crash on (Neo)Forge when loading dynamic textures and the user has hundreds of mods
- Fixed shift-clicking of items between slots on the Bounty Board
- Fixed an issue where boards might show that they are receiving a discount higher than the max of 40% (purely cosmetic)
- Fixed an issue where dragging the scrollbar on the Bounty Board did not work

## [6.0.3] for 1.20.1 - 2023-07-17

### Added
- Implemented the new item group system, so that items will once again show up in creative tabs, as well as in JEI.
- Added Numismatic Overhaul compat for Fabric.
- Updated translations.
- Added a default single localization override for Decrees.
- Allow mob deaths from pets to count towards entity objectives.

### Fixed
- Fixed the "missing ops" cosmetic issue upon world load.

## [6.0.2] for 1.20.1 - 2023-06-24

### Fixed
- Fixed pool validation so that it will not crash for item objectives/rewards that pull from tags. 

## [6.0.1] for 1.20.1 - 2023-06-24

First release for 1.20.1.

## [6.0.0-beta.3] for 1.19.4 - 2023-03-30

### Fixed
- Fixed commands not working as intended, possibly letting players run commands without op permissions or
not letting players run commands at all

## [6.0.0-beta.2] for 1.19.4 - 2023-03-25

### Fixed
- Fixed boards not generating in villages in Forge

## [6.0.0-beta.1] for 1.19.4 - 2023-03-25

### Changed
- Bountiful (and Kambrik) have now been split into separate modules per type of loader. This allows us to
develop for both Fabric and Forge at the same time, releasing for both platforms at once
- Added data validation for pools, allowing us to reject invalid pool entries rather than load them as air



# Old Fabric Versions (Pre-Merge)

## [Fabric-5.0.0] for 1.19.4 - 2023-03-21

### Added
- Added the ability to compost both Bounties and Decrees
- Added Criterion type objectives - see [the wiki](https://kambrik.ejekta.io/mods/bountiful/) for details!
  - These objectives allow us to check for objective completion using the same triggers that vanilla uses for Advancements
  - This allows us to create fun objectives such as "kill a zombie while on fire" if we want!
- Added Notifications upon bounty completion
  - Toast notifications and audio notifications (ping sound) are currently added
  - This can be toggled off in the config
- Added a patch-overwrite system for data loading for modpack makers
  - This allows modpack makers to edit/update/remove specific bounties without replacing entire pools
- Added compat for new mods such as Tech Reborn, Xtra Arrows and Villager Hats
- Did a minor balance pass, adding a few rewards where applicable to several pools
- Added a new Decree called the Inventor Decree, used for redstone and tech mod related items
- Added a new command, `/bo util configToDataPack`, which converts the current Bountiful config data into a data pack.
  - It takes two parameters, the pack file name and the pack description

### Changed
- Non-core data now loads from built-in resource packs
  - This allows us to create a resource pack for every mod we want to add compatibility for
  - This also allows users to turn off compatibility easily for any mods they desire
  - These packs are loaded by default if the associated mod is present
  - Players can use the `/datapack` command or modify datapacks on world creation to change which compat is enabled for a particular world
- Bounty pool entries are each given its own ID so that other mods and data packs can more easily overwrite only parts of our data
- Fundamentally changed how bounty data is stored in bounties
  - This will break existing bounties if upgrading a world that used Bountiful from before 1.19.4 to 1.19.4

### Fixed
- Fixed rare situation where bounties with unmet dependencies could be partially, but not fully completed

### Known Issues
- `/bo util configToDataPack` will not work correctly if the data file is nested (e.g. `bounty_decrees/xyz_folder/my_decree`)

## [Fabric-4.1.1] for 1.19.3 - 2022-03-02

### Added
- Added mod items into the Functional creative tab
- Added config option for maximum number of rewards per bounty

## [Fabric-4.1.0] for 1.19.3 - 2022-03-02

### Removed
- Removed mod items from creative tabs, this is scheduled to be reimplemented at a later time

## [Fabric-2.0.2] for 1.18.1 - 2022-01-09

### Fixed
- Rebuilt mod with newly compiled class files to avoid default interface method bug with Kambrik 3.0.1 and Bountiful 2.0.1

## [Fabric-2.0.1] for 1.18 - 2021-11-21

### Fixed
- Removed several GUIs that existed for testing purposes

## [Fabric-2.0.0] for 1.18 - 2021-11-20

### Added
- A new GUI interface for bounty boards
- Item Tag bounties
  - e.g. get 10 of any type of wool
- Item bounties derived from item tag
  - e.g. picks a type of wool and asks you to get 10 of it
- Command rewards for bounties (intended for modpack makers)
  - runs a command when the bounty completes
- Bounty boards in villages (as well as newly crafted boards) come pre-populated with bounties
- Added a slider for bounty objective frequency

### Fixed
- Fixed a problem with bounty board generation in villages
- Fixed an issue with reputation levels over 30 being allowed

### Changed
- Lightly rebalanced many objectives and added some new rewards
- Lowered default bounty board generation frequency

## [Fabric-1.0.0] for 1.17.1 - 2021-08-25
- Initial release of Bountiful


# Old Forge Versions (Pre-Merge)

## [Forge-5.0.0] for 1.19.4 - 2023-03-21

### Added
- Added the ability to compost both Bounties and Decrees
- Added Criterion type objectives - see [the wiki](https://kambrik.ejekta.io/mods/bountiful/) for details!
  - These objectives allow us to check for objective completion using the same triggers that vanilla uses for Advancements
  - This allows us to create fun objectives such as "kill a zombie while on fire" if we want!
- Added Notifications upon bounty completion
  - Toast notifications and audio notifications (ping sound) are currently added
  - This can be toggled off in the config
- Added a patch-overwrite system for data loading for modpack makers
  - This allows modpack makers to edit/update/remove specific bounties without replacing entire pools
- Added compat for new mods such as Tech Reborn, Xtra Arrows and Villager Hats
- Did a minor balance pass, adding a few rewards where applicable to several pools
- Added a new Decree called the Inventor Decree, used for redstone and tech mod related items

### Changed
- Bounty pool entries are each given its own ID so that other mods and data packs can more easily overwrite only parts of our data
- Fundamentally changed how bounty data is stored in bounties
  - This will break existing bounties if upgrading a world that used Bountiful from before 1.19.4 to 1.19.4

### Fixed
- Fixed rare situation where bounties with unmet dependencies could be partially, but not fully completed

## [Forge-4.1.1] for 1.19.3 - 2022-03-02

### Added
- Added mod items into the Functional creative tab
- Added config option for maximum number of rewards per bounty

## [Forge-4.1.0] for 1.19.3 - 2022-03-02

### Removed
- Removed mod items from creative tabs, this is scheduled to be reimplemented at a later time

## [Forge-2.0.2] for 1.18.1 - 2022-01-09

### Fixed
- Rebuilt mod with newly compiled class files to avoid default interface method bug with Kambrik 3.0.1 and Bountiful 2.0.1

## [Forge-2.0.1] for 1.18 - 2021-11-21

### Fixed
- Removed several GUIs that existed for testing purposes

## [Forge-2.0.0] for 1.18 - 2021-11-20

### Added
- A new GUI interface for bounty boards
- Item Tag bounties
  - e.g. get 10 of any type of wool
- Item bounties derived from item tag
  - e.g. picks a type of wool and asks you to get 10 of it
- Command rewards for bounties (intended for modpack makers)
  - runs a command when the bounty completes
- Bounty boards in villages (as well as newly crafted boards) come pre-populated with bounties
- Added a slider for bounty objective frequency

### Fixed
- Fixed a problem with bounty board generation in villages
- Fixed an issue with reputation levels over 30 being allowed

### Changed
- Lightly rebalanced many objectives and added some new rewards
- Lowered default bounty board generation frequency

## [Forge-1.0.0] for 1.17.1 - 2021-08-25
- Initial release of Bountiful
