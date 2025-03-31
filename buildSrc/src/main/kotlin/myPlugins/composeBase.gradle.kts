
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.compose.ComposePlugin

plugins {
    id("org.jetbrains.compose") apply false
    kotlin("plugin.serialization") apply false
}

// Apply the core plugins needed for compose applications
project.plugins.apply("org.jetbrains.compose")
project.plugins.apply("kotlin.plugin.serialization")

// Configure compose dependencies
dependencies {
    // Use the compose BOM to manage compose dependencies
    implementation(platform(compose.bom))
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.components.resources)
    
    // Add preview support
    implementation(compose.preview)
    debugImplementation(compose.uiTooling)
}
import org.gradle.kotlin.dsl.kotlin

plugins {
    id("org.jetbrains.compose")
}

// Configure compose dependencies
dependencies {
    val composeBom = platform("org.jetbrains.compose:compose-bom:${project.rootProject.property("compose.version")}")
    implementation(composeBom)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    
    // Add common compose dependencies
    @Suppress("UNUSED_VARIABLE")
    val animation = implementation(compose.animation)
}
