package com.adharsh.adharshmart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.adharsh.adharshmart.dao.ProductColorDAO;
import com.adharsh.adharshmart.dao.ProductDAO;
import com.adharsh.adharshmart.dao.ProductImageDAO;
import com.adharsh.adharshmart.dao.ProductSizeDAO;
import com.adharsh.adharshmart.dao.ReviewDAO;
import com.adharsh.adharshmart.dto.ProductDTO;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
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
class ProductServiceImplTest {

    @Mock
    private ProductDAO productDAO;
    @Mock
    private ReviewDAO reviewDAO;
    @Mock
    private ProductImageDAO productImageDAO;
    @Mock
    private ProductSizeDAO productSizeDAO;
    @Mock
    private ProductColorDAO productColorDAO;

    private ProductService productService;

    @BeforeEach
    void setUp() throws Exception {
        productService = new ProductServiceImpl(productDAO, reviewDAO, productImageDAO, productSizeDAO, productColorDAO);
        lenient().when(productImageDAO.findByProduct(anyLong())).thenReturn(List.of());
        lenient().when(productSizeDAO.findByProduct(anyLong())).thenReturn(List.of());
        lenient().when(productColorDAO.findByProduct(anyLong())).thenReturn(List.of());
    }

    @Test
    void createRejectsNegativePrice() {
        ProductDTO dto = validDto();
        dto.setPrice(new BigDecimal("-1.00"));

        assertThrows(ValidationException.class, () -> productService.create(1L, dto));
    }

    @Test
    void createRejectsBlankName() {
        ProductDTO dto = validDto();
        dto.setName("  ");

        assertThrows(ValidationException.class, () -> productService.create(1L, dto));
    }

    @Test
    void updateRejectsWhenSellerDoesNotOwnListing() throws Exception {
        Product existing = new Product();
        existing.setId(10L);
        existing.setSellerId(2L); // owned by a different seller
        existing.setCreatedAt(LocalDateTime.now());
        when(productDAO.findById(10L)).thenReturn(Optional.of(existing));

        assertThrows(UnauthorizedException.class, () -> productService.update(1L, 10L, validDto()));
    }

    @Test
    void updateSucceedsForOwningSeller() throws Exception {
        Product existing = new Product();
        existing.setId(10L);
        existing.setSellerId(1L);
        existing.setCreatedAt(LocalDateTime.now());
        when(productDAO.findById(10L)).thenReturn(Optional.of(existing));
        lenient().when(reviewDAO.averageRating(anyLong())).thenReturn(0.0);
        lenient().when(reviewDAO.countForProduct(anyLong())).thenReturn(0);

        ProductDTO result = productService.update(1L, 10L, validDto());

        assertEquals("Test Product", result.getName());
    }

    @Test
    void getByIdThrowsNotFoundWhenMissing() throws Exception {
        when(productDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getById(99L));
    }

    private ProductDTO validDto() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Test Product");
        dto.setDescription("desc");
        dto.setPrice(new BigDecimal("100.00"));
        dto.setStockQty(5);
        dto.setCategory("Accessories");
        dto.setImageUrl("https://example.com/img.jpg");
        dto.setActive(true);
        return dto;
    }
}
