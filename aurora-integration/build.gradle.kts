plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
}

group = "com.project-aurora"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
}

kotlin {
    jvmToolchain(8)
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
tasks.compileJava {
    sourceCompatibility = "8"
    targetCompatibility = "8"
}
