-- DML (data) script
SET AUTOCOMMIT OFF;
BEGIN TRANSACTION;

use public;

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

INSERT INTO series_data_key
  (key_name, key_title, key_type, input_type, output_key_id, currency_code)
SELECT * FROM (VALUES
  ('R_01', 'Raw Key 01', 'R', null, null, 'AUD'),
  ('R_02', 'Raw Key 02', 'R', null, null, 'AUD'),
  ('A_01', 'Agg Key 01', 'A', null, null, 'AUD')
) WHERE NOT EXISTS (SELECT 1 FROM series_data_key);

INSERT INTO amax_rule
  (expression)
SELECT * FROM (VALUES
  ('#SUM( #I1, #I2 )')
) WHERE NOT EXISTS (SELECT 1 FROM amax_rule);

INSERT INTO amax_rule_input
  (name, rule_id, key_id, output_key_id, output_type)
SELECT * FROM (VALUES
  ('SUM 2 values', 1, 1, 3, 2),
  ('SUM 2 values', 1, 2, 3, 2)
) WHERE NOT EXISTS (SELECT 1 FROM amax_rule_input);

INSERT INTO series_data
  (series_data_date, key_id, series_data_value, currency_base, currency_code, series_data_title)
SELECT * FROM (VALUES
  ('20261124', 1, 10.0, 'EUR', 'AUD', null),
  ('20261124', 2, 20.0, 'EUR', 'AUD', null)
) WHERE NOT EXISTS (SELECT 1 FROM series_data);

COMMIT;
