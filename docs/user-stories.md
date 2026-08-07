# User Stories


## US-00: Choose Table
As a customer, I want to choose available table, so that I can go sit and order food without the waiter.

**Acceptance criteria:**
- Customer can see available tables in the restaurant
- Customer can choose a table and receive a unique QR code for that table
- Customer can see the status (Occupied, Reserved, Available) of each table in real time

## US-01: Scan QR code to view menu
As a customer, I want to scan a QR code at my table, so that I can view
the menu without waiting for a waiter.

**Acceptance criteria:**
- Each table has a static Table ID; the QR code encodes a unique,
  rotating Session ID linked to that table for the current dining period
- A new Session is generated each time a table transitions from
  CLEANED → OCCUPIED, invalidating any previously scanned QR for that table
- Scanning opens the menu filtered to available items only, scoped to
  the active session
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

## US-05: Update order status (kitchen/staff side)
As kitchen staff, I want to update the status of an order, so that waiter can bring the food to the customer at the right time.

**Acceptance criteria:**
- Staff change order status to PREPARING, READY, SERVED.
- Cook can change from PREPARING to READY, and waiter can change from READY to SERVED.
- Status updates are reflected in real time on the customer side.

## US-06: Cleaning up the table
As a waiter, I want to mark a table as cleaned after the customer leaves, so that the table is ready for the next customer.

**Acceptance criteria:**
- Waiter can mark a table as CLEANED after the customer leaves.
- Once marked as CLEANED, the table is available for new customers and the QR code can be reused for the next order.