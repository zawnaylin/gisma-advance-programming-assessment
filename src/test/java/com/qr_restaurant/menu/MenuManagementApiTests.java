package com.qr_restaurant.menu;

import com.qr_restaurant.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class MenuManagementApiTests {

    @Autowired MockMvc mvc;

    @Test
    void catalogueCrud() throws Exception {
        post("/api/menu/catalogues", """
                {"id": "test-dinner", "name": "Dinner", "description": "Evenings only"}""")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/menu/catalogues/test-dinner"))
                .andExpect(jsonPath("$.catalogue.name").value("Dinner"));

        mvc.perform(get("/api/menu/catalogues/test-dinner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.catalogue.description").value("Evenings only"));

        // Creating with a taken id must not overwrite the existing catalogue.
        post("/api/menu/catalogues", """
                {"id": "test-dinner", "name": "Other"}""")
                .andExpect(status().isConflict());

        put("/api/menu/catalogues/test-dinner", """
                {"name": "Late Dinner", "description": "After 9pm"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.catalogue.name").value("Late Dinner"));

        put("/api/menu/catalogues/no-such-catalogue", """
                {"name": "Nope"}""")
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/menu/catalogues/no-such-catalogue")).andExpect(status().isNotFound());

        mvc.perform(delete("/api/menu/catalogues/test-dinner")).andExpect(status().isNoContent());
        mvc.perform(get("/api/menu/catalogues/test-dinner")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/menu/catalogues/test-dinner")).andExpect(status().isNotFound());
    }

    @Test
    void menuItemsReferenceExistingCatalogueAndCategory() throws Exception {
        post("/api/menu/categories", """
                {"id": "test-desserts", "name": "Desserts"}""")
                .andExpect(status().isCreated());

        post("/api/menu/items", """
                {"id": "test-cheesecake", "name": "Cheesecake", "description": "Baked", "price": 6.5,
                 "catalogueId": "all-day", "categoryId": "test-desserts"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.price").value(6.5))
                .andExpect(jsonPath("$.item.categoryId").value("test-desserts"));

        mvc.perform(get("/api/menu/items").param("categoryId", "test-desserts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)));

        // A category that items still use can't be deleted.
        mvc.perform(delete("/api/menu/categories/test-desserts"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Category test-desserts still has 1 menu item(s); delete or move them first"));

        put("/api/menu/items/test-cheesecake", """
                {"name": "Cheesecake", "price": 7, "catalogueId": "no-such-catalogue", "categoryId": "test-desserts"}""")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("No such catalogue: no-such-catalogue"));

        put("/api/menu/items/test-cheesecake", """
                {"name": "Cheesecake", "price": 7, "catalogueId": "all-day", "categoryId": "beverages"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.price").value(7.0))
                .andExpect(jsonPath("$.item.categoryId").value("beverages"));

        mvc.perform(delete("/api/menu/categories/test-desserts")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/menu/items/test-cheesecake")).andExpect(status().isNoContent());
        mvc.perform(get("/api/menu/items/test-cheesecake")).andExpect(status().isNotFound());
    }

    @Test
    void invalidRequestsAreRejectedAndIdsCanBeGenerated() throws Exception {
        post("/api/menu/items", """
                {"name": " ", "price": -1, "categoryId": "beverages"}""")
                .andExpect(status().isBadRequest());

        post("/api/menu/catalogues", """
                {"id": "has spaces!", "name": "Brunch"}""")
                .andExpect(status().isBadRequest());

        post("/api/menu/categories", """
                {"name": "Specials"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category.id", not(emptyOrNullString())));
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
