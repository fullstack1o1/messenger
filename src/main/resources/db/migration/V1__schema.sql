CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       email VARCHAR(255) UNIQUE NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (name, email) VALUES ('Alice', 'alice@samitkumar.net');
INSERT INTO users (name, email) VALUES ('Bob', 'bol@samitkumar.net');
INSERT INTO users (name, email) VALUES ('Charlie', 'charlie@samitkumar.net');

CREATE TABLE groups (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        created_by INTEGER REFERENCES users(id),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_members (
      group_id INTEGER REFERENCES groups(id) ON DELETE CASCADE,
      user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
      joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(group_id, user_id) -- Prevent duplicate user-group pairs
);

CREATE TABLE messages (
      id SERIAL PRIMARY KEY,
      message_from INTEGER REFERENCES users(id),
      message_to INTEGER, -- Can refer to either users(id) or groups(id)
      message_to_type VARCHAR(10) NOT NULL, -- 'user' or 'group'
      message_date DATE DEFAULT CURRENT_DATE,
      message_time TIME DEFAULT CURRENT_TIME,
      message TEXT NOT NULL,
      message_type VARCHAR(50) NOT NULL DEFAULT 'TEXT',
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT fk_message_to_user FOREIGN KEY (message_to) REFERENCES users(id) ON DELETE CASCADE,
      CONSTRAINT fk_message_to_group FOREIGN KEY (message_to) REFERENCES groups(id) ON DELETE CASCADE,
      CHECK (message_to_type IN ('USER', 'GROUP')) -- Restrict values to 'user' or 'group'
);

CREATE TABLE attachments (
     id SERIAL PRIMARY KEY,
     message_id INTEGER REFERENCES messages(id) ON DELETE CASCADE,
     file_path VARCHAR(255) NOT NULL,
     file_type VARCHAR(50) NOT NULL,
     uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE read_receipts (
      id SERIAL PRIMARY KEY,
      message_id INTEGER REFERENCES messages(id) ON DELETE CASCADE,
      user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
      read_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(message_id, user_id) -- Prevent duplicate message-user pairs
);
