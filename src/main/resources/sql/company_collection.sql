-- aggregates incorporation numbers for corporate bodies and foreign branches
SELECT item as result
FROM (
    SELECT incorporation_number as item
    FROM corporate_body cb
    WHERE cb.trading_status_type_id IN (1, 2)
        OR (cb.action_code_date >= TO_DATE('20100101', 'YYYYMMDD') AND cb.action_code_type_id >= 9000)
    UNION
    SELECT incorporation_number
    FROM corporate_body cb
    INNER JOIN transaction t ON t.corporate_body_id = cb.corporate_body_id
    WHERE t.transaction_type_id IN (541, 542) AND transaction_status_type_id = 9 AND transaction_status_date = TO_DATE('20100105','YYYYMMDD')
    UNION
    SELECT foreign_branch_number as item
    FROM (
        SELECT foreign_branch_number
        FROM foreign_branch fb
        INNER JOIN corporate_body cb ON fb.corporate_body_id = cb.corporate_body_id
        WHERE cb.trading_status_type_id IN (1, 2)
            OR (cb.action_code_date >= TO_DATE('20100101', 'YYYYMMDD') AND cb.action_code_type_id >= 9000)
    )
)