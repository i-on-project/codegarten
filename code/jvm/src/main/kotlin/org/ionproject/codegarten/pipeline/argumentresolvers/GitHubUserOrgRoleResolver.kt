package org.ionproject.codegarten.pipeline.argumentresolvers

import org.ionproject.codegarten.pipeline.interceptors.ORG_MEMBERSHIP_ATTRIBUTE
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class GitHubUserOrgRoleResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == GitHubUserOrgRole::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): GitHubUserOrgRole =
        webRequest.getAttribute(ORG_MEMBERSHIP_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) as GitHubUserOrgRole
}