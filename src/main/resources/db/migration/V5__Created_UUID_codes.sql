CREATE TABLE uuid_codes (
    id SERIAL PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL,
    expiration_date TIMESTAMP WITH TIME ZONE,
    is_Activated BOOLEAN,
    user_id BIGINT REFERENCES users(id)
);
