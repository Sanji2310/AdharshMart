package com.adharsh.adharshmart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adharsh.adharshmart.dao.ProductDAO;
import com.adharsh.adharshmart.dao.WishlistDAO;
import com.adharsh.adharshmart.dto.WishlistItemDTO;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Product;
import com.adharsh.adharshmart.model.WishlistItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @Mock
    private WishlistDAO wishlistDAO;
    @Mock
    private ProductDAO productDAO;

    private WishlistService wishlistService;

    @BeforeEach
    void setUp() {
        wishlistService = new WishlistServiceImpl(wishlistDAO, productDAO);
    }

    @Test
    void addRejectsMissingProductId() {
        assertThrows(ValidationException.class, () -> wishlistService.add(1L, null));
    }

    @Test
    void addThrowsNotFoundForUnknownProduct() throws Exception {
        when(productDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> wishlistService.add(1L, 99L));
    }

    @Test
    void addIsIdempotentForAlreadySavedProduct() throws Exception {
        Product product = new Product();
        product.setId(5L);
        product.setName("Coat");
        product.setPrice(new BigDecimal("100.00"));
        product.setActive(true);
        product.setStockQty(3);
        when(productDAO.findById(5L)).thenReturn(Optional.of(product));
        when(wishlistDAO.existsByUserAndProduct(1L, 5L)).thenReturn(true); // already saved

        WishlistItem existing = new WishlistItem();
        existing.setId(10L);
        existing.setProductId(5L);
        when(wishlistDAO.findByUser(1L)).thenReturn(List.of(existing));

        WishlistItemDTO result = wishlistService.add(1L, 5L);

        assertEquals(10L, result.getId());
        verify(wishlistDAO, org.mockito.Mockito.never()).add(anyLong(), anyLong());
    }
}
