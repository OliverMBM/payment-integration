-- Supports the top customers query by filtering payments first by
-- status and creation date before joining/grouping by customer.
CREATE INDEX idx_payments_status_created_customer
    ON payments (status, created_at, customer_id);