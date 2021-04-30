package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Installation
import org.ionproject.codegarten.exceptions.NotFoundException
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

private const val GET_INSTALLATIONS_BASE = "SELECT iid, org_id, access_token, expiration_date FROM INSTALLATION"
private const val GET_INSTALLATION_QUERY = "$GET_INSTALLATIONS_BASE WHERE iid = :installationId"
private const val GET_INSTALLATION_BY_ORG_ID_QUERY = "$GET_INSTALLATIONS_BASE WHERE org_id = :orgId"

private const val CREATE_INSTALLATION_QUERY = "INSERT INTO INSTALLATION VALUES(:orgId, :installationId, :accessToken, :exp)"

private const val UPDATE_INSTALLATION_START = "UPDATE INSTALLATION SET"
private const val UPDATE_INSTALLATION_END = "WHERE org_id = :orgId"

@Component
class InstallationsDb(val jdbi: Jdbi) {

    fun getInstallation(installationId: Int) =
        jdbi.getOne(GET_INSTALLATION_QUERY, Installation::class.java, mapOf("installationId" to installationId))

    fun getInstallationByOrgId(orgId: Int) =
        jdbi.getOne(GET_INSTALLATION_BY_ORG_ID_QUERY, Installation::class.java, mapOf("orgId" to orgId))

    fun createOrUpdateInstallation(
        installationId: Int,
        orgId: Int,
        accessToken: String,
        expirationDate: OffsetDateTime
    ) {
        try {
            getInstallationByOrgId(orgId) // Will throw if the installation is not present
            editInstallation(orgId, installationId = installationId, accessToken, expirationDate)
        } catch (ex: NotFoundException) {
            return createInstallation(installationId, orgId, accessToken, expirationDate)
        }
    }

    fun createInstallation(installationId: Int, orgId: Int, accessToken: String, expirationDate: OffsetDateTime) =
        jdbi.insert(
            CREATE_INSTALLATION_QUERY,
            mapOf(
                "installationId" to installationId,
                "orgId" to orgId,
                "accessToken" to accessToken,
                "exp" to expirationDate
            )
        )

    fun editInstallation(orgId: Int, installationId: Int?, accessToken: String, expirationDate: OffsetDateTime) {
        val updateFields = mutableMapOf<String, Any>(
            "access_token" to accessToken,
            "expiration_date" to expirationDate
        )
        if (installationId != null) updateFields["iid"] = installationId

        jdbi.update(
            UPDATE_INSTALLATION_START,
            updateFields,
            UPDATE_INSTALLATION_END,
            mapOf("orgId" to orgId)
        )
    }
}