-- aggregates officer ids for disqualified directors
SELECT DISTINCT officer_id as result
FROM officer_disqualification
WHERE TRUNC(sysdate) BETWEEN disqualification_eff_date AND disqualification_end_date
ORDER BY officer_id ASC