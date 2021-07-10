package org.ionproject.codegarten.database

import org.ionproject.codegarten.testutils.TestUtils.getResourceFile
import org.ionproject.codegarten.utils.CryptoUtils
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.slf4j.LoggerFactory
import org.springframework.test.context.junit.jupiter.SpringExtension

const val CLEANUP_SCRIPT_PATH = "sql/delete.sql"
const val FILL_SCRIPT_PATH = "sql/fill.sql"

class DatabaseInitializer : BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private var started = false

    private lateinit var jdbi: Jdbi
    private lateinit var cryptoUtils: CryptoUtils
    private val logger = LoggerFactory.getLogger(DatabaseInitializer::class.java)

    override fun beforeAll(context: ExtensionContext) {
        synchronized(this) {
            if (!started) {
                started = true

                // Get the jdbi bean from the spring context, as we cannot get it through injection
                val springContext = SpringExtension.getApplicationContext(context)
                jdbi = springContext.getBean(Jdbi::class.java)
                cryptoUtils = springContext.getBean(CryptoUtils::class.java)
                cleanupDb()
                fillDb()

                // Callback hook for when the root test context is shut down
                context
                    .root
                    .getStore(GLOBAL)
                    .put("database-initializer-cb", this)
            }
        }

    }

    override fun close() {
        cleanupDb()
    }

    private fun cleanupDb() {
        logger.info("Running SQL cleanup script")
        val cleanupScript = getResourceFile(CLEANUP_SCRIPT_PATH).readText()
        jdbi.useHandle<Exception> {
            it.createScript(cleanupScript).execute()
        }
    }

    private fun fillDb() {
        logger.info("Running SQL fill script")
        val fillScript = getResourceFile(FILL_SCRIPT_PATH).readText()
        jdbi.useHandle<Exception> {
            it.createUpdate("INSERT INTO USERS(name, gh_id, gh_token) VALUES ('teacher', '1', '${cryptoUtils.encrypt("gh_tokenAdmin")}'), " +
                    "('student', '2', '${cryptoUtils.encrypt("gh_tokenMember")}'), " +
                    "('user3', '3', '${cryptoUtils.encrypt("gh_tokenNotMember")}'), " +
                    "('user4', '4', '${cryptoUtils.encrypt("gh_tokenDeliveries")}');")
                .execute()
            it.createScript(fillScript).execute()
        }
    }

}