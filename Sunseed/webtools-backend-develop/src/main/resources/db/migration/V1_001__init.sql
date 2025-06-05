CREATE TABLE user_otp
(
    otp_id          bigserial NOT NULL,
    otp             INTEGER,
    user_profile_id BIGINT,
    otp_status      VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ,
    CONSTRAINT pk_user_otp PRIMARY KEY (otp_id)
);

CREATE TABLE user_profile
(
    user_profile_id      bigserial NOT NULL,
    first_name           VARCHAR(255),
    last_name            VARCHAR(255),
    email_id             VARCHAR(255),
    phone_number         VARCHAR(255),
    profile_picture_path VARCHAR(255),
    user_id              BIGINT,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ,
    CONSTRAINT pk_user_profile PRIMARY KEY (user_profile_id)
);
