SELECT
    c.id AS customer_id,
    c.name AS customer_name,
    SUM(p.amount) AS total_amount,
    COUNT(p.id) AS payment_count,
    ROUND(AVG(p.amount), 2) AS average_ticket
FROM customers c
INNER JOIN payments p
    ON p.customer_id = c.id
WHERE p.status = 'PROCESSED'
  AND p.created_at >= SYSTIMESTAMP - INTERVAL '30' DAY
GROUP BY
    c.id,
    c.name
ORDER BY
    total_amount DESC
FETCH FIRST 10 ROWS ONLY;