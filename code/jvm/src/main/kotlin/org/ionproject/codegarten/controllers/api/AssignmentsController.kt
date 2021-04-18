package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.controllers.models.AssignmentCreateInputModel
import org.ionproject.codegarten.controllers.models.AssignmentEditInputModel
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
@RequestMapping("$ORGS_HREF/{orgId}/classrooms/{classroomId}/assignments")
class AssignmentsController : BaseApiController() {

    @RequiresUserAuth
    @GetMapping
    fun getAssignments(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @RequestParam(defaultValue = PAGE_DEFAULT_VALUE) page: Int,
        @RequestParam(defaultValue = COUNT_DEFAULT_VALUE) count: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping("{assignmentId}")
    fun getAssignment(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping("{assignmentId}/users")
    fun getUsersOfAssignment(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PostMapping
    fun createAssignment(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        input: AssignmentCreateInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping("{assignmentId}")
    fun editAssignment(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        input: AssignmentEditInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping("{assignmentId}")
    fun deleteAssignment(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping("{assignmentId}/users")
    fun addUserToAssignment(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        input: UserAddInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping("{assignmentId}/users/{uid}")
    fun removeUserFromAssignment(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable userId: Int,
    ): ResponseEntity<Response> {
        TODO()
    }
}