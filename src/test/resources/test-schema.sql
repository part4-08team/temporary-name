DROP TABLE IF EXISTS clothes_attributes CASCADE;
DROP TABLE IF EXISTS attributes CASCADE;
DROP TABLE IF EXISTS feed_clothes CASCADE;
DROP TABLE IF EXISTS clothes CASCADE;
DROP TABLE IF EXISTS feed_like CASCADE;
DROP TABLE IF EXISTS feed_comment CASCADE;
DROP TABLE IF EXISTS feeds CASCADE;
DROP TABLE IF EXISTS weathers CASCADE;
DROP TABLE IF EXISTS follows CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

DROP TYPE IF EXISTS location_type CASCADE;
DROP TYPE IF EXISTS temperature_type CASCADE;
DROP TYPE IF EXISTS precipitation_info_type CASCADE;
DROP TYPE IF EXISTS wind_speed_type CASCADE;
DROP TYPE IF EXISTS humidity_type CASCADE;
DROP TYPE IF EXISTS user_role_type CASCADE;
DROP TYPE IF EXISTS temperature_sensitivity_type CASCADE;
DROP TYPE IF EXISTS gender_type CASCADE;
DROP TYPE IF EXISTS level_type CASCADE;
DROP TYPE IF EXISTS sky_status_type CASCADE;
DROP TYPE IF EXISTS asWord_type CASCADE;
DROP TYPE IF EXISTS precipitation_type CASCADE;
DROP TYPE IF EXISTS clothes_type CASCADE;

-- Create extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE user_role_type AS ENUM ('USER', 'ADMIN');
CREATE TYPE temperature_sensitivity_type AS ENUM ('ZERO', 'ONE', 'TWO', 'THREE', 'FOUR', 'FIVE');
CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE level_type AS ENUM ('INFO', 'WARNING', 'ERROR');
CREATE TYPE sky_status_type AS ENUM ('CLEAR', 'MOSTLY_CLOUDY', 'CLOUDY');
CREATE TYPE asWord_type AS ENUM ('WEAK', 'MODERATE', 'STRONG');
CREATE TYPE precipitation_type AS ENUM ('NONE', 'RAIN', 'RAIN_SNOW', 'SNOW', 'SHOWER');
CREATE TYPE clothes_type AS ENUM ('TOP', 'BOTTOM', 'DRESS', 'OUTER', 'UNDERWEAR', 'ACCESSORY', 'SHOES', 'SOCKS', 'HAT', 'BAG', 'SCARF', 'ETC');

CREATE TYPE location_type AS (
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    x SMALLINT,
    y SMALLINT,
    location_names TEXT[]
    );


CREATE TYPE temperature_type AS (
    current_value DOUBLE PRECISION,
    compared_to_day_before DOUBLE PRECISION,
    min_value DOUBLE PRECISION,
    max_value DOUBLE PRECISION
    );

CREATE TYPE precipitation_info_type AS (
    type precipitation_type,
    amount DOUBLE PRECISION,
    probability DOUBLE PRECISION
    );

CREATE TYPE wind_speed_type AS (
    speed DOUBLE PRECISION,
    asWord asWord_type
    );

CREATE TYPE humidity_type AS (
    current_value DOUBLE PRECISION,
    compared_to_day_before DOUBLE PRECISION
    );

CREATE TABLE users
(
    user_id    UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    created_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    password   VARCHAR(255)     NOT NULL,
    email      VARCHAR(255)     NOT NULL,
    locked     BOOLEAN          NOT NULL DEFAULT FALSE,
    role       user_role_type   NOT NULL,
    is_temporary_password BOOLEAN NOT NULL
);

CREATE TABLE profiles
(
    profile_id              UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    user_id                 UUID             NOT NULL,
    created_at              TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name                    VARCHAR(255)     NOT NULL,
    gender                  gender_type,
    birth_date              DATE,
    profile_image_url         VARCHAR(1024),
    location                location_type,
    temperature_sensitivity temperature_sensitivity_type
);


CREATE TABLE notifications
(
    notification_id UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    user_id         UUID             NOT NULL,
    created_at      TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    title           VARCHAR(50)      NOT NULL,
    content         VARCHAR(255)     NOT NULL,
    level           level_type       NOT NULL
);


CREATE TABLE follows
(
    follower_id  UUID NOT NULL,
    following_id UUID NOT NULL,
    PRIMARY KEY (follower_id, following_id)
);


CREATE TABLE weathers
(
    weather_id    UUID PRIMARY KEY   NOT NULL DEFAULT gen_random_uuid(),
    forecasted_at TIMESTAMP          NOT NULL,
    forecast_at   TIMESTAMP          NOT NULL,
    sky_status    sky_status_type    NOT NULL,
    location      location_type      NOT NULL,
    precipitation precipitation_type NOT NULL,
    temperature   temperature_type   NOT NULL,
    wind_speed    wind_speed_type    NOT NULL,
    humidity      humidity_type      NOT NULL
);

CREATE TABLE feeds
(
    feed_id    UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    user_id    UUID             NOT NULL,
    content    TEXT,
    created_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feed_comment
(
    feed_comment_id UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    feed_id         UUID             NOT NULL,
    user_id         UUID             NOT NULL,
    comment         TEXT             NOT NULL,
    created_at      TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feed_like
(
    user_id UUID NOT NULL,
    feed_id UUID NOT NULL,
    PRIMARY KEY (user_id, feed_id)
);

CREATE TABLE clothes
(
    clothes_id UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    user_id    UUID             NOT NULL,
    name       VARCHAR(255)     NOT NULL,
    image_url  VARCHAR(1024),
    type       clothes_type     NOT NULL,
    created_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feed_clothes
(
    feed_id         UUID             NOT NULL,
    clothes_id      UUID             NOT NULL,
    PRIMARY KEY (feed_id, clothes_id)
);

CREATE TABLE attributes
(
    definition_id     UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    definition_name   VARCHAR(50)      NOT NULL,
    selectable_values TEXT[] NOT NULL
);

CREATE TABLE clothes_attributes
(
    clothes_attributes_id UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    clothes_id            UUID             NOT NULL,
    definition_id         UUID             NOT NULL,
    value                 VARCHAR(50)      NOT NULL
);


ALTER TABLE users
    ADD CONSTRAINT uq_users_email UNIQUE (email);

ALTER TABLE profiles
    ADD CONSTRAINT fk_profiles_users
        FOREIGN KEY (user_id) REFERENCES users (user_id)
            ON DELETE CASCADE;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_users
        FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE follows
    ADD CONSTRAINT fk_follows_follower_id_users
        FOREIGN KEY (follower_id) REFERENCES users (user_id)
            ON DELETE CASCADE;

ALTER TABLE follows
    ADD CONSTRAINT fk_follows_following_id_users
        FOREIGN KEY (following_id) REFERENCES users (user_id)
            ON DELETE CASCADE;


ALTER TABLE feeds
    ADD CONSTRAINT fk_feeds_users FOREIGN KEY (user_id) REFERENCES users (user_id);
-- ON DELETE CASCADE 넣을지 고민 (user 삭제 시 feed는 남겨 놓을지)

ALTER TABLE feed_comment
    ADD CONSTRAINT fk_feed_comment_feed
        FOREIGN KEY (feed_id) REFERENCES feeds (feed_id)
            ON DELETE CASCADE;

ALTER TABLE feed_comment
    ADD CONSTRAINT fk_feed_comment_users
        FOREIGN KEY (user_id) REFERENCES users (user_id)
            ON DELETE CASCADE;


ALTER TABLE feed_like
    ADD CONSTRAINT fk_feed_like_feed
        FOREIGN KEY (feed_id) REFERENCES feeds (feed_id)
            ON DELETE CASCADE;

ALTER TABLE feed_like
    ADD CONSTRAINT fk_feed_like_users
        FOREIGN KEY (user_id) REFERENCES users (user_id)
            ON DELETE CASCADE;

ALTER TABLE clothes
    ADD CONSTRAINT fk_clothes_users
        FOREIGN KEY (user_id) REFERENCES users (user_id)
            ON DELETE CASCADE;

ALTER TABLE feed_clothes
    ADD CONSTRAINT fk_feed_clothes_feed
        FOREIGN KEY (feed_id) REFERENCES feeds (feed_id)
            ON DELETE CASCADE;

ALTER TABLE feed_clothes
    ADD CONSTRAINT fk_feed_clothes_clothes
        FOREIGN KEY (clothes_id) REFERENCES clothes (clothes_id)
            ON DELETE CASCADE;

ALTER TABLE attributes
    ADD CONSTRAINT uq_attributes_definition_name UNIQUE (definition_name);
-- selectable_values 값 중복 확인은 그냥 application 계층에서 하기

ALTER TABLE clothes_attributes
    ADD CONSTRAINT fk_clothes_attributes_clothes
        FOREIGN KEY (clothes_id) REFERENCES clothes (clothes_id)
            ON DELETE CASCADE;

ALTER TABLE clothes_attributes
    ADD CONSTRAINT fk_clothes_attributes_attributes
        FOREIGN KEY (definition_id) REFERENCES attributes (definition_id)
            ON DELETE CASCADE;