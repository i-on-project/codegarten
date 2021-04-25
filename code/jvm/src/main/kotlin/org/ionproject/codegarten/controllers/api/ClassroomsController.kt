package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.controllers.models.ClassroomCreateInputModel
import org.ionproject.codegarten.controllers.models.ClassroomEditInputModel
import org.ionproject.codegarten.controllers.models.UserAddInputModel
import org.ionproject.codegarten.database.dao.UserDao
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.pipeline.interceptors.USER_ATTRIBUTE
import org.ionproject.codegarten.responses.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$ORGS_HREF/{orgId}/classrooms")
class ClassroomsController : BaseApiController() {

    @RequiresUserAuth
    @GetMapping
    fun getClassrooms(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @RequestParam(defaultValue = PAGE_DEFAULT_VALUE) page: Int,
        @RequestParam(defaultValue = COUNT_DEFAULT_VALUE) count: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping("{classroomId}")
    fun getClassroom(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping("{classroomId}/users")
    fun getUsersOfClassroom(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PostMapping
    fun createClassroom(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        input: ClassroomCreateInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping("{classroomId}")
    fun editClassroom(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @RequestParam(defaultValue = PAGE_DEFAULT_VALUE) page: Int,
        @RequestParam(defaultValue = COUNT_DEFAULT_VALUE) count: Int,
        input: ClassroomEditInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping("{classroomId}")
    fun deleteClassroom(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping("{classroomId}/users")
    fun addUserToClassroom(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        input: UserAddInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping("{classroomId}/users/{uid}")
    fun removeUserFromClassroom(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable userId: Int,
    ): ResponseEntity<Response> {
        TODO()
    }
}