package org.ionproject.codegarten.database.dto

enum class UserClassroomMembership {
    TEACHER,
    STUDENT,
    NOT_A_MEMBER
}

data class UserClassroom(
    val role: UserClassroomMembership,
    val classroom: Classroom
)