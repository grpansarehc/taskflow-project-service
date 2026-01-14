CREATE TABLE projects (
                          project_id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          project_key VARCHAR(50) NOT NULL UNIQUE,
                          description VARCHAR(500),
                          type VARCHAR(50),
                          owner_id UUID NOT NULL,
                          created_at TIMESTAMP
);