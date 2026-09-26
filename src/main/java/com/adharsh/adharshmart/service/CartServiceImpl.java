package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dao.CartDAO;
import com.adharsh.adharshmart.dao.ProductDAO;
import com.adharsh.adharshmart.dto.CartItemDTO;
import com.adharsh.adharshmart.exception.DataAccessException;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.CartItem;
import com.adharsh.adharshmart.model.Product;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/** Business rules for F4 — depends on {@link CartDAO} and {@link ProductDAO} interfaces. */
public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartServiceImpl(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public List<CartItemDTO> getCart(Long userId) {
        return toDTOs(userId);
    }

    @Override
    public List<CartItemDTO> addItem(Long userId, Long productId, int quantity)
            throws ValidationException, NotFoundException {
        if (quantity <= 0) {
            throw new ValidationException("quantity", "VALIDATION_ERROR", "Quantity must be positive");
        }
        try {
            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            if (!product.isActive()) {
                throw new NotFoundException("Product not available");
            }
            var existing = cartDAO.findByUserAndProduct(userId, productId);
            if (existing.isPresent()) {
                cartDAO.updateQuantity(existing.get().getId(), existing.get().getQuantity() + quantity);
            } else {
                CartItem item = new CartItem();
                item.setUserId(userId);
                item.setProductId(productId);
                item.setQuantity(quantity);
                cartDAO.create(item);
            }
            return toDTOs(userId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add cart item", e);
        }
    }

    @Override
    public List<CartItemDTO> updateItem(Long userId, Long cartItemId, int quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("quantity", "VALIDATION_ERROR", "Quantity must be positive");
        }
        try {
            cartDAO.updateQuantity(cartItemId, quantity);
            return toDTOs(userId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update cart item", e);
        }
    }

    @Override
    public List<CartItemDTO> removeItem(Long userId, Long cartItemId) {
        try {
            cartDAO.remove(cartItemId, userId);
            return toDTOs(userId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to remove cart item", e);
        }
    }

    @Override
    public BigDecimal runningTotal(List<CartItemDTO> items) {
        return items.stream().map(CartItemDTO::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<CartItemDTO> toDTOs(Long userId) {
        try {
            List<CartItem> items = cartDAO.findByUser(userId);
            return items.stream().map(this::enrich).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load cart", e);
        }
    }

    private CartItemDTO enrich(CartItem item) {
        try {
            Product product = productDAO.findById(item.getProductId()).orElse(null);
            CartItemDTO dto = new CartItemDTO();
            dto.setId(item.getId());
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            if (product != null) {
                dto.setProductName(product.getName());
                dto.setImageUrl(product.getImageUrl());
                dto.setUnitPrice(product.getPrice());
                dto.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            } else {
                dto.setUnitPrice(BigDecimal.ZERO);
                dto.setLineTotal(BigDecimal.ZERO);
            }
            return dto;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load product for cart item", e);
        }
    }
}
