// The settings file is the entry point of every Gradle build.
// Its primary purpose is to define the subprojects.
// It is also used for some aspects of project-wide configuration, like managing plugins, dependencies, etc.
// https://docs.gradle.org/current/userguide/settings_file_basics.html

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://repo.essential.gg/repository/maven-releases/")
        maven("https://maven.crystaelix.com/releases/")
        maven("https://jitpack.io")
        maven("https://maven.ornithemc.net/releases/")
        maven("https://maven.ornithemc.net/snapshots/")
    }
}

dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// Include the `app` and `utils` subprojects in the build.
// If there are changes in only one of the projects, Gradle will rebuild only the one that has changed.
// Learn more about structuring projects with Gradle - https://docs.gradle.org/8.7/userguide/multi_project_builds.html

rootProject.name = "minecraftGsi"
include("aurora-integration")
include("mc-1.8.9-forge")
include("mc-1.7.10-forge")
include("mc-1.12.2-forge")
include("mc-1.13.2-forge")
include("mc-1.14.4-forge")
include("mc-1.14.4-fabric")
include("mc-1.15.2-fabric")
include("mc-1.16.5-forge")
include("mc-1.16.5-fabric")
include("mc-1.18.2-forge")
include("mc-1.20.6-forge")
include("mc-1.20.6-fabric")
include("mc-1.21.11-forge")
include("mc-1.21.11-fabric")