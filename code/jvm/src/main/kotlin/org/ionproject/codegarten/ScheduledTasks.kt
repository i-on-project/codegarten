package org.ionproject.codegarten

import org.ionproject.codegarten.database.helpers.AccessTokensDb
import org.ionproject.codegarten.database.helpers.AuthCodesDb
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private const val CLEAN_EXPIRED_TOKENS_TIME_MS = 1000L * 60 * 60 * 24 // 1 Day

@Component
class ScheduledTasks(
    val authCodesDb: AuthCodesDb,
    val accessTokensDb: AccessTokensDb,
) {

    private val logger = LoggerFactory.getLogger(ScheduledTasks::class.java)

    @Scheduled(fixedRate = CLEAN_EXPIRED_TOKENS_TIME_MS)
    fun cleanExpiredAuthCodesAndAccessTokens() {
        logger.info("Starting scheduled task to clean all expired access tokens and auth codes from the database")

        val authCodesCount = authCodesDb.deleteExpiredAuthCodes()
        val accessTokensCount = accessTokensDb.deleteExpiredAccessTokens()
        logger.info("Number of expired authentication codes removed: $authCodesCount")
        logger.info("Number of expired access tokens removed: $accessTokensCount")

        logger.info("Ending scheduled task...")
    }
}