plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "today.thisaay"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

intellij {

    version.set(libs.versions.intellijBuild)
    type.set(libs.versions.intellijTarget) // Target IDE Platform

    plugins.set(
        listOf(
            libs.plugins.intellij.kotlin.get().pluginId,
        )
    )
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set(libs.versions.intellijSince)
        untilBuild.set(libs.versions.intellijUntil)
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

