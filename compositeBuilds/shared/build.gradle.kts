// Configuration for the shared composite build
plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Configure repositories
repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}