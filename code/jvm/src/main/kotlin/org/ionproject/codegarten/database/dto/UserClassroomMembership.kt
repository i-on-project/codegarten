package org.ionproject.codegarten.database.dto

data class UserClassroom(
    val role: UserClassroomMembership,
    val classroom: Classroom
)

enum class UserClassroomMembership {
    TEACHER,
    STUDENT,
    NOT_A_MEMBER
}
