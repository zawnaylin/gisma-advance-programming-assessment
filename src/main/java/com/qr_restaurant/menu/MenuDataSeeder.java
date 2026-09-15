package com.qr_restaurant.menu;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.entities.MenuItem;
import com.qr_restaurant.menu.repository.CategoryRepository;
import com.qr_restaurant.menu.repository.MenuCatalogueRepository;
import com.qr_restaurant.menu.repository.MenuItemRepository;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
class MenuDataSeeder {

    private final MenuCatalogueRepository menuCatalogueRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    MenuDataSeeder(MenuCatalogueRepository menuCatalogueRepository,
                   CategoryRepository categoryRepository,
                   MenuItemRepository menuItemRepository) {
        this.menuCatalogueRepository = menuCatalogueRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @PostConstruct
    void seed() {
        var allDayMenu = new MenuCatalogue(new MenuCatalogueId("all-day"), "All Day Menu", "Available any time");
        menuCatalogueRepository.save(allDayMenu);

        var appetizers = new Category(new CategoryId("appetizers"), "Appetizers", "Starters");
        var mainCourse = new Category(new CategoryId("main-course"), "Main Course", "Hearty mains");
        var beverages = new Category(new CategoryId("beverages"), "Beverages", "Drinks");
        categoryRepository.save(appetizers);
        categoryRepository.save(mainCourse);
        categoryRepository.save(beverages);

        menuItemRepository.save(new MenuItem(new MenuItemId("spring-rolls"), "Spring Rolls",
                "Crispy vegetable spring rolls", 5.50, allDayMenu.getId(), appetizers.getId()));
        menuItemRepository.save(new MenuItem(new MenuItemId("margherita-pizza"), "Margherita Pizza",
                "Tomato, mozzarella, basil", 12.00, allDayMenu.getId(), mainCourse.getId()));
        menuItemRepository.save(new MenuItem(new MenuItemId("grilled-chicken"), "Grilled Chicken",
                "Served with seasonal vegetables", 14.50, allDayMenu.getId(), mainCourse.getId()));
        menuItemRepository.save(new MenuItem(new MenuItemId("iced-tea"), "Iced Tea",
                "Freshly brewed", 3.00, allDayMenu.getId(), beverages.getId()));
    }
}
