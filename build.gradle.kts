import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    kotlin("jvm") version "1.3.31"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "com.jordanstremming"
version = "1.0.0"

java {
    targetCompatibility = JavaVersion.VERSION_1_5
    targetCompatibility = JavaVersion.VERSION_1_5
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compile(kotlin("stdlib"))
    compileOnly("org.bukkit:bukkit:1.13.2-R0.1-SNAPSHOT")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    minimize()
}

tasks.processResources {
    filter(ReplaceTokens::class, "tokens" to mapOf("version" to project.version, "artifactId" to project.name))
}