-- CREATE TASKS TABLE
CREATE TABLE IF NOT EXISTS tasks (
    `id` NUMERIC PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(500) NOT NULL,
    `creation_date` TIMESTAMP NOT NULL,
    `last_modification` TIMESTAMP,
    `active` BOOLEAN NOT NULL,
    `stopped` BOOLEAN NOT NULL,
    `type` VARCHAR(500) NOT NULL,
    UNIQUE KEY tasks_name_unique_constraint (`name`)
);

-- CREATE TASK_HISTORICALS TABLE
CREATE TABLE IF NOT EXISTS task_historicals (
    `id` NUMERIC PRIMARY KEY AUTO_INCREMENT,
    `date` TIMESTAMP NOT NULL,
    `parameters` BLOB NOT NULL,
    `username` VARCHAR(100) NOT NULL,
    `task_id` NUMERIC NOT NULL,
    `operation` VARCHAR(50) NOT NULL,
    FOREIGN KEY (`task_id`) REFERENCES tasks(`id`)
);