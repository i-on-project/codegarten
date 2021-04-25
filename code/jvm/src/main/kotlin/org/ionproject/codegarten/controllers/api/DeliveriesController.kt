package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.ASSIGNMENT_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.DELIVERIES_HREF
import org.ionproject.codegarten.Routes.DELIVERIES_OF_USER_HREF
import org.ionproject.codegarten.Routes.DELIVERY_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.DELIVERY_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.USER_PARAM
import org.ionproject.codegarten.controllers.models.DeliveryCreateInputModel
import org.ionproject.codegarten.controllers.models.DeliveryEditInputModel
import org.ionproject.codegarten.database.dto.User
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
class DeliveriesController {

    @RequiresUserAuth
    @GetMapping(DELIVERIES_HREF)
    fun getAllDeliveries(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping(DELIVERY_BY_NUMBER_HREF)
    fun getDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = DELIVERY_PARAM) deliveryNumber: Int,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping(DELIVERIES_OF_USER_HREF)
    fun getAllUserDeliveries(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PostMapping(DELIVERIES_HREF)
    fun createDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        user: User,
        input: DeliveryCreateInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping(DELIVERY_BY_NUMBER_HREF)
    fun editDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = DELIVERY_PARAM) deliveryNumber: Int,
        user: User,
        input: DeliveryEditInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping(DELIVERY_BY_NUMBER_HREF)
    fun deleteDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = DELIVERY_PARAM) deliveryNumber: Int,
        user: User,
    ): ResponseEntity<Response> {
        TODO()
    }
}