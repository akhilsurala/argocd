CREATE TABLE if not exists static_pages(

    id                   bigserial PRIMARY KEY,
    title                VARCHAR(255),
   -- article_image_path  VARCHAR(255),
    description          TEXT,
    summary            VARCHAR(255),
    page_type        VARCHAR(255),
    created_by         BIGINT REFERENCES user_profile(user_profile_id),
    hide              BOOLEAN DEFAULT TRUE,
--    link                 VARCHAR(255)
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE

);