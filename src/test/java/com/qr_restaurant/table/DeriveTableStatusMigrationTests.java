package com.qr_restaurant.table;

import com.qr_restaurant.TestcontainersConfiguration;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Migrates a separate schema to V4 (table status stored on the table), fills it with data as the old
 * code could have left it, then applies V5 and checks each table ends up with the same effective status.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class DeriveTableStatusMigrationTests {

    private static final String SCHEMA = "v5_migration_test";

    @Autowired DataSource dataSource;
    @Autowired JdbcTemplate jdbc;

    @AfterEach
    void dropSchema() {
        jdbc.execute("drop schema if exists " + SCHEMA + " cascade");
    }

    @Test
    void existingTablesKeepTheirEffectiveStatus() {
        migrate("4");

        jdbc.batchUpdate(
                "insert into " + SCHEMA + ".dining_tables (id, capacity, status, version) values"
                        + " ('OCC', 2, 'OCCUPIED', 1), ('DIRTY', 2, 'WAITING_FOR_CLEANING', 2),"
                        + " ('FREE', 2, 'AVAILABLE', 3), ('NEVER', 2, 'AVAILABLE', 0)",
                "insert into " + SCHEMA + ".dining_sessions (id, table_id, status, start_time, end_time, number_of_guests, version) values"
                        // occupied table: an older completed session and the current active one
                        + " ('occ-old', 'OCC', 'COMPLETED', now() - interval '2 hours', now() - interval '1 hour', 1, 1),"
                        + " ('occ-now', 'OCC', 'ACTIVE', now() - interval '10 minutes', null, 1, 0),"
                        // waiting for cleaning: the old flow completed the session when guests left
                        + " ('dirty-old', 'DIRTY', 'COMPLETED', now() - interval '3 hours', now() - interval '2 hours', 1, 1),"
                        + " ('dirty-last', 'DIRTY', 'COMPLETED', now() - interval '1 hour', now() - interval '5 minutes', 1, 1),"
                        // available table with a session left open by an older flow (cleaned without ending)
                        + " ('free-orphan', 'FREE', 'ACTIVE', now() - interval '1 day', null, 1, 0)");

        migrate(null);

        assertThat(currentSession("OCC")).isEqualTo("occ-now");
        assertThat(sessionStatus("occ-now")).isEqualTo("ACTIVE");

        assertThat(currentSession("DIRTY")).isEqualTo("dirty-last");
        assertThat(sessionStatus("dirty-last")).isEqualTo("WAITING_FOR_CLEANING");
        assertThat(sessionStatus("dirty-old")).isEqualTo("COMPLETED");

        assertThat(currentSession("FREE")).isNull();
        assertThat(sessionStatus("free-orphan")).isEqualTo("COMPLETED");
        assertThat(jdbc.queryForObject("select end_time is not null from " + SCHEMA + ".dining_sessions where id = 'free-orphan'",
                Boolean.class)).isTrue();

        assertThat(currentSession("NEVER")).isNull();

        // The stored status is gone; only the session holds the stage now.
        assertThat(jdbc.queryForObject("select count(*) from information_schema.columns"
                + " where table_schema = ? and table_name = 'dining_tables' and column_name = 'status'", Integer.class, SCHEMA))
                .isZero();
    }

    private void migrate(String targetVersion) {
        var config = Flyway.configure()
                .dataSource(dataSource)
                .schemas(SCHEMA)
                .locations("classpath:db/migration");
        if (targetVersion != null) {
            config.target(targetVersion);
        }
        config.load().migrate();
    }

    private String currentSession(String tableId) {
        return jdbc.queryForObject("select current_session_id from " + SCHEMA + ".dining_tables where id = ?", String.class, tableId);
    }

    private String sessionStatus(String sessionId) {
        return jdbc.queryForObject("select status from " + SCHEMA + ".dining_sessions where id = ?", String.class, sessionId);
    }
}
