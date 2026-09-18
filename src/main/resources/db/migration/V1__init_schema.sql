-- menu module
create table menu_catalogues
(
    id          varchar(255) primary key,
    name        varchar(255),
    description varchar(255)
);

create table categories
(
    id          varchar(255) primary key,
    name        varchar(255),
    description varchar(255)
);

create table menu_items
(
    id           varchar(255) primary key,
    name         varchar(255),
    description  varchar(255),
    price        double precision not null,
    catalogue_id varchar(255) references menu_catalogues (id),
    category_id  varchar(255) references categories (id)
);

create index idx_menu_items_catalogue_id on menu_items (catalogue_id);
create index idx_menu_items_category_id on menu_items (category_id);

-- table module
create table dining_tables
(
    id       varchar(255) primary key,
    capacity integer     not null,
    status   varchar(32) not null
);

create table dining_sessions
(
    id               varchar(255) primary key,
    table_id         varchar(255) not null references dining_tables (id),
    status           varchar(32)  not null,
    start_time       timestamp(6),
    end_time         timestamp(6),
    number_of_guests integer      not null
);

create index idx_dining_sessions_table_id on dining_sessions (table_id);

-- order module (ids from other modules are kept as plain values, no cross-module FKs)
create table orders
(
    id                  varchar(255) primary key,
    dining_session_id   varchar(255),
    status              varchar(32) not null,
    cancelled_by        varchar(255),
    cancellation_reason varchar(255)
);

create table order_items
(
    id           varchar(255) primary key,
    order_id     varchar(255)     not null references orders (id),
    menu_item_id varchar(255)     not null,
    quantity     integer          not null,
    price        double precision not null
);

create index idx_order_items_order_id on order_items (order_id);
