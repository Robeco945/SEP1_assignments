# SEP2 Shopping Cart Localization with MySQL

## Overview
This project extends the shopping cart JavaFX app with database localization.

The application now:
- Loads UI localization strings from MySQL table `localization_strings`
- Saves each calculation header into `cart_records`
- Saves each item line into `cart_items` linked by foreign key

## Tech Stack
- Java 21
- JavaFX 21
- Maven
- MySQL 8 (Docker)

## Database Schema
Schema and seed data are in:
- `database/schema_and_seed.sql`

## Local MySQL Setup (Docker)
For this workspace, an existing MariaDB container is used:
- container: `calculator-db`
- host: `localhost`
- port: `3307`
- username: `root`
- password: set through environment variable

Application defaults remain generic in code, so run with environment variables:
- `DB_HOST=localhost`
- `DB_PORT=3307`
- `DB_NAME=shopping_cart_localization`
- `DB_USER=root`
- `DB_PASSWORD=<your-password>`


## Build and Test
```bash
mvn clean verify
```

## Run App
```bash
DB_HOST=localhost DB_PORT=3307 DB_NAME=shopping_cart_localization DB_USER=root DB_PASSWORD=<your-password> mvn javafx:run
```

