# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as closely as it can.

## [2.0.2] for 1.18.1 - 2022-01-09

### Fixed
- Rebuilt mod with newly compiled class files to avoid default interface method bug with Kambrik 3.0.1 and Bountiful 2.0.1

## [2.0.1] for 1.18 - 2021-11-21

### Fixed
- Removed several GUIs that existed for testing purposes

## [2.0.0] for 1.18 - 2021-11-20

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

## [1.0.0] for 1.17.1 - 2021-08-25
- Initial release of Bountiful
