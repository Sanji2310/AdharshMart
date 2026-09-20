package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dao.ProductDAO;
import com.adharsh.adharshmart.dao.ReviewDAO;
import com.adharsh.adharshmart.dto.ProductDTO;
import com.adharsh.adharshmart.exception.DataAccessException;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Product;
import com.adharsh.adharshmart.util.ValidationUtil;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/** Business rules for F2/F3 — depends on {@link ProductDAO} and {@link ReviewDAO} interfaces. */
public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;
    private final ReviewDAO reviewDAO;

    public ProductServiceImpl(ProductDAO productDAO, ReviewDAO reviewDAO) {
        this.productDAO = productDAO;
        this.reviewDAO = reviewDAO;
    }

    @Override
    public ProductDTO create(Long sellerId, ProductDTO dto) throws ValidationException {
        validate(dto);
        try {
            Product product = dto.toEntity();
            product.setSellerId(sellerId);
            product.setActive(true);
            Product created = productDAO.create(product);
            return enrich(ProductDTO.from(created));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create product", e);
        }
    }

    @Override
    public ProductDTO update(Long sellerId, Long productId, ProductDTO dto)
            throws ValidationException, NotFoundException, UnauthorizedException {
        validate(dto);
        try {
            Product existing = productDAO.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            if (!existing.getSellerId().equals(sellerId)) {
                throw new UnauthorizedException("You do not own this listing", true);
            }
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            existing.setPrice(dto.getPrice());
            existing.setStockQty(dto.getStockQty());
            existing.setCategory(dto.getCategory());
            existing.setImageUrl(dto.getImageUrl());
            existing.setActive(dto.isActive());
            productDAO.update(existing);
            return enrich(ProductDTO.from(existing));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update product", e);
        }
    }

    @Override
    public void delete(Long sellerId, Long productId, boolean isAdmin) throws NotFoundException, UnauthorizedException {
        try {
            Product existing = productDAO.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            if (!isAdmin && !existing.getSellerId().equals(sellerId)) {
                throw new UnauthorizedException("You do not own this listing", true);
            }
            productDAO.delete(productId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete product", e);
        }
    }

    @Override
    public ProductDTO getById(Long productId) throws NotFoundException {
        try {
            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            return enrich(ProductDTO.from(product));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load product", e);
        }
    }

    @Override
    public List<ProductDTO> search(String keyword, String category) {
        try {
            return productDAO.search(keyword, category).stream()
                    .map(ProductDTO::from)
                    .map(this::enrich)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to search products", e);
        }
    }

    @Override
    public List<ProductDTO> findBySeller(Long sellerId) {
        try {
            return productDAO.findBySeller(sellerId).stream()
                    .map(ProductDTO::from)
                    .map(this::enrich)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load seller listings", e);
        }
    }

    private ProductDTO enrich(ProductDTO dto) {
        try {
            dto.setAverageRating(reviewDAO.averageRating(dto.getId()));
            dto.setReviewCount(reviewDAO.countForProduct(dto.getId()));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load review aggregates", e);
        }
        return dto;
    }

    private void validate(ProductDTO dto) throws ValidationException {
        if (ValidationUtil.isBlank(dto.getName())) {
            throw new ValidationException("name", "VALIDATION_ERROR", "Product name is required");
        }
        if (!ValidationUtil.isPositive(dto.getPrice())) {
            throw new ValidationException("price", "VALIDATION_ERROR", "Price must be zero or greater");
        }
        if (dto.getStockQty() < 0) {
            throw new ValidationException("stockQty", "VALIDATION_ERROR", "Stock quantity cannot be negative");
        }
        if (ValidationUtil.isBlank(dto.getCategory())) {
            throw new ValidationException("category", "VALIDATION_ERROR", "Category is required");
        }
    }
}
