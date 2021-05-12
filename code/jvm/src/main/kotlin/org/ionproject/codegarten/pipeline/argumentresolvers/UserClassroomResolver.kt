package org.ionproject.codegarten.pipeline.argumentresolvers

import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.pipeline.interceptors.CLASSROOM_MEMBERSHIP_ATTRIBUTE
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class UserClassroomResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == UserClassroom::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): UserClassroom =
        webRequest.getAttribute(CLASSROOM_MEMBERSHIP_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) as UserClassroom
}