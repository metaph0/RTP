plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
}

group = "net.gahvila"
version = "0.17.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.gahvila.net/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("net.crashcraft:crashclaim:1.0.42")
    compileOnly("org.popcraft:chunky-common:1.4.10")
    compileOnly("org.popcraft:chunkyborder-common:1.2.13")
    compileOnly("org.popcraft:chunkyborder-bukkit:1.2.13")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.10")
}

publishing {
    repositories {
        maven {
            name = "gahvila"
            url = uri("https://repo.gahvila.net/snapshots/")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.gahvila"
            artifactId = "rtp"
            version = findProperty("version").toString()
            from(components["java"])
        }
    }
}

tasks {
    processResources {
        expand(project.properties)
    }
}