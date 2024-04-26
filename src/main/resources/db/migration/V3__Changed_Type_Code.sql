-- Изменение типа данных code из BIGINT (Long) на VARCHAR(6) (String)

ALTER TABLE verificationCode
    ALTER COLUMN code TYPE VARCHAR(6);