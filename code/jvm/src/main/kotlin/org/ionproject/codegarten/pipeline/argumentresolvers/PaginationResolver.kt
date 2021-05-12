package org.ionproject.codegarten.pipeline.argumentresolvers

import org.ionproject.codegarten.Routes.DEFAULT_LIMIT
import org.ionproject.codegarten.Routes.DEFAULT_PAGE
import org.ionproject.codegarten.Routes.LIMIT_PARAM
import org.ionproject.codegarten.Routes.MAX_LIMIT
import org.ionproject.codegarten.Routes.PAGE_PARAM
import org.ionproject.codegarten.exceptions.PaginationException
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

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

        val page = webRequest.getParameter(PAGE_PARAM)?.toInt() ?: DEFAULT_PAGE
        val limit = webRequest.getParameter(LIMIT_PARAM)?.toInt() ?: DEFAULT_LIMIT

        if (page < 0) throw PaginationException("Page can't be negative")
        if (limit < 0) throw PaginationException("Limit can't be negative")
        if (limit > MAX_LIMIT) throw PaginationException("Limit can't be higher than $MAX_LIMIT")

        return Pagination(page, limit)
    }
}