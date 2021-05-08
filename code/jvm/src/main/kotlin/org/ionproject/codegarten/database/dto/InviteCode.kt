package org.ionproject.codegarten.database.dto

data class InviteCode(
    val inv_code: String,
    val type: String,
    val assignment_id: Int,
    val classroom_id: Int,
    val org_id: Int,
)

fun InviteCode.isFromClassroom() = this.type == "classroom"
fun InviteCode.isFromAssignment() = this.type == "assignment"