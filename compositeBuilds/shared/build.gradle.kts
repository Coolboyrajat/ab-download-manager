// Configuration for the shared composite build
plugins {
    // Use direct version reference from the parent project's version catalog
    id("org.jetbrains.kotlin.jvm").version("2.1.10")
}

// Configure repositories
repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}