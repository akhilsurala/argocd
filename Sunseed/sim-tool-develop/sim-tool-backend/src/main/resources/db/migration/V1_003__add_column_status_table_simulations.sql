ALTER TABLE IF EXISTS simulations ADD status varchar(20);

ALTER TABLE IF EXISTS simulations DROP COLUMN is_completed;
