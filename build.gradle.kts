plugins {
    id("java")
    id("version-catalog")
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij)
}

group = "today.thisaay"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        intellijDependencies()
    }
}

intellijPlatform {
    buildSearchableOptions = false
    instrumentCode = true
    projectName = project.name
    autoReload = true

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "241"
            untilBuild = "241.*"
        }
    }
}

dependencies {

    intellijPlatform {
        instrumentationTools()
        androidStudio("2024.1.1.12")
        bundledPlugins("org.jetbrains.kotlin","com.intellij.java")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("io.ktor:ktor-client-core:2.3.12"){
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8")
    }
    implementation("io.ktor:ktor-client-okhttp:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")

    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")

    implementation("ch.qos.logback:logback-classic:1.5.6")
}
