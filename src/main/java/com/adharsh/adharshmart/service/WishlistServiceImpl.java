package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dao.ProductDAO;
import com.adharsh.adharshmart.dao.WishlistDAO;
import com.adharsh.adharshmart.dto.WishlistItemDTO;
import com.adharsh.adharshmart.exception.DataAccessException;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Product;
import com.adharsh.adharshmart.model.WishlistItem;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/** Business rules for O1 — depends on {@link WishlistDAO} and {@link ProductDAO} interfaces. */
public class WishlistServiceImpl implements WishlistService {

    private final WishlistDAO wishlistDAO;
    private final ProductDAO productDAO;

    public WishlistServiceImpl(WishlistDAO wishlistDAO, ProductDAO productDAO) {
        this.wishlistDAO = wishlistDAO;
        this.productDAO = productDAO;
    }

    @Override
    public List<WishlistItemDTO> getWishlist(Long userId) {
        try {
            List<WishlistItem> items = wishlistDAO.findByUser(userId);
            return items.stream().map(this::toDto).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load wishlist", e);
        }
    }

    @Override
    public WishlistItemDTO add(Long userId, Long productId) throws ValidationException, NotFoundException {
        if (productId == null) {
            throw new ValidationException("productId", "VALIDATION_ERROR", "productId is required");
        }
        try {
            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            if (!wishlistDAO.existsByUserAndProduct(userId, productId)) {
                wishlistDAO.add(userId, productId);
            }
            WishlistItem saved = wishlistDAO.findByUser(userId).stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Wishlist entry not found after save"));
            return toDto(saved, product);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save to wishlist", e);
        }
    }

    @Override
    public void remove(Long userId, Long wishlistItemId) {
        try {
            wishlistDAO.remove(wishlistItemId, userId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to remove wishlist item", e);
        }
    }

    private WishlistItemDTO toDto(WishlistItem item) {
        try {
            Product product = productDAO.findById(item.getProductId()).orElse(null);
            return toDto(item, product);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load product for wishlist item", e);
        }
    }

    private WishlistItemDTO toDto(WishlistItem item, Product product) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        if (product != null) {
            dto.setProductName(product.getName());
            dto.setImageUrl(product.getImageUrl());
            dto.setPrice(product.getPrice());
            dto.setInStock(product.isActive() && product.getStockQty() > 0);
        }
        return dto;
    }
}
