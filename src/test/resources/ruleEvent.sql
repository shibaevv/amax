use public;

-- DML (data) script
SET AUTOCOMMIT OFF;
BEGIN TRANSACTION;

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
  ('I1', 1, 1, 3, 2),
  ('I2', 1, 2, 3, 2)
) WHERE NOT EXISTS (SELECT 1 FROM amax_rule_input);

INSERT INTO series_data
  (series_data_date, key_id, series_data_value, currency_base, currency_code, series_data_title)
SELECT * FROM (VALUES
  ('20261124', 1, 10.0, 'EUR', 'AUD', null),
  ('20261124', 2, 20.0, 'EUR', 'AUD', null)
) WHERE NOT EXISTS (SELECT 1 FROM series_data);

COMMIT;
