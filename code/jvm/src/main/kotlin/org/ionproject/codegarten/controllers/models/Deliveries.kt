package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.collection
import org.ionproject.codegarten.responses.siren.SirenClass.delivery
import java.time.OffsetDateTime

class DeliveryOutputModel(
    val id: Int,
    val tag: String,
    val dueDate: OffsetDateTime?,
    val assignment: String,
    val classroom: String,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(delivery)
}

class UserDeliveryItemOutputModel(
    val id: Int,
    val tag: String,
    val dueDate: OffsetDateTime?,
    val isDelivered: Boolean,
    val assignment: String,
    val classroom: String,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(delivery)
}

class UserDeliveryOutputModel(
    val id: Int,
    val tag: String,
    val dueDate: OffsetDateTime?,
    val isDelivered: Boolean,
    val deliverDate: OffsetDateTime?,
    val assignment: String,
    val classroom: String,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(delivery)
}

class DeliveriesOutputModel(
    val collectionSize: Int,
    val pageIndex: Int,
    val pageSize: Int,
) : OutputModel() {
    override fun getSirenClasses() = listOf(delivery, collection)
}

data class DeliveryCreateInputModel(
    val tag: String?,
    val dueDate: String?,
)

data class DeliveryEditInputModel(
    val tag: String?,
    val dueDate: String?
)