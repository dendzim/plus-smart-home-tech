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

CREATE TABLE IF NOT EXISTS warehouse.bookings (
    order_id UUID PRIMARY KEY,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS warehouse.bookings_items (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES warehouse.bookings (order_id) ON DELETE CASCADE
);