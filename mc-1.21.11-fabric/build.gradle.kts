plugins {
    id("fabric-loom") version "1.3-SNAPSHOT"
    kotlin("jvm") version "2.2.20"
}

//Constants:
val mcVersion: String by project
val modPlatform: String by project
val modId: String by project

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

dependencies {
    implementation(project(":aurora-integration")){
        exclude(group = "org.jetbrains.kotlin")
    }
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
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

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to inputs.properties["version"]))
    }
}

tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    destinationDirectory.set(file("${rootProject.projectDir}/output/jars"))
}
