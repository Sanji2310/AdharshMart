package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dto.OrderResponseDTO;
import com.adharsh.adharshmart.dto.SellerOrderItemDTO;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.OrderStatus;
import java.util.List;

/** F5 (checkout via mock payment) and F6 (order history for buyer and seller), plus O2 (status workflow). */
public interface OrderService {
    /** Places an order from the buyer's current cart contents; mock payment always confirms. */
    OrderResponseDTO checkout(Long buyerId) throws ValidationException;

    OrderResponseDTO getOrder(Long orderId, Long requesterId, boolean isAdmin)
            throws NotFoundException, UnauthorizedException;

    List<OrderResponseDTO> findByBuyer(Long buyerId);

    List<SellerOrderItemDTO> findIncomingForSeller(Long sellerId);

    void updateStatus(Long orderId, OrderStatus next) throws ValidationException;

    List<OrderResponseDTO> findAll();
}
