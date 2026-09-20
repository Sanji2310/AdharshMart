package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.listener.DataSourceListener;
import javax.sql.DataSource;

/**
 * Factory pattern — the single place that decides which DAO implementation backs each interface.
 * Service classes depend on the interfaces this factory returns, never on the impl classes
 * directly (SOLID: dependency inversion).
 */
public final class DAOFactory {

    private DAOFactory() {
    }

    private static DataSource dataSource() {
        DataSource ds = DataSourceListener.getDataSource();
        if (ds == null) {
            throw new IllegalStateException(
                    "DataSource not initialized — DataSourceListener.contextInitialized has not run");
        }
        return ds;
    }

    public static UserDAO userDAO() {
        return new UserDAOImpl(dataSource());
    }

    public static ProductDAO productDAO() {
        return new ProductDAOImpl(dataSource());
    }

    public static CartDAO cartDAO() {
        return new CartDAOImpl(dataSource());
    }

    public static OrderDAO orderDAO() {
        return new OrderDAOImpl(dataSource());
    }

    public static ReviewDAO reviewDAO() {
        return new ReviewDAOImpl(dataSource());
    }
}
