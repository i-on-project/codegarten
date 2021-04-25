package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.ASSIGNMENT_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.USERS_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.USERS_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USER_HREF
import org.ionproject.codegarten.Routes.USER_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.USER_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USER_PARAM
import org.ionproject.codegarten.controllers.models.UserAddInputModel
import org.ionproject.codegarten.controllers.models.UserEditInputModel
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.responses.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UsersController(db: UsersDb) {

    @RequiresUserAuth
    @GetMapping(USER_HREF)
    fun getUser(
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping(USER_HREF)
    fun editUser(
        user: User,
        input: UserEditInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping(USER_HREF)
    fun deleteUser(
        user: User,
        input: UserEditInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    // Users of Classrooms Handlers
    @RequiresUserAuth
    @GetMapping(USERS_OF_CLASSROOM_HREF)
    fun getUsersOfClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping(USERS_OF_CLASSROOM_HREF)
    fun addUserToClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        input: UserAddInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping(USER_OF_CLASSROOM_HREF)
    fun removeUserFromClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    // Users of Assignments Handlers
    @RequiresUserAuth
    @GetMapping(USERS_OF_ASSIGNMENT_HREF)
    fun getUsersOfAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping(USERS_OF_ASSIGNMENT_HREF)
    fun addUserToAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        user: User,
        input: UserAddInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping(USER_OF_ASSIGNMENT_HREF)
    fun removeUserFromAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User,
    ): ResponseEntity<Response> {
        TODO()
    }
}