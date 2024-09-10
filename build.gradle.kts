plugins {
	kotlin("jvm") version "2.0.20"
	id("java-library")
	id("maven-publish")
	id("nebula.release") version "19.0.8"
}

group = "org.shypl.tool"

kotlin {
	jvmToolchain(17)
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation("org.shypl.tool:tool-lang:1.0.0-SNAPSHOT")
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
}