package org.ionproject.codegarten.pipeline.exceptionhandling

import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.auth.AuthHeaderValidator.AUTH_SCHEME
import org.ionproject.codegarten.database.PsqlErrorCode
import org.ionproject.codegarten.database.getPsqlErrorCode
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.ClientException
import org.ionproject.codegarten.exceptions.ForbiddenException
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.exceptions.LoopDetectedException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.exceptions.PaginationException
import org.ionproject.codegarten.responses.ProblemJson
import org.ionproject.codegarten.responses.Response
import org.jdbi.v3.core.JdbiException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI
import java.sql.SQLException
import javax.servlet.http.HttpServletRequest

private val logger = LoggerFactory.getLogger("ExceptionHandler")

fun handleExceptionResponse(
    type: URI,
    title: String,
    status: HttpStatus,
    detail: String,
    instance: String,
    customHeaders: HttpHeaders = HttpHeaders(),
): ResponseEntity<Response> {
    logger.error("[$instance] $detail")

    return ResponseEntity
        .status(status)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .headers(customHeaders)
        .body(ProblemJson(type.toString(), title, status.value(), detail, instance))
}

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [HttpRequestException::class])
    private fun handleHttpRequestException(
        ex: HttpRequestException,
        request: HttpServletRequest
    ): ResponseEntity<Response> =
        handleExceptionResponse(
            URI("/problems/github-api-error").includeHost(),
            "Error While Processing GitHub API Response",
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.localizedMessage,
            request.requestURI
        )

    @ExceptionHandler(value = [NotFoundException::class])
    private fun handleNotFoundException(
        ex: NotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<Response> =
        handleExceptionResponse(
            URI("/problems/resource-not-found").includeHost(),
            "Resource Not Found",
            HttpStatus.NOT_FOUND,
            ex.localizedMessage,
            request.requestURI
        )

    @ExceptionHandler(value = [AuthorizationException::class])
    private fun handleAuthorizationException(
        ex: AuthorizationException,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        val headers = HttpHeaders()
        headers.add("WWW-Authenticate", AUTH_SCHEME)

        return handleExceptionResponse(
            URI("/problems/not-authorized").includeHost(),
            "Authorization Error",
            HttpStatus.UNAUTHORIZED,
            ex.localizedMessage,
            request.requestURI,
            headers
        )
    }

    @ExceptionHandler(value = [ForbiddenException::class])
    private fun handleForbiddenException(
        ex: ForbiddenException,
        request: HttpServletRequest
    ): ResponseEntity<Response> =
        handleExceptionResponse(
            URI("/problems/forbidden-operation").includeHost(),
            "Forbidden Operation",
            HttpStatus.FORBIDDEN,
            ex.localizedMessage,
            request.requestURI,
        )

    @ExceptionHandler(value = [LoopDetectedException::class])
    private fun handleForbiddenException(
        ex: LoopDetectedException,
        request: HttpServletRequest
    ): ResponseEntity<Response> =
        handleExceptionResponse(
            URI("/problems/loop-detected").includeHost(),
            "Loop Detected",
            HttpStatus.LOOP_DETECTED,
            ex.localizedMessage,
            request.requestURI,
        )

    @ExceptionHandler(value = [PaginationException::class])
    private fun handlePaginationException(
        ex: PaginationException,
        request: HttpServletRequest
    ): ResponseEntity<Response> =
        handleExceptionResponse(
            URI("/problems/invalid-pagination-parameters").includeHost(),
            "Invalid Pagination Parameters",
            HttpStatus.BAD_REQUEST,
            ex.localizedMessage,
            request.requestURI
        )

    @ExceptionHandler(value = [InvalidInputException::class])
    private fun handleInvalidInputException(
        ex: InvalidInputException,
        request: HttpServletRequest
    ): ResponseEntity<Response> =
        handleExceptionResponse(
            URI("/problems/invalid-input").includeHost(),
            "Invalid Input",
            HttpStatus.BAD_REQUEST,
            ex.localizedMessage,
            request.requestURI
        )

    @ExceptionHandler(value = [ClientException::class])
    private fun handleClientException(
        ex: ClientException,
        request: HttpServletRequest
    ): ResponseEntity<Response> =
        handleExceptionResponse(
            URI("/problems/invalid-client").includeHost(),
            "Invalid Client",
            HttpStatus.UNAUTHORIZED,
            ex.localizedMessage,
            request.requestURI
        )

    @ExceptionHandler(value = [JdbiException::class])
    private fun handleJdbiException(
        ex: JdbiException,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        val cause = ex.cause as SQLException
        val psqlError = ex.getPsqlErrorCode()

        if (psqlError == null) {
            return handleExceptionResponse(
                URI("/problems/database-error").includeHost(),
                "Unknown Database Error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unknown database error has occurred",
                request.requestURI
            )
        }

        return when (psqlError) {
            PsqlErrorCode.UniqueViolation -> {
                handleExceptionResponse(
                    URI("/problems/resource-already-exists").includeHost(),
                    "Resource Already Exists",
                    HttpStatus.CONFLICT,
                    cause.localizedMessage,
                    request.requestURI
                )
            }
            PsqlErrorCode.ForeignKeyViolation -> {
                handleExceptionResponse(
                    URI("/problems/resource-referenced").includeHost(),
                    "Resource Is Referenced",
                    HttpStatus.CONFLICT,
                    cause.localizedMessage,
                    request.requestURI
                )
            }
            PsqlErrorCode.CheckViolation -> {
                handleExceptionResponse(
                    URI("/problems/invalid-creation-request").includeHost(),
                    "Invalid Creation Request",
                    HttpStatus.BAD_REQUEST,
                    cause.localizedMessage,
                    request.requestURI
                )
            }
            PsqlErrorCode.StringDataRightTruncation -> {
                handleExceptionResponse(
                    URI("/problems/invalid-string-size").includeHost(),
                    "Invalid String Size",
                    HttpStatus.BAD_REQUEST,
                    cause.localizedMessage,
                    request.requestURI
                )
            }
        }
    }
}