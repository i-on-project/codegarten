package org.ionproject.codegarten.pipeline.argumentresolvers

import org.ionproject.codegarten.database.dto.InviteCode
import org.ionproject.codegarten.pipeline.interceptors.INVITE_CODE_ATTRIBUTE
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class InviteCodeResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == InviteCode::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): InviteCode =
        webRequest.getAttribute(INVITE_CODE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) as InviteCode

}