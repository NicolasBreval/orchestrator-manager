package org.nitb.orchestrator2.manager.exception

/**
 * Exception thrown when not exists a requested task.
 */
class TaskNotFoundException(
    msg: String
): Exception(msg)