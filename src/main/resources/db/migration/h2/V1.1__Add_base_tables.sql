/*
Table to register all workers connected to manager
*/
CREATE TABLE IF NOT EXISTS workers (
    `name` VARCHAR(500) PRIMARY KEY,
    `server_name` VARCHAR(500) NOT NULL,
    `server_port` NUMERIC NOT NULL,
    `last_event` TIMESTAMP NOT NULL,
    `is_connected` BOOLEAN DEFAULT TRUE,
    `cpu_number` NUMBER NOT NULL,
    `available_memory` NUMBER NOT NULL,
    `server_cpu_usage` NUMBER NOT NULL,
    `system_cpu_usage` NUMBER NOT NULL,
    `os_arch` VARCHAR(100) NOT NULL,
    `os_version` VARCHAR(100) NOT NULL,
    `build_revision` VARCHAR(500) NOT NULL,
    `worker_version` VARCHAR(500) NOT NULL
);

/*
Table which represents a task. This table contains only basic definition of task, like their name and type.
*/
CREATE TABLE IF NOT EXISTS tasks (
    `name` VARCHAR(500) NOT NULL,
    `type` VARCHAR(500) NOT NULL,
    `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `user_group` VARCHAR(100) NOT NULL,
    `worker` VARCHAR(500),
    PRIMARY KEY(`name`, `user_group`),
    FOREIGN KEY (`worker`) REFERENCES workers(`name`)
);

/*
Table to store all changes of tables registered in tasks table. This table is historical, so there are multiple
registers related to a task in tasks table.
*/
CREATE TABLE IF NOT EXISTS task_historicals (
    `id` NUMERIC PRIMARY KEY AUTO_INCREMENT,
    `parameters` BLOB NOT NULL,
    `is_orphan` BOOLEAN NOT NULL,
    `active` BOOLEAN NOT NULL,
    `stopped` BOOLEAN NOT NULL,
    `modification_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `task_name` VARCHAR(500),
    `task_user` VARCHAR(500),
    FOREIGN KEY (`task_name`, `task_user`) REFERENCES tasks(`name`, `user_group`)
);

/*
Table with groups of tasks. A group is only represented by a name and a user related to it. This table is not
incremental, so it's not possible to contain two registers with same name and user.
*/
CREATE TABLE IF NOT EXISTS task_groups (
    `name` VARCHAR(500) NOT NULL,
    `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `user_group` VARCHAR(100) NOT NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY(`name`, `user_group`)
);

/*
Relation table to make a MANY-TO-MANY relation between tasks and task_groups tables
*/
CREATE TABLE IF NOT EXISTS task_like (
    `task_name` VARCHAR(500) NOT NULL,
    `task_user` VARCHAR(500) NOT NULL,
    `group_name` VARCHAR(500) NOT NULL,
    `group_user` VARCHAR(500) NOT NULL,
    FOREIGN KEY (`task_name`, `task_user`) REFERENCES tasks(`name`, `user_group`),
    FOREIGN KEY (`group_name`, `group_user`) REFERENCES task_groups(`name`, `user_group`)
);