-- When the order was placed, so the kitchen can work through orders oldest first.
-- Existing orders get the migration time; new orders always supply their own value.
alter table orders
    add column placed_at timestamp(6) with time zone not null default now();

alter table orders
    alter column placed_at drop default;
