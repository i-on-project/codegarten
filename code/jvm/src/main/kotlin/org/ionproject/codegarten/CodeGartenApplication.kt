package org.ionproject.codegarten

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
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@ConfigurationPropertiesScan
@EnableScheduling
@SpringBootApplication
class CodeGartenApplication(private val configProperties: ConfigProperties) {

	private val cryptoUtils = CryptoUtils(configProperties.cipherKeyPath)

	@Bean
	fun getJdbi() = Jdbi
		.create(configProperties.dbConnectionString)
		.installPlugin(KotlinPlugin())

	@Bean
	fun getGithubInterface() =
		GitHubInterface(
			configProperties.gitHubAppId,
			configProperties.gitHubAppClientId,
			configProperties.gitHubAppName,
			configProperties.gitHubAppClientSecret,
			cryptoUtils.readRsaPrivateKey(configProperties.gitHubAppPrivateKeyPemPath),
			Jackson2ObjectMapperBuilder().build()
		)

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
	System.setProperty("server.port", Routes.PORT)
	runApplication<CodeGartenApplication>(*args)
}
