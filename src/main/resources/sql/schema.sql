-- 1. Users
CREATE TABLE users
(
    id                    UUID                     NOT NULL PRIMARY KEY,
    created_at            TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name                  VARCHAR(50)              NOT NULL UNIQUE,
    email                 VARCHAR(100)             NOT NULL UNIQUE,
    password              VARCHAR(255)             NOT NULL,
    locked                BOOLEAN                  NOT NULL,
    role                  VARCHAR(50)              NOT NULL,
    is_temporary_password BOOLEAN                  NOT NULL
);

-- 2. Weathers
CREATE TABLE weathers
(
    id                  UUID                     NOT NULL PRIMARY KEY,
    forecasted_at       TIMESTAMP                NOT NULL,
    forecast_at         TIMESTAMP                NOT NULL,
    sky_status          VARCHAR(50)              NOT NULL,
    amount              DOUBLE PRECISION         NOT NULL,
    probability         DOUBLE PRECISION         NOT NULL,
    precipitation_type  VARCHAR(50)              NOT NULL,
    wind_speed          DOUBLE PRECISION         NOT NULL,
    as_word             VARCHAR(50)              NOT NULL,
    humidity            DOUBLE PRECISION         NOT NULL,
    current_temperature DOUBLE PRECISION         NOT NULL,
    max_temperature     DOUBLE PRECISION         NOT NULL,
    min_temperature     DOUBLE PRECISION         NOT NULL,
    x                   INTEGER                  NOT NULL,
    y                   INTEGER                  NOT NULL,
    created_at          TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE weather_locations
(
    id UUID    NOT NULL PRIMARY KEY,
    x  INTEGER NOT NULL,
    y  INTEGER NOT NULL,
    UNIQUE (x, y)
);

-- 3. Profiles
CREATE TABLE profiles
(
    id                      UUID                     NOT NULL PRIMARY KEY,
    user_id                 UUID                     NOT NULL,
    created_at              TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP with time zone          DEFAULT CURRENT_TIMESTAMP,
    gender                  VARCHAR(50),
    birth_date              DATE,
    profile_image_key       VARCHAR(1024),
    temperature_sensitivity INTEGER,
    latitude                DOUBLE PRECISION,
    longitude               DOUBLE PRECISION,
    location_name           VARCHAR(50)
);

CREATE TABLE profile_location_names
(
    profile_id    UUID NOT NULL,
    location_name VARCHAR(50),
    CONSTRAINT fk_profile_location FOREIGN KEY (profile_id) REFERENCES profiles (id)
);

-- 4. Feeds
CREATE TABLE feeds
(
    id         UUID                     NOT NULL PRIMARY KEY,
    author_id  UUID,
    weather_id UUID                     NOT NULL,
    created_at TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content    TEXT                     NOT NULL,
    like_count INTEGER                  NOT NULL DEFAULT 0
);

-- 5. Feed Comments
CREATE TABLE feed_comments
(
    id         UUID                     NOT NULL PRIMARY KEY,
    feed_id    UUID                     NOT NULL,
    author_id  UUID                     NOT NULL,
    created_at TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content    TEXT                     NOT NULL
);

-- 6. Clothes
CREATE TABLE clothes
(
    id         UUID                     NOT NULL PRIMARY KEY,
    owner_id   UUID                     NOT NULL,
    created_at TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name       VARCHAR(50)              NOT NULL UNIQUE,
    image_key  VARCHAR(1024)            NOT NULL,
    type       VARCHAR(50)              NOT NULL
);

-- 7. Attributes
CREATE TABLE attributes
(
    id              UUID        NOT NULL PRIMARY KEY,
    definition_name VARCHAR(50) NOT NULL UNIQUE
);

-- 8. Clothes Attributes
CREATE TABLE clothes_attributes
(
    id            UUID        NOT NULL PRIMARY KEY,
    clothes_id    UUID        NOT NULL,
    definition_id UUID        NOT NULL,
    value         VARCHAR(50) NOT NULL
);

-- 9. Feed-Clothes Association
CREATE TABLE feed_clothes
(
    id         UUID NOT NULL PRIMARY KEY,
    feed_id    UUID NOT NULL,
    clothes_id UUID NOT NULL
);

-- 10. Follows UNIQUE 제약 조건 동일한 유저가 동일한 유저를 팔로우할 수 없도록
CREATE TABLE follows
(
    id          UUID                     NOT NULL PRIMARY KEY,
    follower_id UUID                     NOT NULL,
    followee_id UUID                     NOT NULL,
    created_at  TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 11. Notifications
CREATE TABLE notifications
(
    id          UUID                     NOT NULL PRIMARY KEY,
    receiver_id UUID                     NOT NULL,
    created_at  TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    title       VARCHAR(50)              NOT NULL,
    content     VARCHAR(255)             NOT NULL,
    level       VARCHAR(50)              NOT NULL
);

-- 12. Feed Likes
CREATE TABLE feed_likes
(
    id      UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    feed_id UUID NOT NULL,
    CONSTRAINT uk_feed_like UNIQUE (user_id, feed_id)
);

-- 13. Attribute Selectable Values
CREATE TABLE attribute_selectable_value
(
    id            UUID         NOT NULL PRIMARY KEY,
    definition_id UUID         NOT NULL,
    value         VARCHAR(100) NOT NULL
);

-- 14. Direct Messages
CREATE TABLE direct_messages
(
    id          UUID                     NOT NULL PRIMARY KEY,
    receiver_id UUID                     NOT NULL,
    sender_id   UUID                     NOT NULL,
    content     TEXT                     NOT NULL,
    created_at  TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 15. JWT SESSIONS
CREATE TABLE jwt_sessions
(
    id              UUID                     NOT NULL PRIMARY KEY,
    user_id         UUID                     NOT NULL,
    access_token    VARCHAR(512)             NOT NULL UNIQUE,
    refresh_token   VARCHAR(512)             NOT NULL UNIQUE,
    expiration_Time TIMESTAMP with time zone NOT NULL,
    created_at      TIMESTAMP with time zone NOT NULL,
    updated_at      TIMESTAMP with time zone NOT NULL
);

-- --------------------------------------------------------
-- Foreign key constraints
-- --------------------------------------------------------

ALTER TABLE profiles
    ADD CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE feeds
    ADD CONSTRAINT fk_feeds_user
        FOREIGN KEY (author_id)
            REFERENCES users (id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_feeds_weather
        FOREIGN KEY (weather_id)
            REFERENCES weathers (id) ON
            DELETE
            CASCADE;

ALTER TABLE feed_comments
    ADD CONSTRAINT fk_feed_comment_feed FOREIGN KEY (feed_id) REFERENCES feeds (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_feed_comment_user FOREIGN KEY (author_id) REFERENCES users (id) ON
        DELETE
        CASCADE;

ALTER TABLE clothes
    ADD CONSTRAINT fk_clothes_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE clothes_attributes
    ADD CONSTRAINT fk_clothes_attributes_clothes FOREIGN KEY (clothes_id) REFERENCES clothes (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_clothes_attributes_definition FOREIGN KEY (definition_id) REFERENCES attributes (id) ON
        DELETE
        CASCADE;

ALTER TABLE feed_clothes
    ADD CONSTRAINT fk_feed_clothes_feed FOREIGN KEY (feed_id) REFERENCES feeds (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_feed_clothes_clothes FOREIGN KEY (clothes_id) REFERENCES clothes (id) ON
        DELETE
        CASCADE;

ALTER TABLE follows
    ADD CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_follows_followee FOREIGN KEY (followee_id) REFERENCES users (id) ON
        DELETE
        CASCADE;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_user FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE feed_likes
    ADD CONSTRAINT fk_feed_likes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_feed_likes_feed FOREIGN KEY (feed_id) REFERENCES feeds (id) ON
        DELETE
        CASCADE;

ALTER TABLE attribute_selectable_value
    ADD CONSTRAINT fk_attribute_selectable_value_definition FOREIGN KEY (definition_id) REFERENCES attributes (id) ON DELETE CASCADE;

ALTER TABLE direct_messages
    ADD CONSTRAINT fk_direct_messages_receiver FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_direct_messages_sender FOREIGN KEY (sender_id) REFERENCES users (id) ON
        DELETE
        CASCADE;
