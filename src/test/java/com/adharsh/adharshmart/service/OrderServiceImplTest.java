package com.adharsh.adharshmart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adharsh.adharshmart.dao.CartDAO;
import com.adharsh.adharshmart.dao.OrderDAO;
import com.adharsh.adharshmart.dao.ProductDAO;
import com.adharsh.adharshmart.dto.OrderResponseDTO;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.CartItem;
import com.adharsh.adharshmart.model.Order;
import com.adharsh.adharshmart.model.OrderStatus;
import com.adharsh.adharshmart.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderDAO orderDAO;
    @Mock
    private CartDAO cartDAO;
    @Mock
    private ProductDAO productDAO;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderDAO, cartDAO, productDAO, new MockPaymentStrategy());
    }

    @Test
    void checkoutRejectsEmptyCart() throws Exception {
        when(cartDAO.findByUser(1L)).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> orderService.checkout(1L));
    }

    @Test
    void checkoutPlacesOrderFromCartContents() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(5L);
        cartItem.setQuantity(2);
        when(cartDAO.findByUser(1L)).thenReturn(List.of(cartItem));

        Product product = new Product();
        product.setId(5L);
        product.setName("Coat");
        product.setPrice(new BigDecimal("100.00"));
        product.setActive(true);
        when(productDAO.findById(5L)).thenReturn(Optional.of(product));

        Order placedOrder = new Order();
        placedOrder.setId(42L);
        placedOrder.setBuyerId(1L);
        placedOrder.setStatus(OrderStatus.CONFIRMED);
        placedOrder.setTotalAmount(new BigDecimal("200.00"));
        placedOrder.setCreatedAt(LocalDateTime.now());
        when(orderDAO.placeOrder(any(Order.class), any())).thenReturn(placedOrder);
        when(orderDAO.findItemsByOrder(42L)).thenReturn(List.of());

        OrderResponseDTO result = orderService.checkout(1L);

        assertEquals(42L, result.getId());
        assertEquals(0, new BigDecimal("200.00").compareTo(result.getTotalAmount()));
        verify(cartDAO).clearByUser(1L);
    }

    @Test
    void updateStatusRejectsInvalidTransition() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));

        // PENDING can only move to CONFIRMED or CANCELLED, never straight to DELIVERED
        assertThrows(ValidationException.class, () -> orderService.updateStatus(1L, OrderStatus.DELIVERED));
    }

    @Test
    void updateStatusAllowsValidTransition() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));

        orderService.updateStatus(1L, OrderStatus.CONFIRMED);

        verify(orderDAO).updateStatus(1L, OrderStatus.CONFIRMED);
    }
}
