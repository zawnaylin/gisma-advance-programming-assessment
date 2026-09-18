# REST API

All endpoints of the QR Restaurant app. Base URL is the running app, e.g. `http://localhost:8080`.
Request and response bodies are JSON (`Content-Type: application/json`).

There is **no authentication**: every endpoint is open to anyone who can reach the app.

A ready-to-run Postman collection with tests for all of this lives in
[`postman/qr-restaurant.postman_collection.json`](postman/qr-restaurant.postman_collection.json).

## Conventions

| Status | Meaning |
|--------|---------|
| `200 OK` | Read or action succeeded |
| `201 Created` | Resource created; the `Location` header points at it |
| `204 No Content` | Deleted |
| `400 Bad Request` | Invalid body (missing/blank name, non-positive capacity or quantity, malformed JSON) or a reference to something that doesn't exist, such as an unknown `catalogueId` or `menuItemId` |
| `404 Not Found` | The id in the URL doesn't exist |
| `409 Conflict` | The current state forbids it: id already taken, invalid status transition, table in use, catalogue still used by menu items, or a concurrent update won the race |

Errors use RFC 9457 problem details:

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "Cannot transition from SERVED to SERVED",
  "instance": "/api/orders/4e726975-5ffa-4fdd-b260-008800cbb48c/serve"
}
```

On create, `id` is optional; leave it out and a UUID is generated. Supplied ids may contain only letters, digits,
`-` and `_`. `PUT` replaces all fields of an existing resource and never creates one.

---

## Menu catalogues — `/api/menu/catalogues`

A catalogue is a menu that applies at certain times, e.g. "All Day Menu".

| Method | Path | Success | Errors |
|--------|------|---------|--------|
| GET | `/api/menu/catalogues` | `200` | |
| GET | `/api/menu/catalogues/{id}` | `200` | `404` |
| POST | `/api/menu/catalogues` | `201` | `400`, `409` id taken |
| PUT | `/api/menu/catalogues/{id}` | `200` | `400`, `404` |
| DELETE | `/api/menu/catalogues/{id}` | `204` | `404`, `409` menu items still use it |

Body fields: `id` (create only, optional), `name` (required), `description`.

```bash
curl localhost:8080/api/menu/catalogues
# {"catalogues":[{"id":"all-day","name":"All Day Menu","description":"Available any time"}]}

curl localhost:8080/api/menu/catalogues/all-day
# {"catalogue":{"id":"all-day","name":"All Day Menu","description":"Available any time"}}

curl -X POST localhost:8080/api/menu/catalogues -H 'Content-Type: application/json' \
     -d '{"id": "dinner", "name": "Dinner", "description": "From 6pm"}'
# 201, Location: /api/menu/catalogues/dinner

curl -X PUT localhost:8080/api/menu/catalogues/dinner -H 'Content-Type: application/json' \
     -d '{"name": "Late Dinner", "description": "From 9pm"}'

curl -X DELETE localhost:8080/api/menu/catalogues/dinner
```

## Categories — `/api/menu/categories`

A category groups items across catalogues, e.g. "Beverages". Same shape as catalogues.

| Method | Path | Success | Errors |
|--------|------|---------|--------|
| GET | `/api/menu/categories` | `200` | |
| GET | `/api/menu/categories/{id}` | `200` | `404` |
| POST | `/api/menu/categories` | `201` | `400`, `409` id taken |
| PUT | `/api/menu/categories/{id}` | `200` | `400`, `404` |
| DELETE | `/api/menu/categories/{id}` | `204` | `404`, `409` menu items still use it |

Body fields: `id` (create only, optional), `name` (required), `description`.
The list is returned as `{"categories": [...]}`, a single one as `{"category": {...}}`.

## Menu items — `/api/menu/items`

| Method | Path | Success | Errors |
|--------|------|---------|--------|
| GET | `/api/menu/items?catalogueId=&categoryId=` | `200` | |
| GET | `/api/menu/items/{id}` | `200` | `404` |
| POST | `/api/menu/items` | `201` | `400` invalid body or unknown catalogue/category, `409` id taken |
| PUT | `/api/menu/items/{id}` | `200` | `400`, `404` |
| DELETE | `/api/menu/items/{id}` | `204` | `404` |

Body fields: `id` (create only, optional), `name` (required), `description`, `price` (required, ≥ 0),
`catalogueId` (required, must exist), `categoryId` (required, must exist).
Both query parameters are optional and can be combined.

```bash
curl 'localhost:8080/api/menu/items?categoryId=beverages'
# {"items":[{"id":"iced-tea","name":"Iced Tea","description":"Freshly brewed","price":3.0,
#            "catalogueId":"all-day","categoryId":"beverages"}]}

curl -X POST localhost:8080/api/menu/items -H 'Content-Type: application/json' \
     -d '{"id": "cheesecake", "name": "Cheesecake", "price": 6.5,
          "catalogueId": "all-day", "categoryId": "desserts"}'
```

Deleting an item does not touch past orders: they keep the item id and the price the order was placed at.
The kitchen screen then shows that id instead of a name.

## Tables — `/api/tables`

`status` is derived from the table's dining session (`AVAILABLE`, `OCCUPIED`, `WAITING_FOR_CLEANING`) and is
read-only. `PUT` changes the capacity only.

| Method | Path | Success | Errors |
|--------|------|---------|--------|
| GET | `/api/tables` | `200` | |
| GET | `/api/tables/{id}` | `200` | `404` |
| POST | `/api/tables` | `201` | `400`, `409` id taken |
| PUT | `/api/tables/{id}` | `200` | `400`, `404` |
| DELETE | `/api/tables/{id}` | `204` | `404`, `409` in use or has dining history |
| POST | `/api/tables/{id}/select` | `200` seats guests and starts a dining session | `404`, `409` table not available |
| GET | `/api/tables/{id}/qr` | `200` | `404` unknown table or no session accepting orders |

Body fields: `id` (create only, optional), `capacity` (required, > 0).

```bash
curl localhost:8080/api/tables
# {"tables":[{"id":"T1","capacity":"2","status":"AVAILABLE"}, ...]}

curl -X POST localhost:8080/api/tables -H 'Content-Type: application/json' -d '{"id": "T5", "capacity": 4}'
curl -X PUT  localhost:8080/api/tables/T5 -H 'Content-Type: application/json' -d '{"capacity": 6}'
curl -X DELETE localhost:8080/api/tables/T5

curl -X POST localhost:8080/api/tables/T1/select
# {"qr":{"qrUrl":"http://localhost:8080/scan?table=T1&session=4ed72448-322e-4897-b9f1-7961aa11122e"}}
```

A table can only be deleted while nobody is seated **and** before it has any dining sessions, so that history is
never lost. `qrUrl` encodes the session id; it is what the QR code image on the staff screen contains, and the
`APP_BASE_URL` setting decides the host in it.

## Orders — `/api/orders`

Order status moves `PENDING → CONFIRMED → READY → SERVED`, and `PENDING` or `CONFIRMED` can go to `CANCELLED`.
Anything else is a `409`.

| Method | Path | Success | Errors |
|--------|------|---------|--------|
| POST | `/api/orders` | `200` returns the new order id | `400` invalid body or unknown menu item, `409` session not accepting orders |
| GET | `/api/orders?status=` | `200` | `400` unknown status value |
| GET | `/api/orders/{id}` | `200` | `404` |
| POST | `/api/orders/{id}/confirm` | `200` | `404`, `409` |
| POST | `/api/orders/{id}/ready` | `200` | `404`, `409` |
| POST | `/api/orders/{id}/serve` | `200` | `404`, `409` |
| POST | `/api/orders/{id}/cancel` | `200` | `400` blank `cancelledBy`, `404`, `409` already served |

Place order body: `diningSessionId` (required), `items` (required, at least one), each with `menuItemId`
(required) and `quantity` (required, > 0). The `status` query parameter is optional; without it, all orders are
returned.

```bash
curl -X POST localhost:8080/api/orders -H 'Content-Type: application/json' \
     -d '{"diningSessionId": "4ed72448-...", "items": [{"menuItemId": "iced-tea", "quantity": 2}]}'
# {"orderId":"4e726975-5ffa-4fdd-b260-008800cbb48c"}

curl localhost:8080/api/orders/4e726975-5ffa-4fdd-b260-008800cbb48c
# {"order":{"id":"4e726975-...","diningSessionId":"4ed72448-...","status":"PENDING",
#           "items":[{"id":"b5ef2a80-...","menuItemId":"iced-tea","quantity":2,"price":3.0}],
#           "cancelledBy":null,"cancellationReason":null}}

curl 'localhost:8080/api/orders?status=PENDING'      # {"orders":[ ... ]}
curl -X POST localhost:8080/api/orders/{id}/confirm  # then /ready, /serve

curl -X POST localhost:8080/api/orders/{id}/cancel -H 'Content-Type: application/json' \
     -d '{"cancelledBy": "kitchen", "reason": "Out of stock"}'
```

The price on each line is copied from the menu when the order is placed, so later menu edits don't change
existing orders. An order can only be placed while its dining session is still accepting orders.

## A full flow over REST

```bash
# 1. customer picks a free table -> dining session + QR url
SESSION=$(curl -s -X POST localhost:8080/api/tables/T1/select | grep -oE 'session=[0-9a-f-]+' | cut -d= -f2)

# 2. customer orders from the menu
ORDER=$(curl -s -X POST localhost:8080/api/orders -H 'Content-Type: application/json' \
        -d "{\"diningSessionId\": \"$SESSION\", \"items\": [{\"menuItemId\": \"iced-tea\", \"quantity\": 2}]}" \
        | grep -oE '[0-9a-f-]{36}')

# 3. kitchen works the order
curl -X POST localhost:8080/api/orders/$ORDER/confirm
curl -X POST localhost:8080/api/orders/$ORDER/ready
curl -X POST localhost:8080/api/orders/$ORDER/serve
```

## Not available over REST

These exist only in the web UI, so a REST-only run leaves the table occupied:

- **End dining session** — customer ("End order" on the menu page) or waiter ("End session" on `/staff/tables`).
- **Mark table cleaned** — waiter on `/staff/tables`, which frees the table again.
- **Live updates** — the table list and kitchen screens update over server push (WebSocket, falling back to long
  polling). REST clients have to poll.
