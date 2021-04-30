package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.ORGS_HREF
import org.ionproject.codegarten.Routes.ORG_BY_ID_HREF
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.createSirenLinkListForPagination
import org.ionproject.codegarten.Routes.getClassroomsUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.models.OrganizationOutputModel
import org.ionproject.codegarten.controllers.models.OrganizationsOutputModel
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInOrg
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGithubLoginUri
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.siren.SirenLink
import org.ionproject.codegarten.responses.toResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class OrgsController(
    val gitHub: GitHubInterface
) {

    @RequiresUserAuth
    @GetMapping(ORGS_HREF)
    fun getUserOrgs(
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        val userOrgs = gitHub.getUserOrgs(user.gh_token, pagination.page, pagination.limit)

        return OrganizationsOutputModel(
            pageIndex = pagination.page,
            pageSize = userOrgs.size
        ).toSirenObject(
            entities = userOrgs.map {
                OrganizationOutputModel(
                    id = it.id,
                    name = it.login,
                    description = if (it.description.isNullOrEmpty()) null else it.description
                ).toSirenObject(
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getOrgByIdUri(it.id).includeHost()),
                        SirenLink(listOf("github"), getGithubLoginUri(it.login)),
                        SirenLink(listOf("avatar"), URI(it.avatar_url)),
                        SirenLink(listOf("classrooms"), getClassroomsUri(it.id).includeHost())
                    )
                )
            },
            links = createSirenLinkListForPagination(URI(ORGS_HREF).includeHost(), pagination.page, pagination.limit, pageSize = userOrgs.size)
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserAuth
    @RequiresUserInOrg
    @GetMapping(ORG_BY_ID_HREF)
    fun getOrg(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        val org = gitHub.getOrgById(orgId, user.gh_token)

        return OrganizationOutputModel(
            id = org.id,
            name = org.login,
            description = if (org.description.isNullOrEmpty()) null else org.description
        ).toSirenObject(
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getOrgByIdUri(orgId).includeHost()),
                SirenLink(listOf("github"), getGithubLoginUri(org.login)),
                SirenLink(listOf("avatar"), URI(org.avatar_url)),
                SirenLink(listOf("classrooms"), getClassroomsUri(org.id).includeHost())
            )
        ).toResponseEntity(HttpStatus.OK)
    }
}