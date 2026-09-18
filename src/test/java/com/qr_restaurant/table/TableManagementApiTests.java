package com.qr_restaurant.table;

import com.qr_restaurant.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TableManagementApiTests {

    @Autowired MockMvc mvc;

    @Test
    void tableCrud() throws Exception {
        post("/api/tables", """
                {"id": "API-T1", "capacity": 4}""")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tables/API-T1"))
                .andExpect(jsonPath("$.table.capacity").value(4))
                .andExpect(jsonPath("$.table.status").value("AVAILABLE"));

        mvc.perform(get("/api/tables/API-T1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.table.id").value("API-T1"));

        // Creating with a taken id must not overwrite the table (and its dining session).
        post("/api/tables", """
                {"id": "API-T1", "capacity": 2}""")
                .andExpect(status().isConflict());

        put("/api/tables/API-T1", """
                {"capacity": 6}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.table.capacity").value(6));

        put("/api/tables/NO-SUCH-TABLE", """
                {"capacity": 2}""")
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/tables/NO-SUCH-TABLE")).andExpect(status().isNotFound());

        mvc.perform(delete("/api/tables/API-T1")).andExpect(status().isNoContent());
        mvc.perform(get("/api/tables/API-T1")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/tables/API-T1")).andExpect(status().isNotFound());
    }

    @Test
    void tableInUseCannotBeDeleted() throws Exception {
        post("/api/tables", """
                {"id": "API-T2", "capacity": 2}""")
                .andExpect(status().isCreated());

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/tables/API-T2/select")).andExpect(status().isOk());

        // Guests are seated.
        mvc.perform(delete("/api/tables/API-T2"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Table API-T2 is in use (OCCUPIED); end its dining session and clean it first"));

        // Even once they leave, the finished session is history worth keeping.
        mvc.perform(get("/api/tables/API-T2")).andExpect(jsonPath("$.table.status").value("OCCUPIED"));
    }

    @Test
    void invalidRequestsAreRejectedAndIdsCanBeGenerated() throws Exception {
        post("/api/tables", """
                {"id": "API-T3", "capacity": 0}""")
                .andExpect(status().isBadRequest());

        post("/api/tables", """
                {"id": "has spaces!", "capacity": 2}""")
                .andExpect(status().isBadRequest());

        post("/api/tables", """
                {"capacity": 2}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.table.id", not(emptyOrNullString())));
    }

    private ResultActions post(String url, String json) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions put(String url, String json) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }
}
