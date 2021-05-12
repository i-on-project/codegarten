package org.ionproject.codegarten.database

import org.jdbi.v3.core.JdbiException
import java.sql.SQLException

enum class PsqlErrorCode(val code: String) {
    UniqueViolation("23505"),
    ForeignKeyViolation("23503"),
    CheckViolation("23514"),
    StringDataRightTruncation("22001")
}

fun JdbiException.getPsqlErrorCode(): PsqlErrorCode? {
    val cause = this.cause as SQLException
    return PsqlErrorCode.values().find { it.code == cause.sqlState }
}