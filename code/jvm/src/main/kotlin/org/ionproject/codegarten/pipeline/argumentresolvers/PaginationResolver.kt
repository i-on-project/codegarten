package org.ionproject.codegarten.pipeline.argumentresolvers

import org.ionproject.codegarten.exceptions.PaginationException
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

private const val PAGE_PARAMETER_NAME = "page"
private const val LIMIT_PARAMETER_NAME = "limit"
private const val DEFAULT_PAGE = 0
private const val DEFAULT_LIMIT = 10
private const val MAX_LIMIT = 100

data class Pagination(
    val page: Int,
    val limit: Int
)

class PaginationResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == Pagination::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?): Pagination {

        val page = webRequest.getParameter(PAGE_PARAMETER_NAME)?.toInt() ?: DEFAULT_PAGE
        val limit = webRequest.getParameter(LIMIT_PARAMETER_NAME)?.toInt() ?: DEFAULT_LIMIT

        if (page < 0) throw PaginationException("Page can't be negative")
        if (limit < 0) throw PaginationException("Limit can't be negative")
        if (limit > MAX_LIMIT) throw PaginationException("Limit can't be higher than $MAX_LIMIT")

        return Pagination(page, limit)
    }
}