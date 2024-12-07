plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
}

group = "biz.donvi"
version = "0.16.0"
description = "IF"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.crashcraft:CrashClaim:1.0.42")
    compileOnly("org.popcraft:chunky-common:1.4.10")
    compileOnly("org.popcraft:chunkyborder-common:1.2.13")
    compileOnly("org.popcraft:chunkyborder-bukkit:1.2.13")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.10")
    implementation("io.papermc:paperlib:1.0.7")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${version}.jar")
        relocate("io.papermc", "biz.donvi.jakesRTP.libs.io.papermc")
        relocate("biz.donvi.argsChecker", "biz.donvi.jakesRTP.libs.biz.donvi.argsChecker")
        relocate("biz.donvi.evenDistribution", "biz.donvi.jakesRTP.libs.biz.donvi.evenDistribution")
    }

    processResources {
        expand(project.properties)
    }
}