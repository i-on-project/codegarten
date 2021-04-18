package org.ionproject.codegarten.controllers.im

import com.sun.jndi.toolkit.url.Uri
import org.springframework.http.MediaType

const val AUTH_HREF = "im/oauth"
const val GH_INSTALLATIONS_HREF = "im/github/install"

val INPUT_CONTENT_TYPE = MediaType.APPLICATION_FORM_URLENCODED

abstract class BaseImController {
    companion object {
        fun redirectToUri(uri: Uri) { TODO() }
    }
}