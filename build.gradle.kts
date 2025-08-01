plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.m0dii"
version = "6.2.0"

tasks.shadowJar {
    relocate("org.bstats", "me.m0dii.onlineplayersgui")
    minimize()
    archiveFileName.set("M0-OnlinePlayersGUI-$version.jar")
}

tasks.processResources {
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
    runServer {
        downloadPlugins {
            modrinth("viaversion", "5.5.0-SNAPSHOT+793")
            modrinth("viabackwards", "5.4.2")
            modrinth("luckperms", "v5.5.0-bukkit")

            url("https://www.spigotmc.org/resources/1331/download?version=552626") // SuperVanish 6.2.20
        }
        minecraftVersion("1.21.8")
    }
}