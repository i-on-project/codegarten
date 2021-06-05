package org.ionproject.codegarten.database.dto

data class UserClassroomDto(
    val uid: Int,
    val name: String,
    val gh_id: Int,
    val gh_token: String,
    val classroom_role: String,
    val classroom_id: Int,

    val count: Int? = null
)

data class UserClassroom(
    val role: UserClassroomMembership,
    val classroom: Classroom,
    val user: User?
)

enum class UserClassroomMembership {
    TEACHER,
    STUDENT,
    NOT_A_MEMBER
}
