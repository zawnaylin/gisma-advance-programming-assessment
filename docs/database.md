# ADR-001: Main Database Choice

**Status:** Proposed **Date:** 2026-08-07

## Context

I need to choose the database for storing the restaurant's menu, orders, and user data. The database should support
real-time updates for order tracking and be scalable to handle multiple restaurants.

## Decision

I decided to go with PostgresSQL as the main database for the application. PostgreSQL is a powerful, open-source
relational database that supports advanced features like JSONB for semi-structured data, full-text search, and robust
indexing. It also has strong community support and is known for its reliability and performance.

## Alternatives considered

I considered other relational databases such as MySQL and MSSQL, but PostgreSQL's advanced features and flexibility made
it a better fit for the application's needs.

## Consequences

- Able to handle complex queries and relationships between tables.
