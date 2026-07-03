CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE IF NOT EXISTS payment.payments (
    payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_payment DECIMAL(19, 2),
    delivery_total DECIMAL(19, 2),
    product_total DECIMAL(19, 2),
    payment_state VARCHAR(50)
);