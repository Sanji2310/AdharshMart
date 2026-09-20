package com.adharsh.adharshmart.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adharsh.adharshmart.model.Product;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.model.User;
import com.adharsh.adharshmart.model.WishlistItem;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WishlistDAOTest {

    private HikariDataSource dataSource;
    private WishlistDAO wishlistDAO;
    private Long buyerId;
    private Long productId;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = TestDb.freshInMemory();
        wishlistDAO = new WishlistDAOImpl(dataSource);
        UserDAO userDAO = new UserDAOImpl(dataSource);
        ProductDAO productDAO = new ProductDAOImpl(dataSource);

        User seller = new User();
        seller.setName("Seller");
        seller.setEmail("seller.wishlist.test@example.com");
        seller.setPasswordHash("hashed");
        seller.setRole(Role.SELLER);
        Long sellerId = userDAO.create(seller).getId();

        User buyer = new User();
        buyer.setName("Buyer");
        buyer.setEmail("buyer.wishlist.test@example.com");
        buyer.setPasswordHash("hashed");
        buyer.setRole(Role.BUYER);
        buyerId = userDAO.create(buyer).getId();

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName("Wishlist Test Product");
        product.setDescription("desc");
        product.setPrice(new BigDecimal("99.00"));
        product.setStockQty(5);
        product.setCategory("Accessories");
        productId = productDAO.create(product).getId();
    }

    @AfterEach
    void tearDown() {
        dataSource.close();
    }

    @Test
    void addThenFindByUserRoundTrips() throws Exception {
        wishlistDAO.add(buyerId, productId);

        List<WishlistItem> items = wishlistDAO.findByUser(buyerId);

        assertEquals(1, items.size());
        assertEquals(productId, items.get(0).getProductId());
    }

    @Test
    void existsByUserAndProductReflectsState() throws Exception {
        assertFalse(wishlistDAO.existsByUserAndProduct(buyerId, productId));

        wishlistDAO.add(buyerId, productId);

        assertTrue(wishlistDAO.existsByUserAndProduct(buyerId, productId));
    }

    @Test
    void removeDeletesOnlyForOwningUser() throws Exception {
        WishlistItem saved = wishlistDAO.add(buyerId, productId);

        wishlistDAO.remove(saved.getId(), 999L); // wrong user — should not delete
        assertTrue(wishlistDAO.existsByUserAndProduct(buyerId, productId));

        wishlistDAO.remove(saved.getId(), buyerId);
        assertFalse(wishlistDAO.existsByUserAndProduct(buyerId, productId));
    }

    @Test
    void duplicateAddViolatesUniqueConstraint() throws Exception {
        wishlistDAO.add(buyerId, productId);

        org.junit.jupiter.api.Assertions.assertThrows(java.sql.SQLException.class,
                () -> wishlistDAO.add(buyerId, productId));
    }
}
