package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.remote.GitHubInterface
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.siren.SirenLink
import org.ionproject.codegarten.utils.EnvironmentInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.net.URI

const val COUNT_DEFAULT_VALUE = "10"
const val PAGE_DEFAULT_VALUE = "0"

const val ORGS_HREF = "api/orgs"
const val USER_HREF = "api/user"
const val AUTH_HREF = "api/oauth"

val INPUT_CONTENT_TYPE = MediaType.APPLICATION_FORM_URLENCODED

abstract class BaseApiController {
    companion object {
        fun createResponseEntity(response: Response, status: Int) = ResponseEntity
            .status(status)
            .contentType(MediaType.parseMediaType(response.getContentType()))
            .body(response)

        fun createUriListForPagination(baseUri: String, pageIndex: Int, pageSize: Int, count: Int, collectionSize: Int): List<SirenLink> {
            val toReturn = mutableListOf(
                SirenLink(listOf("self"), URI("${baseUri}?page=${pageIndex}&count=${count}")),
                SirenLink(listOf("page"), hrefTemplate = "${baseUri}{?pageIndex,pageSize}")
            )

            if (pageIndex > 0)
                toReturn.add(SirenLink(listOf("previous"), URI("${baseUri}?page=${pageIndex - 1}&count=${count}")))
            if (collectionSize != ((pageIndex + 1) * pageSize))
                toReturn.add(SirenLink(listOf("next"), URI("${baseUri}?page=${pageIndex + 1}&count=${count}")))
            return toReturn
        }
    }

    @Autowired
    protected lateinit var env: EnvironmentInfo

    @Autowired
    protected lateinit var github: GitHubInterface
}