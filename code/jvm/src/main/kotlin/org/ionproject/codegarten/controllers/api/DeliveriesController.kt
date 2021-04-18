package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.controllers.models.DeliveryCreateInputModel
import org.ionproject.codegarten.controllers.models.DeliveryEditInputModel
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
@RequestMapping("$ORGS_HREF/{orgId}/classrooms/{classroomId}/assignments/{assignmentId}/deliveries")
class DeliveriesController {

    @RequiresUserAuth
    @GetMapping
    fun getDeliveries(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @RequestParam(defaultValue = PAGE_DEFAULT_VALUE) page: Int,
        @RequestParam(defaultValue = COUNT_DEFAULT_VALUE) count: Int
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @GetMapping("{deliveryTag}")
    fun getDelivery(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable deliveryTag: String
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PostMapping
    fun createDelivery(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        input: DeliveryCreateInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping("{deliveryTag}")
    fun editDelivery(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable deliveryTag: String,
        input: DeliveryEditInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping("{deliveryTag}")
    fun deleteDelivery(
        @RequestAttribute(name = USER_ATTRIBUTE) user: UserDao,
        @PathVariable orgId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable deliveryTag: String
    ): ResponseEntity<Response> {
        TODO()
    }
}