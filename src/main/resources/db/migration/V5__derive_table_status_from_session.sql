-- A table's status is now derived from its current dining session instead of being stored twice:
--   no current session -> AVAILABLE, session ACTIVE -> OCCUPIED, session WAITING_FOR_CLEANING -> WAITING_FOR_CLEANING.
-- Deliberately not UNIQUE: Postgres treats an update of a column with a unique index as a key update, which
-- needs a lock that conflicts with the key-share lock the dining_sessions.table_id foreign key takes on insert.
-- Two customers seating at the same table would then deadlock instead of one failing the optimistic version check.
alter table dining_tables
    add column current_session_id varchar(255) references dining_sessions (id);

-- Occupied tables point at their latest open session.
update dining_tables t
set current_session_id = latest.id
from (select distinct on (table_id) id, table_id
      from dining_sessions
      where status <> 'COMPLETED'
      order by table_id, start_time desc nulls last) latest
where latest.table_id = t.id
  and t.status = 'OCCUPIED';

-- Tables waiting for cleaning had their session COMPLETED under the old flow; point at the latest one
-- and move it back to the new WAITING_FOR_CLEANING stage.
update dining_tables t
set current_session_id = latest.id
from (select distinct on (table_id) id, table_id
      from dining_sessions
      order by table_id, start_time desc nulls last) latest
where latest.table_id = t.id
  and t.status = 'WAITING_FOR_CLEANING';

update dining_sessions s
set status   = 'WAITING_FOR_CLEANING',
    end_time = coalesce(s.end_time, now())
from dining_tables t
where t.current_session_id = s.id
  and t.status = 'WAITING_FOR_CLEANING';

-- Any other open session is left over from older flows (e.g. a table cleaned without ending its session).
update dining_sessions s
set status   = 'COMPLETED',
    end_time = coalesce(s.end_time, now())
where s.status <> 'COMPLETED'
  and not exists (select 1 from dining_tables t where t.current_session_id = s.id);

alter table dining_tables
    drop column status;
