package org.ionproject.codegarten.database.dto

data class DtoListWrapper<T>(
    val count: Int,
    val results: List<T>
)