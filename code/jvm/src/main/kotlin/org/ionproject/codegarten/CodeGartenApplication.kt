package org.ionproject.codegarten

import com.fasterxml.jackson.databind.ObjectMapper
import org.ionproject.codegarten.pipeline.argumentresolvers.AssignmentResolver
import org.ionproject.codegarten.pipeline.argumentresolvers.GitHubUserOrgRoleResolver
import org.ionproject.codegarten.pipeline.argumentresolvers.InstallationResolver
import org.ionproject.codegarten.pipeline.argumentresolvers.InviteCodeResolver
import org.ionproject.codegarten.pipeline.argumentresolvers.PaginationResolver
import org.ionproject.codegarten.pipeline.argumentresolvers.UserClassroomResolver
import org.ionproject.codegarten.pipeline.argumentresolvers.UserResolver
import org.ionproject.codegarten.pipeline.interceptors.AuthorizationInterceptor
import org.ionproject.codegarten.pipeline.interceptors.InstallationInterceptor
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.utils.CryptoUtils
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.net.URI

@ConfigurationPropertiesScan
@EnableScheduling
@SpringBootApplication
class CodeGartenApplication(private val configProperties: ConfigProperties) {

	private val cryptoUtils: CryptoUtils

	init {
		val cipherKey = System.getenv(configProperties.cipherKeyEnv)
			?: throw IllegalStateException("Missing ${configProperties.cipherKeyEnv} environment variable!")

		cryptoUtils = CryptoUtils(cipherKey)
	}

	@Bean
	fun getJdbi(): Jdbi {
		val jdbcConnectionString = System.getenv(configProperties.dbJdbcConnectionStringEnv)
		val connectionUrl = System.getenv(configProperties.dbUrlConnectionStringEnv)
		if (jdbcConnectionString == null && connectionUrl == null)
			throw IllegalStateException("Missing ${configProperties.dbJdbcConnectionStringEnv} or ${configProperties.dbUrlConnectionStringEnv} environment variable!")

		val connectionString: String =
			if (jdbcConnectionString != null)
				jdbcConnectionString
			else {
				val dbUri = URI(connectionUrl)

				val username: String = dbUri.userInfo.split(":")[0]
				val password: String = dbUri.userInfo.split(":")[1]
				"jdbc:postgresql://${dbUri.host}:${dbUri.port}${dbUri.path}?user=$username&password=$password"
			}

		return Jdbi
			.create(connectionString)
			.installPlugin(KotlinPlugin())
	}

	@Bean
	fun getGithubInterface(): GitHubInterface {
		val ghAppPropertiesEnv = System.getenv(configProperties.gitHubAppPropertiesEnv)
			?: throw IllegalStateException("Missing ${configProperties.gitHubAppPropertiesEnv} environment variable!")
		val ghAppPrivateKeyEnv = System.getenv(configProperties.gitHubAppPrivateKeyEnv)
			?: throw IllegalStateException("Missing ${configProperties.gitHubAppPrivateKeyEnv} environment variable!")

		val ghAppPropertiesString = try {
			Base64Utils.decodeFromString(ghAppPropertiesEnv)
		} catch(ex: IllegalArgumentException) {
			throw IllegalArgumentException("${configProperties.gitHubAppPropertiesEnv} environment variable is not a valid Base64 string!")
		}

		val ghAppPrivateKeyString = try {
			String(Base64Utils.decodeFromString(ghAppPrivateKeyEnv))
		} catch(ex: IllegalArgumentException) {
			throw IllegalArgumentException("${configProperties.gitHubAppPrivateKeyEnv} environment variable is not a valid Base64 string!")
		}

		val mapper: ObjectMapper = Jackson2ObjectMapperBuilder().build()
		val ghAppProperties = mapper.readValue(
			ghAppPropertiesString,
			GitHubAppProperties::class.java
		)

		return GitHubInterface(
			ghAppProperties,
			cryptoUtils.readRsaPrivateKey(ghAppPrivateKeyString),
			mapper
		)
	}

	@Bean
	fun getCryptoUtils() = cryptoUtils
}

@Component
class MvcConfig(
	val authInterceptor: AuthorizationInterceptor,
	val installationInterceptor: InstallationInterceptor
) : WebMvcConfigurer {

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(authInterceptor)
		registry.addInterceptor(installationInterceptor)
	}

	override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
		resolvers.add(PaginationResolver())
		resolvers.add(UserResolver())
		resolvers.add(GitHubUserOrgRoleResolver())
		resolvers.add(UserClassroomResolver())
		resolvers.add(AssignmentResolver())
		resolvers.add(InstallationResolver())
		resolvers.add(InviteCodeResolver())
	}
}

fun main(args: Array<String>) {
	runApplication<CodeGartenApplication>(*args)
}
