package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.collection
import org.ionproject.codegarten.responses.siren.SirenClass.delivery
import java.time.OffsetDateTime

class DeliveryOutputModel(
    val id: Int,
    val number: Int,
    val tag: String,
    val dueDate: OffsetDateTime?,
    val assignment: String,
    val classroom: String,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(delivery)
}

class ParticipantDeliveryItemOutputModel(
    val id: Int,
    val number: Int,
    val tag: String,
    val dueDate: OffsetDateTime?,
    val isDelivered: Boolean,
    val assignment: String,
    val classroom: String,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(delivery)
}

class ParticipantDeliveryOutputModel(
    val id: Int,
    val number: Int,
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
    val assignment: String,
    val classroom: String,
    val organization: String,
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