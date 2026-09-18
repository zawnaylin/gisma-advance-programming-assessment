# QR Restaurant

A QR-code ordering system for restaurants. Customers scan a QR code at their table to view the menu, place
orders and track their status; kitchen staff work through incoming orders and waiters manage tables from their
own screens. Built with Spring Boot, Spring Modulith and Vaadin, backed by PostgreSQL.

## Running the app

The app stores its data in PostgreSQL. The schema is managed by Flyway migrations in
`src/main/resources/db/migration`.

### With Docker Compose (deployment)

```
cp .env.example .env   # set POSTGRES_PASSWORD and APP_BASE_URL
docker compose up -d --build
```

The app is served on http://localhost:8080. `APP_BASE_URL` is encoded into the table QR codes, so set it to an
address customers' phones can reach (e.g. the host's LAN IP or domain). Data is kept in the `pgdata` volume;
`docker compose down -v` deletes it.

### Locally from the IDE

Start only the database, then run `Application` as usual (it connects to `localhost:5432` by default):

```
docker compose up -d db
```

Connection settings can be overridden with the `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` environment variables.

### Tests

`./mvnw test` starts a throwaway PostgreSQL container through Testcontainers, so Docker must be running.

## Managing the menu and tables (REST API)

Full endpoint reference: [docs/rest-api.md](docs/rest-api.md). A Postman collection with tests for every
endpoint is in [docs/postman](docs/postman).

Catalogues, categories, menu items and tables have no admin screen; manage them over REST. Each resource supports
`GET` (list), `GET /{id}`, `POST`, `PUT /{id}` and `DELETE /{id}`:

| Resource   | Path                    | Body fields                                                          |
|------------|-------------------------|----------------------------------------------------------------------|
| Catalogue  | `/api/menu/catalogues`  | `id` (create only, optional), `name`, `description`                  |
| Category   | `/api/menu/categories`  | `id` (create only, optional), `name`, `description`                  |
| Menu item  | `/api/menu/items`       | `id` (create only, optional), `name`, `description`, `price`, `catalogueId`, `categoryId` |
| Table      | `/api/tables`           | `id` (create only, optional), `capacity`                             |

A table's status is derived from its dining session, so it is read-only; `PUT` changes the capacity. A table can
only be deleted while nobody is seated and before it has any dining sessions, so that history is never lost.
`/api/tables/{id}/select` (start a session) and `/api/tables/{id}/qr` are part of the dining flow, not of CRUD.

`GET /api/menu/items` can be filtered with `?catalogueId=` and/or `?categoryId=`.

```
curl -X POST localhost:8080/api/menu/categories -H 'Content-Type: application/json' \
     -d '{"id": "desserts", "name": "Desserts", "description": "Sweet things"}'

curl -X POST localhost:8080/api/menu/items -H 'Content-Type: application/json' \
     -d '{"id": "cheesecake", "name": "Cheesecake", "price": 6.5, "catalogueId": "all-day", "categoryId": "desserts"}'

curl -X PUT localhost:8080/api/menu/items/cheesecake -H 'Content-Type: application/json' \
     -d '{"name": "Cheesecake", "description": "Baked", "price": 7, "catalogueId": "all-day", "categoryId": "desserts"}'

curl -X DELETE localhost:8080/api/menu/items/cheesecake
```

- `POST` returns `201 Created` with a `Location` header; without an `id` a UUID is generated. Ids may only contain
  letters, digits, `-` and `_`.
- `PUT` replaces all fields and returns `404` if the id doesn't exist (it never creates).
- Errors use RFC 9457 problem details: `400` invalid body or unknown `catalogueId`/`categoryId`, `404` unknown id,
  `409` id already taken or a catalogue/category that menu items still use.
- Deleting a menu item doesn't affect past orders; they keep the item id and the price they were placed at.

## Design docs

More background on the design lives in [docs](docs):

- [`use-case.puml`](docs/use-case.puml) / [`usecase.mmd`](docs/usecase.mmd) — use case diagrams
- [`context-map.mmd`](docs/context-map.mmd) — module boundaries and how they talk to each other
- [`menu.mmd`](docs/menu.mmd), [`order.mmd`](docs/order.mmd), [`table.mmd`](docs/table.mmd) — per-module domain models
- [`database.md`](docs/database.md) — why PostgreSQL
- [`user-stories.md`](docs/user-stories.md) — user stories and acceptance criteria
