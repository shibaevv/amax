-- DML (data) script
SET AUTOCOMMIT OFF;
BEGIN TRANSACTION;

use public;

INSERT INTO series_data_key
  (key_name, key_title, key_type, input_type, output_key_id, currency_code)
SELECT * FROM (VALUES
  ('KEY_01', 'Key 01', '1', 1, null, 'AUD')
) WHERE NOT EXISTS (SELECT 1 FROM series_data_key);

COMMIT;
