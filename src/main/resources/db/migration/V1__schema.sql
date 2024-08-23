CREATE TABLE Users (
       id SERIAL PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       email VARCHAR(255) UNIQUE NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Groups (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        created_by INTEGER REFERENCES Users(id),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE GroupMembers (
      id SERIAL PRIMARY KEY,
      group_id INTEGER REFERENCES Groups(id) ON DELETE CASCADE,
      user_id INTEGER REFERENCES Users(id) ON DELETE CASCADE,
      joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(group_id, user_id) -- Prevent duplicate user-group pairs
);

CREATE TABLE Messages (
      id SERIAL PRIMARY KEY,
      message_from INTEGER REFERENCES Users(id),
      message_to INTEGER, -- Can refer to either Users(id) or Groups(id)
      message_date DATE NOT NULL,
      message_time TIME NOT NULL,
      message TEXT NOT NULL,
      message_type VARCHAR(50) NOT NULL DEFAULT 'text',
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT fk_message_to_user FOREIGN KEY (message_to) REFERENCES Users(id) ON DELETE CASCADE,
      CONSTRAINT fk_message_to_group FOREIGN KEY (message_to) REFERENCES Groups(id) ON DELETE CASCADE
);

CREATE TABLE Attachments (
     id SERIAL PRIMARY KEY,
     message_id INTEGER REFERENCES Messages(id) ON DELETE CASCADE,
     file_path VARCHAR(255) NOT NULL,
     file_type VARCHAR(50) NOT NULL,
     uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ReadReceipts (
      id SERIAL PRIMARY KEY,
      message_id INTEGER REFERENCES Messages(id) ON DELETE CASCADE,
      user_id INTEGER REFERENCES Users(id) ON DELETE CASCADE,
      read_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(message_id, user_id) -- Prevent duplicate message-user pairs
);
