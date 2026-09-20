package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dao.OrderDAO;
import com.adharsh.adharshmart.dao.ProductDAO;
import com.adharsh.adharshmart.dao.UserDAO;
import com.adharsh.adharshmart.dto.OrderResponseDTO;
import com.adharsh.adharshmart.dto.ProductDTO;
import com.adharsh.adharshmart.dto.UserResponseDTO;
import com.adharsh.adharshmart.exception.DataAccessException;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.model.Order;
import com.adharsh.adharshmart.model.OrderItem;
import com.adharsh.adharshmart.model.Product;
import com.adharsh.adharshmart.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/** Business rules for F7 — depends on DAO interfaces only. */
public class AdminServiceImpl implements AdminService {

    private final UserDAO userDAO;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public AdminServiceImpl(UserDAO userDAO, OrderDAO orderDAO, ProductDAO productDAO) {
        this.userDAO = userDAO;
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    @Override
    public List<UserResponseDTO> listUsers() {
        try {
            return userDAO.findAll().stream().map(UserResponseDTO::from).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list users", e);
        }
    }

    @Override
    public List<OrderResponseDTO> listOrders() {
        try {
            List<Order> orders = orderDAO.findAll();
            List<OrderResponseDTO> result = new java.util.ArrayList<>();
            for (Order order : orders) {
                List<OrderItem> items = orderDAO.findItemsByOrder(order.getId());
                OrderResponseDTO.Builder builder = OrderResponseDTO.builder()
                        .id(order.getId())
                        .buyerId(order.getBuyerId())
                        .status(order.getStatus())
                        .totalAmount(order.getTotalAmount())
                        .createdAt(order.getCreatedAt());
                for (OrderItem i : items) {
                    builder.addItem(new OrderResponseDTO.OrderItemDTO(
                            i.getProductId(), i.getProductName(), i.getQuantity(), i.getUnitPrice()));
                }
                result.add(builder.build());
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list orders", e);
        }
    }

    @Override
    public List<ProductDTO> listAllProducts() {
        try {
            List<Product> products = productDAO.findAllForAdmin();
            List<ProductDTO> result = new java.util.ArrayList<>();
            for (Product p : products) {
                ProductDTO dto = ProductDTO.from(p);
                userDAO.findById(p.getSellerId()).map(User::getName).ifPresent(dto::setSellerName);
                result.add(dto);
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list products", e);
        }
    }

    @Override
    public void removeListing(Long productId) throws NotFoundException {
        try {
            productDAO.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
            productDAO.delete(productId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to remove listing", e);
        }
    }
}
