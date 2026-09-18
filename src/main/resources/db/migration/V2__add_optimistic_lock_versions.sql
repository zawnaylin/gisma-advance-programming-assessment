-- Version columns for optimistic locking on tables and dining sessions (@Version in the entities).
alter table dining_tables
    add column version bigint not null default 0;

alter table dining_sessions
    add column version bigint not null default 0;
