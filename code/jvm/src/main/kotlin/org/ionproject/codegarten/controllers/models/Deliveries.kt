package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.collection
import org.ionproject.codegarten.responses.siren.SirenClass.delivery
import java.time.LocalDateTime

class DeliveryOutputModel(
    val tag: String,
    val due_date: LocalDateTime,
    val assignment: String
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
    val due_date: LocalDateTime?,
)

data class DeliveryEditInputModel(
    val due_date: LocalDateTime?,
)