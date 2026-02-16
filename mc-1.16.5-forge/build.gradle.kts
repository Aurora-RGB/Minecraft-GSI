buildscript {
    repositories {
        maven(url = "https://maven.minecraftforge.net/")
        mavenCentral()
    }

    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:6.0.+")
    }
}

plugins {
    kotlin("jvm") version "2.3.0"
    id("net.minecraftforge.gradle") version "6.0.+"
}

//Constants:
val mcVersion: String by project
val modPlatform: String by project
val modId: String by project

base.archivesName = "$modId-$mcVersion-$modPlatform"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(8)
}

apply(plugin = "kotlin")
apply(plugin = "net.minecraftforge.gradle")


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.compileJava {
    sourceCompatibility = "8"
    targetCompatibility = "8"
}

repositories {
    maven(url = "https://maven.minecraftforge.net/")
}

dependencies {
    implementation(project(":aurora-integration")){
        exclude(group = "org.jetbrains.kotlin")
    }
    minecraft("net.minecraftforge:forge:1.16.5-36.2.35")
}

minecraft {
    //mappings but for 1.16.5
    mappings("snapshot", "20201028-1.16.3")
}

tasks.jar {
    // include project(":aurora-integration") files in the jar
    from(project(":aurora-integration").sourceSets.main.get().output)

    from(project(":aurora-integration").configurations.runtimeClasspath.get().filter {
        it.name.startsWith("kotlinx-serialization") || it.name.startsWith("kotlin-stdlib")
    }.map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude("META-INF/versions/**")
    exclude("module-info.class")
}

// Forge 1.12 expects resources next to compiled classes
sourceSets.all {
    val firstClassDir = output.classesDirs.files.first()
    output.resourcesDir = firstClassDir
}
