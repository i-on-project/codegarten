package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.CLASSROOMS_HREF
import org.ionproject.codegarten.Routes.CLASSROOM_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.controllers.models.ClassroomCreateInputModel
import org.ionproject.codegarten.controllers.models.ClassroomEditInputModel
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.helpers.ClassroomsDb
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.responses.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClassroomsController(val db: ClassroomsDb) {

    @RequiresUserAuth
    @GetMapping(CLASSROOMS_HREF)
    fun getUserClassrooms(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping(CLASSROOM_BY_NUMBER_HREF)
    fun getClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PostMapping(CLASSROOMS_HREF)
    fun createClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        user: User,
        input: ClassroomCreateInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping(CLASSROOM_BY_NUMBER_HREF)
    fun editClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        input: ClassroomEditInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping(CLASSROOM_BY_NUMBER_HREF)
    fun deleteClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }
}