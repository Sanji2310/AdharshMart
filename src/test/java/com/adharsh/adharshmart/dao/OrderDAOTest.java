package com.adharsh.adharshmart.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adharsh.adharshmart.model.Order;
import com.adharsh.adharshmart.model.OrderItem;
import com.adharsh.adharshmart.model.OrderStatus;
import com.adharsh.adharshmart.model.Product;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.model.User;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderDAOTest {

    private HikariDataSource dataSource;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private Long buyerId;
    private Long productId;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = TestDb.freshInMemory();
        orderDAO = new OrderDAOImpl(dataSource);
        productDAO = new ProductDAOImpl(dataSource);
        UserDAO userDAO = new UserDAOImpl(dataSource);

        User seller = new User();
        seller.setName("Seller");
        seller.setEmail("seller.order.test@example.com");
        seller.setPasswordHash("hashed");
        seller.setRole(Role.SELLER);
        Long sellerId = userDAO.create(seller).getId();

        User buyer = new User();
        buyer.setName("Buyer");
        buyer.setEmail("buyer.order.test@example.com");
        buyer.setPasswordHash("hashed");
        buyer.setRole(Role.BUYER);
        buyerId = userDAO.create(buyer).getId();

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName("Test Jacket");
        product.setDescription("desc");
        product.setPrice(new BigDecimal("199.99"));
        product.setStockQty(3);
        product.setCategory("Outerwear");
        productId = productDAO.create(product).getId();
    }

    @AfterEach
    void tearDown() {
        dataSource.close();
    }

    @Test
    void placeOrderDecrementsStockAndPersistsItems() throws Exception {
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(new BigDecimal("399.98"));

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("199.99"));

        Order placed = orderDAO.placeOrder(order, List.of(item));

        assertTrue(placed.getId() > 0);
        List<OrderItem> items = orderDAO.findItemsByOrder(placed.getId());
        assertEquals(1, items.size());
        assertEquals(2, items.get(0).getQuantity());

        var product = productDAO.findById(productId).orElseThrow();
        assertEquals(1, product.getStockQty());
    }

    @Test
    void placeOrderRollsBackWhenStockInsufficient() throws Exception {
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(new BigDecimal("999.99"));

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(10); // exceeds stock of 3
        item.setUnitPrice(new BigDecimal("199.99"));

        assertThrows(OrderDAO.InsufficientStockException.class, () -> orderDAO.placeOrder(order, List.of(item)));

        // stock and order table must be untouched by the rolled-back transaction
        var product = productDAO.findById(productId).orElseThrow();
        assertEquals(3, product.getStockQty());
        assertEquals(0, orderDAO.findByBuyer(buyerId).size());
    }

    @Test
    void updateStatusPersists() throws Exception {
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(new BigDecimal("199.99"));
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("199.99"));
        Order placed = orderDAO.placeOrder(order, List.of(item));

        orderDAO.updateStatus(placed.getId(), OrderStatus.SHIPPED);

        var reloaded = orderDAO.findById(placed.getId()).orElseThrow();
        assertEquals(OrderStatus.SHIPPED, reloaded.getStatus());
    }
}
