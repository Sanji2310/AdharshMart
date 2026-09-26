-- V3: supports the O2 order-status workflow query pattern (filter by status)
CREATE INDEX idx_orders_status ON orders (status);
