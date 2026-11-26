use public;

-- DML (data) script
SET AUTOCOMMIT OFF;
BEGIN TRANSACTION;

INSERT INTO currency
  (currency_code, currency_title, currency_base)
SELECT * FROM (VALUES
  ('EUR', 'Euro', 'Y'),
  ('USD', 'United States Dollar', 'Y'),
  ('AUD', 'Australian Dollar', 'Y')
) WHERE NOT EXISTS (SELECT 1 FROM currency);

INSERT INTO amax_rule_input_type
  (type_id, type_code, type_title)
SELECT * FROM (VALUES
  ( 1, 'IN',    'Rule Input'),
  ( 2, 'OUT',   'Rule Output'),
  (10, 'G_IN',  'Rule Generated Input'),
  (20, 'G_OUT', 'Rule Generated Output')
) WHERE NOT EXISTS (SELECT 1 FROM amax_rule_input_type);

COMMIT;
