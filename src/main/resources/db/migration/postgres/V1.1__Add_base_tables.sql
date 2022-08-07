-- CREATE TASKS TABLE
CREATE TABLE IF NOT EXISTS tasks (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(500) NOT null UNIQUE ,
    "creation_date" TIMESTAMP NOT NULL,
    "last_modification" TIMESTAMP,
    "active" BOOLEAN NOT NULL,
    "stopped" BOOLEAN NOT NULL,
    "type" VARCHAR(50) NOT NULL
);

-- CREATE TASK_HISTORICALS TABLE
CREATE TABLE IF NOT EXISTS task_historicals (
    "id" BIGSERIAL PRIMARY KEY,
    "date" TIMESTAMP NOT NULL,
    "parameters" OID NOT NULL,
    "username" VARCHAR(100) NOT NULL,
    "task_id" BIGSERIAL NOT NULL,
    "operation" VARCHAR(50) NOT NULL,
    FOREIGN KEY ("task_id") REFERENCES tasks("id")
);