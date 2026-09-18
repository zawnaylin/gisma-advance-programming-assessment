package com.qr_restaurant.order;

import com.qr_restaurant.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OrderApiErrorsTests {

    @Autowired MockMvc mvc;

    @Test
    void orderForAnUnknownMenuItemIsRejectedAsBadRequest() throws Exception {
        mvc.perform(post("/api/tables").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": "API-ORDER-T1", "capacity": 2}"""))
                .andExpect(status().isCreated());
        var qr = mvc.perform(post("/api/tables/API-ORDER-T1/select"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var sessionId = qr.replaceAll(".*session=([0-9a-f-]+).*", "$1");

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"diningSessionId": "%s", "items": [{"menuItemId": "does-not-exist", "quantity": 1}]}"""
                                .formatted(sessionId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("No such menu item: does-not-exist"));
    }

    @Test
    void actionsOnAnUnknownOrderReturnNotFound() throws Exception {
        mvc.perform(post("/api/orders/does-not-exist/confirm"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No such order: does-not-exist"));

        mvc.perform(post("/api/orders/does-not-exist/ready")).andExpect(status().isNotFound());
        mvc.perform(post("/api/orders/does-not-exist/serve")).andExpect(status().isNotFound());

        mvc.perform(post("/api/orders/does-not-exist/cancel").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cancelledBy": "kitchen", "reason": "test"}"""))
                .andExpect(status().isNotFound());
    }
}
