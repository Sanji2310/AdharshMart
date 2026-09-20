package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dao.DAOFactory;
import com.adharsh.adharshmart.service.chat.ChatProviderFactory;

/** Factory pattern — wires services to their DAO dependencies; servlets depend on this, never on impls directly. */
public final class ServiceFactory {

    // ChatService holds per-session rate-limit/cache state, so it must be a process-wide singleton
    // rather than constructed fresh per request like the other services below.
    private static final ChatService CHAT_SERVICE = new ChatService(ChatProviderFactory.create());

    private ServiceFactory() {
    }

    public static ChatService chatService() {
        return CHAT_SERVICE;
    }

    public static UserService userService() {
        return new UserServiceImpl(DAOFactory.userDAO());
    }

    public static ProductService productService() {
        return new ProductServiceImpl(DAOFactory.productDAO(), DAOFactory.reviewDAO());
    }

    public static CartService cartService() {
        return new CartServiceImpl(DAOFactory.cartDAO(), DAOFactory.productDAO());
    }

    public static OrderService orderService() {
        return new OrderServiceImpl(DAOFactory.orderDAO(), DAOFactory.cartDAO(), DAOFactory.productDAO(),
                new MockPaymentStrategy());
    }

    public static ReviewService reviewService() {
        return new ReviewServiceImpl(DAOFactory.reviewDAO(), DAOFactory.orderDAO(), DAOFactory.userDAO());
    }

    public static AdminService adminService() {
        return new AdminServiceImpl(DAOFactory.userDAO(), DAOFactory.orderDAO(), DAOFactory.productDAO());
    }
}
