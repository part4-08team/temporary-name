-- 1. Users
CREATE TABLE users (
                       user_id UUID NOT NULL PRIMARY KEY,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       name VARCHAR(50) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       locked BOOLEAN NOT NULL DEFAULT FALSE,
                       role VARCHAR(50) NOT NULL DEFAULT 'USER',
                       is_temporary_password BOOLEAN NOT NULL DEFAULT FALSE
);

-- 2. Untitled (grid/location reference)
CREATE TABLE untitled (
                          id UUID NOT NULL PRIMARY KEY,
                          x INTEGER NOT NULL,
                          y INTEGER NOT NULL,
                          location_name VARCHAR(50) NOT NULL
);

-- 3. Weathers
CREATE TABLE weathers (
                          weather_id UUID NOT NULL PRIMARY KEY,
                          id2 UUID NOT NULL,
                          forecasted_at TIMESTAMP NOT NULL,
                          forecast_at TIMESTAMP NOT NULL,
                          sky_status VARCHAR(50) NOT NULL,
                          amount DOUBLE PRECISION NOT NULL,
                          probability INTEGER NOT NULL,
                          precipitation_type VARCHAR(50) NOT NULL,
                          wind_speed DOUBLE PRECISION NOT NULL,
                          as_word VARCHAR(50) NOT NULL,
                          humidity DOUBLE PRECISION NOT NULL,
                          current_temperature DOUBLE PRECISION NOT NULL,
                          max_temperature DOUBLE PRECISION NOT NULL,
                          min_temperature DOUBLE PRECISION NOT NULL
);

-- 4. Profiles
CREATE TABLE profiles (
                          profile_id UUID NOT NULL PRIMARY KEY,
                          user_id UUID NOT NULL,
                          id2 UUID NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          gender VARCHAR(50),
                          birth_date DATE,
                          profile_image_url VARCHAR(1024),
                          temperature_sensitivity VARCHAR(50),
                          latitude DOUBLE PRECISION,
                          longitude DOUBLE PRECISION
);

-- 5. Feeds
CREATE TABLE feeds (
                       feed_id UUID NOT NULL PRIMARY KEY,
                       user_id UUID,
                       weather_id UUID NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       content TEXT
);

-- 6. Feed Comments
CREATE TABLE feed_comment (
                              feed_comment_id UUID NOT NULL PRIMARY KEY,
                              feed_id UUID NOT NULL,
                              user_id UUID NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              comment TEXT NOT NULL
);

-- 7. Clothes
CREATE TABLE clothes (
                         clothes_id UUID NOT NULL PRIMARY KEY,
                         owner_id UUID NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         name VARCHAR(50) NOT NULL UNIQUE,
                         image_url VARCHAR(1024) NOT NULL,
                         type VARCHAR(50) NOT NULL
);

-- 8. Attributes Definition
CREATE TABLE attributes (
                            definition_id UUID NOT NULL PRIMARY KEY,
                            definition_name VARCHAR(50) NOT NULL UNIQUE
);

-- 9. Clothes Attributes
CREATE TABLE clothes_attributes (
                                    clothes_attributes_id UUID NOT NULL PRIMARY KEY,
                                    clothes_id UUID NOT NULL,
                                    definition_id UUID NOT NULL,
                                    value VARCHAR(50) NOT NULL
);

-- 10. Feed–Clothes association
CREATE TABLE feed_clothes (
                              feed_clothes_id UUID NOT NULL PRIMARY KEY,
                              feed_id UUID NOT NULL,
                              clothes_id UUID NOT NULL
);

-- 11. Follows
CREATE TABLE follows (
                         follow_id UUID NOT NULL PRIMARY KEY,
                         follower_id UUID NOT NULL,
                         followee_id UUID NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 12. Notifications
CREATE TABLE notifications (
                               notification_id UUID NOT NULL PRIMARY KEY,
                               user_id UUID NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               title VARCHAR(50) NOT NULL,
                               content VARCHAR(255) NOT NULL,
                               level VARCHAR(50) NOT NULL
);

-- 13. Feed Likes
CREATE TABLE feed_likes (
                            feed_like_id UUID NOT NULL PRIMARY KEY,
                            user_id UUID NOT NULL,
                            feed_id UUID NOT NULL
);

-- 14. Attribute Selectable Values
CREATE TABLE attribute_selectable_value (
                                            id UUID NOT NULL PRIMARY KEY,
                                            definition_id UUID NOT NULL,
                                            value VARCHAR(100) NOT NULL
);

-- 15. Direct Messages
CREATE TABLE direct_messages (
                                 id UUID NOT NULL PRIMARY KEY,
                                 receiver_user_id UUID NOT NULL,
                                 sender_user_id UUID NOT NULL,
                                 content VARCHAR(100) NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------
-- Foreign key constraints (added after all tables created)
-- --------------------------------------------------------

ALTER TABLE weathers
    ADD CONSTRAINT fk_weathers_untitled
        FOREIGN KEY (id2) REFERENCES untitled(id);

ALTER TABLE profiles
    ADD CONSTRAINT fk_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_profiles_untitled
    FOREIGN KEY (id2) REFERENCES untitled(id);

ALTER TABLE feeds
    ADD CONSTRAINT fk_feeds_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
  ADD CONSTRAINT fk_feeds_weather
    FOREIGN KEY (weather_id) REFERENCES weathers(weather_id) ON DELETE CASCADE;

ALTER TABLE feed_comment
    ADD CONSTRAINT fk_feed_comment_feed
        FOREIGN KEY (feed_id) REFERENCES feeds(feed_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_feed_comment_user
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE clothes
    ADD CONSTRAINT fk_clothes_owner
        FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE clothes_attributes
    ADD CONSTRAINT fk_clothes_attributes_clothes
        FOREIGN KEY (clothes_id) REFERENCES clothes(clothes_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_clothes_attributes_definition
    FOREIGN KEY (definition_id) REFERENCES attributes(definition_id) ON DELETE CASCADE;

ALTER TABLE feed_clothes
    ADD CONSTRAINT fk_feed_clothes_feed
        FOREIGN KEY (feed_id) REFERENCES feeds(feed_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_feed_clothes_clothes
    FOREIGN KEY (clothes_id) REFERENCES clothes(clothes_id) ON DELETE CASCADE;

ALTER TABLE follows
    ADD CONSTRAINT fk_follows_follower
        FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_follows_followee
    FOREIGN KEY (followee_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE feed_likes
    ADD CONSTRAINT fk_feed_likes_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_feed_likes_feed
    FOREIGN KEY (feed_id) REFERENCES feeds(feed_id) ON DELETE CASCADE;

ALTER TABLE attribute_selectable_value
    ADD CONSTRAINT fk_attribute_selectable_value_definition
        FOREIGN KEY (definition_id) REFERENCES attributes(definition_id) ON DELETE CASCADE;

ALTER TABLE direct_messages
    ADD CONSTRAINT fk_direct_messages_receiver
        FOREIGN KEY (receiver_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_direct_messages_sender
    FOREIGN KEY (sender_user_id) REFERENCES users(user_id) ON DELETE CASCADE;
