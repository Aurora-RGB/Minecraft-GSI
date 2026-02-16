plugins {
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.3.0"
}

//Constants:
val baseGroup: String by project
val mcVersion: String by project
val modPlatform: String by project
val version: String by project
val modId: String by project

base.archivesName = "$modId-$mcVersion-$modPlatform"

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

kotlin {
    jvmToolchain(8)
}

sourceSets.main {
    output.setResourcesDir(sourceSets.main.flatMap { it.java.classesDirectory })
    kotlin.destinationDirectory.set(java.destinationDirectory)
}

// Dependencies:

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.spongepowered.org/maven/")

    maven("https://repo.nea.moe/releases")
    maven("https://maven.notenoughupdates.org/releases")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val shadowModImpl: Configuration by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    implementation(project(":aurora-integration")){
        exclude(group = "org.jetbrains.kotlin")
    }

    shadowModImpl(libs.moulconfig)
    shadowImpl(libs.libautoupdate)
    shadowImpl("org.jetbrains.kotlin:kotlin-reflect:2.3.0")
}

// Minecraft configuration:
loom {
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
    }
}

// Tasks:
tasks.compileJava {
    dependsOn(tasks.processResources)
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    archiveBaseName.set(modId)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", mcVersion)
    inputs.property("modid", modId)

    filesMatching(listOf("mcmod.info")) {
        expand(inputs.properties)
    }

    rename("(.+_at.cfg)", "META-INF/$1")
}


val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    // include project(":aurora-integration") files in the shadow jar
    from(project(":aurora-integration").sourceSets.main.get().output)

    // include kotlinx-serialization dependencies
    from(project(":aurora-integration").configurations.runtimeClasspath.get().filter {
        it.name.startsWith("kotlinx-serialization")
    }.map { if (it.isDirectory) it else zipTree(it) })

    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl, shadowModImpl)
    doLast {
        configurations.forEach {
            println("Copying jars into mod: ${it.files}")
        }
    }
    exclude("META-INF/versions/**")

    // If you want to include other dependencies and shadow them, you can relocate them in here
    relocate("io.github.moulberry.moulconfig", "$baseGroup.deps.moulconfig")
    relocate("moe.nea.libautoupdate", "$baseGroup.deps.libautoupdate")
}

tasks.jar {
    // include project(":aurora-integration") files in the jar
    from(project(":aurora-integration").sourceSets.main.get().output)

    archiveClassifier.set("nodeps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.assemble.get().dependsOn(tasks.remapJar)
