CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN,
    width DECIMAL(8, 4),
    height DECIMAL(8, 4),
    depth DECIMAL(8, 4),
    weight DECIMAL(8, 4),
    quantity INTEGER DEFAULT 0
);