package com.adharsh.adharshmart.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adharsh.adharshmart.model.Product;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.model.User;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductDAOTest {

    private HikariDataSource dataSource;
    private ProductDAO productDAO;
    private Long sellerId;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = TestDb.freshInMemory();
        productDAO = new ProductDAOImpl(dataSource);
        UserDAO userDAO = new UserDAOImpl(dataSource);
        User seller = new User();
        seller.setName("Seller");
        seller.setEmail("seller.dao.test@example.com");
        seller.setPasswordHash("hashed");
        seller.setRole(Role.SELLER);
        sellerId = userDAO.create(seller).getId();
    }

    @AfterEach
    void tearDown() {
        dataSource.close();
    }

    @Test
    void createThenFindByIdRoundTrips() throws Exception {
        Product product = newProduct("Wool Scarf", "Accessories", new BigDecimal("120.00"), 5);

        Product created = productDAO.create(product);
        var found = productDAO.findById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("Wool Scarf", found.get().getName());
        assertEquals(0, new BigDecimal("120.00").compareTo(found.get().getPrice()));
    }

    @Test
    void searchFiltersByCategoryAndKeyword() throws Exception {
        productDAO.create(newProduct("Silk Scarf", "Accessories", new BigDecimal("90.00"), 5));
        productDAO.create(newProduct("Leather Belt", "Accessories", new BigDecimal("70.00"), 5));
        productDAO.create(newProduct("Wool Coat", "Outerwear", new BigDecimal("400.00"), 5));

        List<Product> accessories = productDAO.search(null, "Accessories");
        List<Product> scarves = productDAO.search("scarf", null);

        assertEquals(2, accessories.size());
        assertEquals(1, scarves.size());
        assertEquals("Silk Scarf", scarves.get(0).getName());
    }

    @Test
    void decrementStockFailsWhenInsufficient() throws Exception {
        Product created = productDAO.create(newProduct("Limited Bag", "Bags", new BigDecimal("500.00"), 2));

        boolean firstAttempt = productDAO.decrementStock(created.getId(), 2);
        boolean secondAttempt = productDAO.decrementStock(created.getId(), 1);

        assertTrue(firstAttempt);
        assertFalse(secondAttempt);
    }

    @Test
    void deleteDeactivatesRatherThanRemovingRow() throws Exception {
        Product created = productDAO.create(newProduct("Old Listing", "Bags", new BigDecimal("50.00"), 1));

        productDAO.delete(created.getId());
        var found = productDAO.findById(created.getId());

        assertTrue(found.isPresent());
        assertFalse(found.get().isActive());
    }

    private Product newProduct(String name, String category, BigDecimal price, int stock) {
        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(name);
        product.setDescription("Test description");
        product.setPrice(price);
        product.setStockQty(stock);
        product.setCategory(category);
        product.setImageUrl("https://example.com/img.jpg");
        return product;
    }
}
