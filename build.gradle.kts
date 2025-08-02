import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.m0dii"
version = "6.2.1"

tasks.shadowJar {
    relocate("org.bstats", "me.m0dii.onlineplayersgui")
    minimize()
    archiveFileName.set("M0-OnlinePlayersGUI-$version.jar")
}

tasks.processResources {
    inputs.property("version", version)
    filesMatching("**/*.yml") {
        expand("version" to version)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "jitpack.io"
        url = uri("https://jitpack.io")
    }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://repo.extendedclip.com/releases/") }
    maven { url = uri("https://ci.ender.zone/plugin/repository/everything/") }
    maven { url = uri("https://repo.essentialsx.net/releases/") }
    maven { url = uri("https://repo.essentialsx.net/snapshots/") }
}

dependencies {
    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("com.github.cryptomorin:XSeries:9.1.0")

    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.18-3")
    // compileOnly("net.essentialsx:EssentialsX:2.19.4")
    compileOnly("net.ess3:EssentialsX:2.18.2")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}


tasks {
    val runVersions = mapOf(
        8 to setOf("1.8.8"),
        11 to setOf("1.9.4", "1.10.2", "1.11.2"),
        17 to setOf("1.12.2", "1.13.2", "1.14.4", "1.15.2", "1.16.5", "1.17.1", "1.18.2", "1.19.4", "1.20.4"),
        21 to setOf("1.20.6", "1.21.8"),
    )

    runVersions.forEach { (javaVersion, minecraftVersions) ->
        for (version in minecraftVersions) {
            createVersionedRun(version, javaVersion)
        }
    }

    runServer {
        runDirectory(file("run/latest"))
        minecraftVersion("1.21.8")

        downloadPlugins {
            modrinth("viaversion", "5.5.0-SNAPSHOT+793")
            modrinth("viabackwards", "5.4.2")
            modrinth("luckperms", "v5.5.0-bukkit")

            url("https://www.spigotmc.org/resources/1331/download?version=552626") // SuperVanish 6.2.20
        }

        doFirst {
            val eulaFile = file("run/$version/eula.txt")
            eulaFile.parentFile.mkdirs()
            if (!eulaFile.exists()) {
                eulaFile.writeText("eula=true")
            }
        }
    }
}

fun TaskContainerScope.createVersionedRun(
    version: String,
    javaVersion: Int
) = register<RunServer>("runServer${version.replace(".", "_")}") {
    group = "cloud"
    pluginJars.from(shadowJar.flatMap { it.archiveFile })
    minecraftVersion(version)

    downloadPlugins {
        modrinth("viaversion", "5.5.0-SNAPSHOT+793")
        modrinth("viabackwards", "5.4.2")
        modrinth("luckperms", "v5.5.0-bukkit")
        url("https://www.spigotmc.org/resources/1331/download?version=552626")
    }

    runDirectory(file("run/$version"))
    systemProperty("Paper.IgnoreJavaVersion", true)
    javaLauncher.set(
        project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    )

    doFirst {
        val eulaFile = file("run/$version/eula.txt")
        eulaFile.parentFile.mkdirs()
        if (!eulaFile.exists()) {
            eulaFile.writeText("eula=true")
        }
    }
}