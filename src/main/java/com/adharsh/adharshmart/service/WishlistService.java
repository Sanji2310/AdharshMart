package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dto.WishlistItemDTO;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.ValidationException;
import java.util.List;

/** O1 — wishlist / save-for-later. */
public interface WishlistService {
    List<WishlistItemDTO> getWishlist(Long userId);

    WishlistItemDTO add(Long userId, Long productId) throws ValidationException, NotFoundException;

    void remove(Long userId, Long wishlistItemId);
}
