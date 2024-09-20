CREATE TABLE users (
       user_id SERIAL PRIMARY KEY,
       username VARCHAR(50) UNIQUE NOT NULL,
       email VARCHAR(100) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       created_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO users (username, email, password) VALUES ('Alice', 'alice@samitkumar.net', '{noop}password');
INSERT INTO users (username, email, password) VALUES ('Bob', 'bol@samitkumar.net', '{noop}password');
INSERT INTO users (username, email, password) VALUES ('Charlie', 'charlie@samitkumar.net', '{noop}password');

CREATE TABLE groups (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    created_by INT REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT NOW()
);


CREATE TABLE group_members (
       group_id INT REFERENCES groups(group_id) ON DELETE CASCADE,
       user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
       joined_at TIMESTAMP DEFAULT NOW(),
       PRIMARY KEY (group_id, user_id)
);


CREATE TABLE messages (
    message_id SERIAL PRIMARY KEY,
    sender_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    receiver_id INT REFERENCES users(user_id) ON DELETE CASCADE, -- For direct messages (nullable)
    group_id INT REFERENCES groups(group_id) ON DELETE CASCADE,  -- For group messages (nullable)
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    -- Ensure either receiver_id or group_id is set, not both
    CHECK ((receiver_id IS NOT NULL AND group_id IS NULL) OR (receiver_id IS NULL AND group_id IS NOT NULL))
);
