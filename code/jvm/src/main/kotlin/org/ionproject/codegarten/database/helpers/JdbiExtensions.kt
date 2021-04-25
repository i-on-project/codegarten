package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.exceptions.NotFoundException
import org.jdbi.v3.core.Jdbi

private const val GET_PAGINATED_SUFFIX = "LIMIT :limit OFFSET :offset"

fun <T> Jdbi.getList(query: String, mapTo: Class<T>, page: Int, perPage: Int, binds: Map<String, Any>? = null): List<T> =
    this.withHandle<List<T>, Exception> {
        val handle = it.createQuery("$query $GET_PAGINATED_SUFFIX")
            .bind("limit", perPage)
            .bind("offset", page * perPage)
        binds?.forEach { entry -> handle.bind(entry.key, entry.value) }

        handle
            .mapTo(mapTo)
            .list()
    }

fun <T> Jdbi.getOne(query: String, mapTo: Class<T>, binds: Map<String, Any>? = null): T =
    this.withHandle<T, Exception> {
        val handle = it.createQuery(query)
        binds?.forEach { entry -> handle.bind(entry.key, entry.value) }

        val res = handle
            .mapTo(mapTo)
            .findOne()

        if (res.isEmpty) throw NotFoundException("Resource does not exist")
        res.get()
    }

fun <T, V> Jdbi.insertAndGet(insertQuery: String, generatedIdType: Class<V>,
                       getInsertedQuery: String, mapTo: Class<T>,
                       insertBinds: Map<String, Any>? = null, getBindKey: String? = null): T =
    this.withHandle<T, Exception> {
        val insertHandle = it.createUpdate(insertQuery)
        insertBinds?.forEach { entry -> insertHandle.bind(entry.key, entry.value) }

        val key = insertHandle
            .executeAndReturnGeneratedKeys()
            .mapTo(generatedIdType)
            .one()

        val getHandle = it.createQuery(getInsertedQuery)
        if (getBindKey != null)
            getHandle.bind(getBindKey, key)

        getHandle
            .mapTo(mapTo)
            .one()
    }

fun Jdbi.insert(insertQuery: String, insertBinds: Map<String, Any>? = null) {
    this.useHandle<Exception> {
        val insertHandle = it.createUpdate(insertQuery)
        insertBinds?.forEach { entry -> insertHandle.bind(entry.key, entry.value) }

        insertHandle.execute()
    }
}

fun Jdbi.update(queryStart: String, updateFields: Map<String, Any>,
                     queryEnd: String, endBinds: Map<String, Any>) {
    // Build query string
    val stringBuilder = StringBuilder(queryStart)
    updateFields.forEach { stringBuilder.append(" ${it.key} = :${it.key},") }
    stringBuilder.deleteCharAt(stringBuilder.length - 1)
    stringBuilder.append(' ')
    stringBuilder.append(queryEnd)

    this.useHandle<Exception> {
        val handle = it.createUpdate(stringBuilder.toString())
        updateFields.forEach { entry -> handle.bind(entry.key, entry.value) }
        endBinds.forEach { entry -> handle.bind(entry.key, entry.value) }

        if (handle.execute() == 0) throw NotFoundException("Resource does not exist")
    }
}

fun Jdbi.delete(query: String, binds: Map<String, Any>? = null) {
    this.useHandle<Exception> {
        val handle = it.createUpdate(query)
        binds?.forEach { entry -> handle.bind(entry.key, entry.value) }

        if (handle.execute() == 0) throw NotFoundException("Resource does not exist")
    }
}