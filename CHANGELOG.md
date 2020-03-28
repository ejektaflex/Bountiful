# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as closely as it can.

## [Unreleased]
- Nothing as of yet

## [3.1.0] - 2020-03-27
### Added
- "Tinkering" Decree for redstone objectives/rewards
- New objectives/rewards to default bounty data
- Bounty difficulty can now be globally changed via config (`worthRatio`)
- Allow Decrees to be combined at an anvil (not cheap)
- New community made Korean & Chinese language support
- Support for commands as rewards (for modpack makers)
- New '/bo entities' command to dump a list of all entities to `logs/bountiful.log`
- New '/bo hand' command to copy hand content to clipboard
- More compat for some other popular mods
- Recipes for Bounty Boards and Decrees
- Bounty boards now keep their inventory when broken

### Changed
- Rebalance some objectives and rewards
- Datapack structure for more modpack flexibility

### Fixes
- Fix bounty boards not breaking ever
- Fixed some Item-Tag bounties ("Get X of any Y"/"Rewards X of random Y") causing a crash on servers (#71)

## [3.0.0] - 2020-03-11
### Added
- Initial release of Bountiful 3 for v1.14 & v1.15

