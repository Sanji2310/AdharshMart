package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dto.OrderResponseDTO;
import com.adharsh.adharshmart.dto.ProductDTO;
import com.adharsh.adharshmart.dto.UserResponseDTO;
import com.adharsh.adharshmart.exception.NotFoundException;
import java.util.List;

/** F7 — admin view of all users/orders/inventory and listing moderation. */
public interface AdminService {
    List<UserResponseDTO> listUsers();

    List<OrderResponseDTO> listOrders();

    /** Marketplace-wide inventory: every product across every seller, active or not. */
    List<ProductDTO> listAllProducts();

    void removeListing(Long productId) throws NotFoundException;
}
