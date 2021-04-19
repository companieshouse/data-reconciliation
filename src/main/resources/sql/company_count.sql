-- count corporate bodies and foreign branches
SELECT SUM(cb_count) as result
FROM (
    SELECT COUNT(incorporation_number) as cb_count
    FROM (
        SELECT incorporation_number
        FROM corporate_body cb
        WHERE cb.trading_status_type_id IN (1, 2) --trading and dormant companies
            OR (cb.action_code_date >= TO_DATE('20100101', 'YYYYMMDD') AND cb.action_code_type_id >= 9000) --exclude companies dissolved before 01/01/2010
        UNION
        -- include companies that were in the process of being struck off
        SELECT incorporation_number
        FROM corporate_body cb
        INNER JOIN transaction t ON t.corporate_body_id = cb.corporate_body_id
        WHERE t.transaction_type_id IN (541, 542)
            AND transaction_status_type_id = 9
            AND transaction_status_date = TO_DATE('20100105','YYYYMMDD')
    )
    UNION ALL
    SELECT COUNT(foreign_branch_number) as cb_count
    FROM (
        SELECT foreign_branch_number
        FROM foreign_branch fb
        INNER JOIN corporate_body cb ON fb.corporate_body_id = cb.corporate_body_id
        WHERE cb.trading_status_type_id IN (1, 2)
            OR (cb.action_code_date >= TO_DATE('20100101', 'YYYYMMDD') AND cb.action_code_type_id >= 9000)
    )
)