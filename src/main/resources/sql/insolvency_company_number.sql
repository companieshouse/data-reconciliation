SELECT DISTINCT cb.incorporation_number as result
FROM insolvency_case ic
INNER JOIN corporate_body cb ON cb.corporate_body_id = ic.corporate_body_id
WHERE (cb.trading_status_type_id IN (1, 2)
OR (cb.action_code_date >= TO_DATE('20100101', 'YYYYMMDD') AND cb.action_code_type_id >= 9000))
UNION ALL
SELECT DISTINCT cb.incorporation_number
FROM insolvency_case ic
INNER JOIN corporate_body cb ON cb.corporate_body_id = ic.corporate_body_id
INNER JOIN TRANSACTION t on cb.corporate_body_id = t.corporate_body_id
WHERE t.transaction_type_id in (541,542) and t.transaction_status_type_id=9
AND t.transaction_status_date = TO_DATE('05/01/2010','dd/mm/yyyy')
