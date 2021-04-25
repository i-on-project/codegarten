package org.ionproject.codegarten.pipeline.filters

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class RequestLoggingFilter : Filter {
    private val logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val start = System.currentTimeMillis()

        logger.info("Handling ${httpRequest.method} ${httpRequest.requestURI}")
        chain?.doFilter(request, response)
        logger.info("Handled ${httpRequest.method} ${httpRequest.requestURI} in ${System.currentTimeMillis() - start} ms")
    }
}