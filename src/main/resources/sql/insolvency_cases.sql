  SELECT DISTINCT cb.incorporation_number, COUNT(NULLIF(10, ic.insolvency_case_type_id)) AS number_of_cases
  FROM corporate_body cb
  INNER JOIN insolvency_case ic ON cb.corporate_body_id = ic.corporate_body_id
  WHERE (cb.trading_status_type_id IN (1, 2)
  OR (cb.action_code_date >= TO_DATE('20091231', 'YYYYMMDD')
  AND cb.action_code_type_id >= 9000))
  AND ic.insolvency_case_type_id <> 10
  GROUP BY cb.incorporation_number
  UNION ALL
  SELECT DISTINCT cb.incorporation_number, COUNT(NULLIF(10, ic.insolvency_case_type_id)) AS number_of_cases
  FROM corporate_body cb
  INNER JOIN insolvency_case ic ON cb.corporate_body_id = ic.corporate_body_id
  INNER JOIN transaction t ON t.corporate_body_id = cb.corporate_body_id WHERE t.transaction_type_id IN (541,542)
  AND t.transaction_status_type_id=9
  AND t.transaction_status_date = TO_DATE('05/01/2010','dd/mm/yyyy')
  AND ic.insolvency_case_type_id <> 10
  GROUP BY cb.incorporation_number ORDER BY number_of_cases ASC