package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.USER_HREF
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType
import org.springframework.http.HttpMethod
import java.net.URI

object UserActions {

    fun getEditUserAction() = SirenAction(
        name = "edit-user",
        title = "Edit User",
        method = HttpMethod.PUT,
        href = URI(USER_HREF).includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "name", type = SirenFieldType.text),
        )
    )

    fun getDeleteUserAction() = SirenAction(
        name = "delete-user",
        title = "Delete User",
        method = HttpMethod.DELETE,
        href = URI(USER_HREF).includeHost()
    )
}