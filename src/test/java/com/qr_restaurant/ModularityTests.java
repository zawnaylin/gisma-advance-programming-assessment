package com.qr_restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void printsModuleStructure() {
        modules.forEach(System.out::println);
    }

    @Test
    void verifiesModuleBoundaries() {
        modules.verify();
    }
}
