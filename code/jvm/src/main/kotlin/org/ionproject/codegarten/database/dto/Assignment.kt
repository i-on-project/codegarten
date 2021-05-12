package org.ionproject.codegarten.database.dto

data class Assignment(
    val aid: Int,
    val number: Int,
    val inv_code: String?,
    val name: String,
    val description: String?,
    val type: String,
    val repo_prefix: String,
    val repo_template: Int?,

    val org_id: Int,
    val classroom_id: Int,
    val classroom_number: Int,
    val classroom_name: String,
)

fun Assignment.isGroupAssignment() = this.type == "group"
fun Assignment.isIndividualAssignment() = this.type == "individual"