package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.controllers.models.UserEditInputModel
import org.ionproject.codegarten.database.dao.UserDao
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.pipeline.interceptors.USER_ATTRIBUTE
import org.ionproject.codegarten.responses.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(USER_HREF)
class UsersController : BaseApiController() {

    @RequiresUserAuth
    @GetMapping
    fun getUser(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping("classrooms")
    fun getUserClassrooms(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @RequestParam(defaultValue = PAGE_DEFAULT_VALUE) page: Int,
        @RequestParam(defaultValue = COUNT_DEFAULT_VALUE) count: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping("assignments")
    fun getUserAssignments(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @RequestParam(defaultValue = PAGE_DEFAULT_VALUE) page: Int,
        @RequestParam(defaultValue = COUNT_DEFAULT_VALUE) count: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping("classrooms/{classroomId}/assignments")
    fun getUserAssignmentsOfClassroom(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable classroomId: Int,
        @RequestParam(defaultValue = PAGE_DEFAULT_VALUE) page: Int,
        @RequestParam(defaultValue = COUNT_DEFAULT_VALUE) count: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping
    fun editUser(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        input: UserEditInputModel
    ): ResponseEntity<Response> {
        TODO()
    }
}