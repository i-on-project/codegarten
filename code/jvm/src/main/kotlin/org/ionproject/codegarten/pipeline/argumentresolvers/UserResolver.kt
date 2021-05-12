package org.ionproject.codegarten.pipeline.argumentresolvers

import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.pipeline.interceptors.USER_ATTRIBUTE
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class UserResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): User =
        webRequest.getAttribute(USER_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) as User
}