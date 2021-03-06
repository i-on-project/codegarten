import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
}

group = "org.ionproject"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

val DOCKER_PROJECT_NAME = "CodeGarten"

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// Database access
	implementation("org.postgresql:postgresql:42.2.19")
	implementation("org.jdbi:jdbi3-core:3.18.0")
	implementation("org.jdbi:jdbi3-kotlin:3.18.0")

	// Http Requests
	implementation("com.squareup.okhttp3:okhttp:4.9.0")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

	// For reading PKCS1 RSA private key files
	implementation("org.bouncycastle:bcprov-jdk15on:1.68")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

task<Copy>("extractUberJar") {
	dependsOn("build")
	from(zipTree("$buildDir/libs/${rootProject.name}-$version.jar"))
	into("build/extracted")
}

task<Exec>("dbUp") {
	commandLine("docker-compose", "-p", DOCKER_PROJECT_NAME, "up", "-d", "codegarten-db")
}

task<Exec>("dbWait") {
	commandLine("docker", "exec", "codegarten-db", "/app/bin/wait-for-postgres.sh", "localhost")
	dependsOn("dbUp")
}

task<Exec>("dbStop") {
	commandLine("docker", "stop", "codegarten-db")
}

task<Exec>("dbTestsUp") {
	commandLine("docker-compose", "-p", DOCKER_PROJECT_NAME, "up", "-d", "codegarten-db-tests")
}

task<Exec>("dbTestsWait") {
	commandLine("docker", "exec", "codegarten-db-tests", "/app/bin/wait-for-postgres.sh", "localhost")
	dependsOn("dbTestsUp")
}

task<Exec>("dbTestsDown") {
	commandLine("docker-compose", "-p", DOCKER_PROJECT_NAME, "rm", "-fsv", "codegarten-db-tests")
}

tasks {
	named<Test>("test") {
		dependsOn("dbTestsWait")
		finalizedBy("dbTestsDown")
	}
	
	named("bootRun") {
		dependsOn("dbWait")
		finalizedBy("dbStop")
	}
}