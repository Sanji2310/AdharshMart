package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dto.CartItemDTO;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.ValidationException;
import java.math.BigDecimal;
import java.util.List;

/** F4 — cart add/update/remove with a running total. */
public interface CartService {
    List<CartItemDTO> getCart(Long userId);

    List<CartItemDTO> addItem(Long userId, Long productId, int quantity) throws ValidationException, NotFoundException;

    List<CartItemDTO> updateItem(Long userId, Long cartItemId, int quantity) throws ValidationException;

    List<CartItemDTO> removeItem(Long userId, Long cartItemId);

    BigDecimal runningTotal(List<CartItemDTO> items);
}
