package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass
import java.time.OffsetDateTime

class HomeOutputModel(
    val name: String,
    val description: String,
    val uptimeMs: Long,
    val time: OffsetDateTime,
) : OutputModel() {

    override fun getSirenClasses() = listOf(SirenClass.home)
}