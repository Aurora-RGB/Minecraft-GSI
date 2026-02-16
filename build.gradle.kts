subprojects {
    val version: String by project
    val modId: String by project
    val modName: String by project
    val modUrl: String by project
    val authorList: String by project
    val mcVersion: String? by project
    val modPlatform: String? by project
    val loader_version_range: String? by project
    val minecraft_version_range: String? by project

    // set archive name to {modid}-{mcVersion}.jar
    tasks.withType<Jar>().configureEach {
        archiveBaseName.set(modId)
        archiveVersion.set("$mcVersion-$modPlatform")
        destinationDirectory.set(file("${rootProject.projectDir}/output/jars"))
    }

    tasks.withType<ProcessResources>().configureEach {
        inputs.property("version", version)
        inputs.property("modid", modId)
        inputs.property("modName", modName)
        inputs.property("modUrl", modUrl)
        inputs.property("authorList", authorList)
        inputs.property("mcVersion", mcVersion)
        loader_version_range?.let { inputs.property("loader_version_range", it) }
        minecraft_version_range?.let { inputs.property("minecraft_version_range", it) }

        filesMatching(listOf("**/mcmod.info", "**/mods.toml", "**/pack.mcmeta", "**/fabric.mod.json")) {
            expand(inputs.properties)
        }

        rename("(.+_at.cfg)", "META-INF/$1")
    }
}