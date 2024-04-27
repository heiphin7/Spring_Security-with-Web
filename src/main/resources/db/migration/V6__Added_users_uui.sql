CREATE TABLE IF NOT EXISTS users_uuid_codes (
    user_id BIGINT REFERENCES users(id),
    uuid_id BIGINT REFERENCES uuid_codes(id),
    PRIMARY KEY (user_id, uuid_id)
);

