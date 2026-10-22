# Library Management System
Java 11 · Servlet 4 · JSP/JSTL · JDBC · MySQL 8 · Bootstrap 5 (MVC + DAO)

## Run locally (Tomcat 9)
1. `mysql -u root -p < sql/schema.sql`
2. Edit `src/main/resources/db.properties` (or set DB_URL / DB_USER / DB_PASSWORD env vars)
3. `mvn clean package` -> deploy `target/library.war` to Tomcat 9 -> http://localhost:8080/library/
4. First start seeds admin: `admin@library.com` / `Admin@123` (override with ADMIN_EMAIL / ADMIN_PASSWORD) - change it!

## Run with Docker
`docker compose up --build` -> http://localhost:8080/

## Features
- Roles: ADMIN (dashboard, book CRUD, issue/return) and MEMBER (browse/search, own history)
- Search by title / author / category
- Issue & return use JDBC transactions with `SELECT ... FOR UPDATE` so stock never goes inconsistent
- 14-day loan, Rs. 5/day fine (see `Issue.java`), calculated on return and shown live for overdue books
- PBKDF2 password hashing, session login (session id rotated on login), `AuthFilter` for role-based access,
  CSRF tokens on every POST, prepared statements everywhere, `c:out` escaping for XSS

## Before real production
Use HTTPS (+ `<secure>true</secure>` in web.xml), a connection pool (HikariCP), a non-root DB user, and add unit tests.

## Setup
Copy `src/main/resources/db.properties.example` to `db.properties` and add your MySQL credentials.

## Screenshots
**Member Registration**
![Member Registration](screenshots/01-member-registration.png)

**Member Login**
![Member Login](screenshots/02-member-login.png)

**Search Books**
![Search Books](screenshots/03-member-search-books.png)

**Admin Login**
![Admin Login](screenshots/04-admin-login.png)

**Admin Dashboard**
![Admin Dashboard](screenshots/05-admin-dashboard.png)

**Admin Issues Book**
![Issue Book](screenshots/06-admin-issues-book.png)

**Member Book History**
![Member History](screenshots/07-member-book-history.png)
