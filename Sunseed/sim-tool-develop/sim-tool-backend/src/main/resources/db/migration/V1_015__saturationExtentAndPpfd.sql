ALTER TABLE IF EXISTS pv_yields
ADD COLUMN ppfd numeric;

ALTER TABLE IF EXISTS crop_yields
ADD COLUMN saturation_extent numeric;