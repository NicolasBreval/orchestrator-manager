package org.nitb.orchestrator2.exception

class GroupNotFoundException(msg: String? = null, throwable: Throwable? = null): Exception(msg, throwable)

class DeleteTasksInvalidParametersException(msg: String? = null, throwable: Throwable? = null): Exception(msg, throwable)

class NotAvailableWorkersException(msg: String? = null, throwable: Throwable? = null): Exception(msg, throwable)

class UnableToUploadTasksInWorkersException(msg: String? = null, throwable: Throwable? = null): Exception(msg, throwable)