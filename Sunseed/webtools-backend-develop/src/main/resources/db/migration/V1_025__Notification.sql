CREATE TABLE IF NOT EXISTS notifications(
    id              bigserial NOT NULL PRIMARY KEY,
    message         VARCHAR(255),
    source_id          BIGINT REFERENCES user_profile(user_profile_id),
    destination_id  BIGINT REFERENCES user_profile(user_profile_id),
    mark_as_read    BOOLEAN,
    link     VARCHAR(255),
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE
)
