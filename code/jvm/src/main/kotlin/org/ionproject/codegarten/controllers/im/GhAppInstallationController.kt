package org.ionproject.codegarten.controllers.im

import org.ionproject.codegarten.Routes.GH_INSTALLATIONS_CB_HREF
import org.ionproject.codegarten.Routes.GH_INSTALLATIONS_HREF
import org.ionproject.codegarten.Routes.INSTALLATION_ID_PARAM
import org.ionproject.codegarten.database.helpers.InstallationsDb
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.responses.GitHubAccountType.ORGANIZATION
import org.ionproject.codegarten.utils.CryptoUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GhAppInstallationController(
    val gitHub: GitHubInterface,
    val installationsDb: InstallationsDb,
    val cryptoUtils: CryptoUtils
) {

    @GetMapping(GH_INSTALLATIONS_HREF)
    fun installToOrg() : ResponseEntity<Any> {
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", gitHub.getInstallationUri().toString())
            .body(null)
    }


    @GetMapping(GH_INSTALLATIONS_CB_HREF)
    fun orgInstallationCallback(
        @RequestParam(name = INSTALLATION_ID_PARAM) installationId: Int?
    ) : ResponseEntity<Any> {
        val status: String

        if (installationId != null) {
            val installationOrg = gitHub.getInstallationOrg(installationId)
            if (installationOrg.account.type == ORGANIZATION) {
                val installationToken = gitHub.getInstallationToken(installationId)
                installationsDb.createOrUpdateInstallation(
                    installationId, installationOrg.account.id,
                    cryptoUtils.encrypt(installationToken.token), installationToken.expires_at
                )
                status = "successful"
            } else {
                status = "ignored, since the account is not an organization"
            }
        } else {
            status = "requested"
        }

        return ResponseEntity
            .status(200)
            .body("Installation has been $status. You can now close this page <script> window.close() </script>")
    }
}