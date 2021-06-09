SELECT CB.INCORPORATION_NUMBER AS INCORPORATION_NUMBER
FROM CORPORATE_BODY CB
WHERE CB.ACTION_CODE_TYPE_ID = 9000
  AND CB.TRADING_STATUS_TYPE_ID = 3
  AND CB.ACTION_CODE_DATE >= TO_DATE('30-DEC-2009', 'DD-MON-YYYY')