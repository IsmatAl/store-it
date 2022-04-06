package com.example.storageit;

import com.example.storageit.persistence.entity.Product;
import com.example.storageit.persistence.repo.ProductRepo;
import com.example.storageit.service.UserService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StorageItApplication.class)
@Sql(scripts = "/dataset.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor
class StorageItApplicationTests {

    private final ProductRepo productRepo;
    private final UserService userService;

    @Test
    public void addProduct() {

        Product product = Product.of("Gizmo", "23923823WLCMW48", 1900.0, "Gadget", 0.8);
    userService.addProduct("test@example.com", product);

    }

}
