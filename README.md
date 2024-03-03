# BotCore

This is a "core" providing a robust foundation for developing Discord bots.

## Features

- **JDA Based**: Built on top of the JDA library, this core provides a simple integration of the library and a
  ready-to-use interface:
    - `ICommand` and `AbstractCommand` can be used to create what we call "classic commands",
      e.g., `?help`, `?kick @foo`.
    - `ISlashCommand`, `AbstractSlashCommand`, and `ISubCommand` can be used to create slash commands,
      e.g., `/kick member:@foo`.
    - `ISubsystem` and `AbstractSubsystem` can be used to instantiate different listeners to receive various events from
      JDA.
- **Database Support**: BotCore supports database interactions via Hibernate (SQLite), enabling easy data storage and
  retrieval for your bot's needs.
- **Configurability**: This core integrates configuration via a .json file, see `Config`.
- **Java 20 Compatibility**: Developed using Java 20 with the latest language features and improvements.
- **Sentry and Webhook Error Reporting**: Implementation of Sentry and Discord webhook to report errors.

### Prerequisites

- Java Development Kit (JDK) 20 or higher

### Getting Started

To use this library, you can checkout Kánei:
[chianti-ga/kanei](https://github.com/chianti-ga/kanei)
