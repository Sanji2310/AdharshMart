package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dto.ProductDTO;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import java.util.List;

/** F2 (seller CRUD) and F3 (buyer browse/search). */
public interface ProductService {
    ProductDTO create(Long sellerId, ProductDTO dto) throws ValidationException;

    ProductDTO update(Long sellerId, Long productId, ProductDTO dto)
            throws ValidationException, NotFoundException, UnauthorizedException;

    void delete(Long sellerId, Long productId, boolean isAdmin) throws NotFoundException, UnauthorizedException;

    ProductDTO getById(Long productId) throws NotFoundException;

    List<ProductDTO> search(String keyword, String category);

    List<ProductDTO> findBySeller(Long sellerId);
}
