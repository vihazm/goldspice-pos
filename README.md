# GoldSpice POS

A Java Swing desktop point-of-sale and store management app for a Ceylon spice business, backed by MySQL.

## Features
- Role-based logins: **Admin**, **Manager**, **Cashier**
- Admin: manage users and products
- Manager: update product prices, view sales (filter by quarter)
- Cashier: create customer bills (saved with their line items)

## Requirements
- Java 17+ (built with JDK 24)
- MySQL 8.x
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) (9.3.0 used)
- NetBeans (optional, the project is a standard NetBeans/Ant project)

## Setup
1. Create the database and tables:
   ```bash
   mysql -u root -p < schema.sql
   ```
2. Copy `db.properties.example` to `db.properties` and put in your MySQL password.
   (`db.properties` is git-ignored. You can also use the `GOLDSPICE_DB_URL`, `GOLDSPICE_DB_USER` and `GOLDSPICE_DB_PASSWORD` environment variables.)
3. Add the MySQL Connector/J jar to the project libraries and run `FinalProjectGoldspice.FinalProjectGoldspice`.
   Start the app from the project folder so it can find `db.properties`.

## Demo logins (from `schema.sql`)
| Role | Username | Password |
|---|---|---|
| Admin | admin1 | admin123 |
| Manager | manager1 | manager123 |
| Cashier | cashier1 | cashier123 |

These are demo accounts for local testing only. Change them before any real use.

## Known limitations
- User passwords are stored in plain text.
- `LoginController` still has a hardcoded `admin` / `admin123` login.
- The Reports screen is an unfinished stub.
