plugins {
	kotlin("jvm") version "2.1.10"
	id("java-library")
	id("maven-publish")
	id("nebula.release") version "19.0.10"
}

group = "org.shypl.tool"

kotlin {
	jvmToolchain(21)
}

repositories {
	mavenCentral()
	maven("https://maven.pkg.github.com/shypl/packages").credentials {
		username = ""
		password = project.property("shypl.gpr.key") as String
	}
}

dependencies {
	implementation("org.shypl.tool:tool-lang:1.0.0")
	implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
	implementation(kotlin("reflect"))
	testImplementation(kotlin("test"))
}

java {
	withSourcesJar()
}

publishing {
	publications.create<MavenPublication>("Library") {
		from(components["java"])
	}
	repositories.maven("https://maven.pkg.github.com/shypl/packages").credentials {
		username = project.property("shypl.gpr.user") as String
		password = project.property("shypl.gpr.key") as String
	}
}

tasks.release {
	finalizedBy(tasks.publish)
}