import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.0"
    kotlin("plugin.compose") version "2.1.20"
}

group = "ru.ifmo.freertostracker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("cafe.adriel.voyager:voyager-navigator:1.0.0")
    implementation ("io.ktor:ktor-client-core-jvm:3.1.2")
    implementation("io.ktor:ktor-client-cio-jvm:3.1.2")
    implementation("io.ktor:ktor-client-websockets:3.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Application-desktop"
            packageVersion = "1.0.0"
        }
    }
}
