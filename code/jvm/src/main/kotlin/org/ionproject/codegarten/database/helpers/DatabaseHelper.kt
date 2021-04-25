package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.PsqlErrorCode
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.JdbiException
import org.springframework.beans.factory.annotation.Autowired
import java.sql.SQLException

private const val GET_PAGINATED_SUFFIX = "LIMIT :limit OFFSET :offset"

data class DbResponse<T> (
    val response: T? = null,
    val error: PsqlErrorCode? = null,
)

abstract class DatabaseHelper {
    @Autowired
    protected lateinit var jdbi: Jdbi

    protected fun <T> getList(query: String, mapTo: Class<T>, page: Int, perPage: Int, vararg bindPairs: Pair<String, Any>): List<T> =
        jdbi.withHandle<List<T>, Exception> {
            val handle = it.createQuery("$query $GET_PAGINATED_SUFFIX")
                .bind("limit", perPage)
                .bind("offset", page * perPage)
            bindPairs.forEach { pair ->
                handle.bind(pair.first, pair.second)
            }
            handle
                .mapTo(mapTo)
                .list()
        }

    protected fun <T> getOne(query: String, mapTo: Class<T>, vararg bindPairs: Pair<String, Any>): T =
        jdbi.withHandle<T, Exception> {
            val handle = it.createQuery(query)
            bindPairs.forEach { pair ->
                handle.bind(pair.first, pair.second)
            }
            handle
                .mapTo(mapTo)
                .one()
        }

    protected fun <T> insertOrUpdate(query: String, generatedIdType: Class<T>, vararg bindPairs: Pair<String, Any>): DbResponse<T> =
        try {
            DbResponse(
                jdbi.withHandle<T, Exception> {
                    val handle = it.createUpdate(query)
                    bindPairs.forEach { pair ->
                        handle.bind(pair.first, pair.second)
                    }

                    handle
                        .executeAndReturnGeneratedKeys()
                        .mapTo(generatedIdType)
                        .one()
                }
            )
        } catch (e: JdbiException) {
            DbResponse(error = PsqlErrorCode.values().find { it.code == (e.cause as SQLException).sqlState })
        }
}