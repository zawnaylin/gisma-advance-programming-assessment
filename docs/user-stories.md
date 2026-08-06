# User Stories

## US-01: Scan QR code to view menu
As a customer, I want to scan a QR code at my table, so that I can view
the menu without waiting for a waiter.

**Acceptance criteria:**
- QR code encodes a unique table and order ID
- Scanning opens the menu filtered to available items only
- No login/account required

## US-02: Place an order
As a customer, I want to add items to a cart and submit an order, so that
the kitchen receives it without a waiter taking it manually.

**Acceptance criteria:**
- Order is linked to the correct table and order ID
- Order fails clearly if an item just went out of stock
- Customer sees an order confirmation with estimated status

## US-03: Track order status
As a customer, I want to see my order's status update, so that I know
when my food is being prepared or ready.

**Acceptance criteria:**
- Status moves through PLACED → PREPARING → READY → SERVED
- Status is visible without needing to re-scan the QR code

## US-04: View incoming orders (kitchen/staff side)
As kitchen staff, I want to see incoming orders in real time, so that I
can prepare them in the correct sequence.

**Acceptance criteria:**
- Staff view requires authentication
- Orders are sortable/filterable by status
- Staff can update order status