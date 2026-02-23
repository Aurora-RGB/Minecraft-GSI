buildscript {
    repositories {
        maven(url = "https://maven.minecraftforge.net/")
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:6.0.+")
    }
}

plugins {
    kotlin("jvm") version "2.3.0"
    id("net.minecraftforge.gradle") version "[6.0.16,6.2)"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val mcVersion: String by project
val modPlatform: String by project
val modId: String by project
val minecraft_version: String by project
val forge_version: String by project
val kff_version: String by project

base.archivesName = "$modId-$mcVersion-$modPlatform"

repositories {
    maven(url = "https://maven.minecraftforge.net/")
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/")
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":aurora-integration")) {
        exclude(group = "org.jetbrains.kotlin")
    }
    minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    implementation ("thedarkcolour:kotlinforforge:${kff_version}")
}

minecraft {
    mappings("official", minecraft_version)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.jar {
    from(project(":aurora-integration").sourceSets.main.get().output)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude("META-INF/versions/**")
    exclude("module-info.class")
}

