plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.10"
    id("com.github.weave-mc.weave-gradle") version "bcf6ab0279"
}

group = "club.maxstats.weave.examplemod"
version = "1.0-SNAPSHOT"

minecraft.version("1.8.9")

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("net.weavemc.api:1.8:1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}