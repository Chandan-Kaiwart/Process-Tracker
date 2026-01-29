import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.compose") version "1.5.11"
    kotlin("plugin.serialization") version "1.9.21"  // ADD THIS LINE - Critical for serialization to work!
}

group = "com.progresstracker"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // For date/time handling
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // For JSON serialization (saving data)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ProgressTracker"
            packageVersion = "1.0.0"

            macOS {
                iconFile.set(project.file("src/main/resources/icon.icns"))
                bundleID = "com.progresstracker.app"
            }
        }
    }
}