package org.ionproject.codegarten

import com.fasterxml.jackson.databind.ObjectMapper
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubInterfaceMock
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@SpringBootTest
class CodeGartenApplicationTests {

	@Test
	fun contextLoads() {}
}

@TestConfiguration
class GitHubMockConfiguration {

	@Bean
	@Primary
	fun getGitHubInterface(mapper: ObjectMapper): GitHubInterface {
		return GitHubInterfaceMock()
	}
}