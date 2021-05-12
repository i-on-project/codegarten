package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.collection
import org.ionproject.codegarten.responses.siren.SirenClass.organization

class OrganizationOutputModel(
    val id: Int,
    val name: String,
    val description: String?
) : OutputModel() {
    override fun getSirenClasses() = listOf(organization)
}

class OrganizationsOutputModel(
    val pageIndex: Int,
    val pageSize: Int
) : OutputModel() {
    override fun getSirenClasses() = listOf(organization, collection)
}