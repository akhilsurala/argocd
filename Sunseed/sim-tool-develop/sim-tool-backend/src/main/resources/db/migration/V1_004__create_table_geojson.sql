CREATE TABLE IF NOT EXISTS geo_json (
    longitude		numeric,
    latitude		numeric,
    title		varchar(255),
    epw_file_url	text,
    PRIMARY KEY (longitude, latitude)
);
