-- aggregates incorporation numbers for corporate bodies and foreign branches
SELECT item as result
FROM (
    SELECT incorporation_number as item
    FROM corporate_body cb
    WHERE cb.trading_status_type_id IN (1, 2)
        OR substr(cb.incorporation_number,1,2)  IN  ('IP', 'SP', 'NP', 'NO', 'RS', 'R0', 'AC', 'SA', 'NA', 'IC', 'SI', 'NV', 'PC', 'SG', 'CE', 'RC', 'SR', 'NR', 'ZC', 'SZ', 'NZ' )
        OR ( cb.action_code_type_id >= 9000 
            AND ( SELECT coalesce(MAX(transaction_status_date) ,cb.action_code_date)
                      FROM DISSOLUTION_TRANSACTION tr
                      WHERE tr.corporate_body_id = cb.corporate_body_id
                      AND tr.transaction_type_id in (541,542,566))  >= to_date('01/01/2010','dd/mm/yyyy'))
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