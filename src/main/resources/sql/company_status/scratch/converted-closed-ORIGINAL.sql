SELECT INCORPORATION_NUMBER
FROM CORPORATE_BODY CB
WHERE trading_status_type_id=5
  AND SUBSTR(incorporation_number,1,2) NOT IN 'SE'
  AND ACTION_CODE_DATE >= TO_DATE ('30-DEC-2009','DD-MON-YYYY')
UNION
SELECT DISTINCT INCORPORATION_NUMBER
FROM CORPORATE_BODY cb
WHERE ACTION_CODE_TYPE_ID = 9100
  AND TRADING_STATUS_TYPE_ID = 5
  AND TRADING_STATUS_SUB_TYPE_ID = 2
  AND ACTION_CODE_DATE >= '30-DEC-2009'
UNION
SELECT DISTINCT INCORPORATION_NUMBER
FROM CORPORATE_BODY cb
WHERE ACTION_CODE_TYPE_ID = 9100
  AND SUBSTR (INCORPORATION_NUMBER, 1, 2)= 'SE'
  AND TRADING_STATUS_SUB_TYPE_ID = 3
  AND ACTION_CODE_DATE >= '30-DEC-2009'
