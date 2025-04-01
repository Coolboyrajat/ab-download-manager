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
    val composeBomVersion = project.rootProject.properties["compose.version"]?.toString() ?: "1.5.0"
    // These functions need to be accessed through the configurations object
    "implementation"(platform("org.jetbrains.compose:compose-bom:$composeBomVersion"))
        "implementation"("org.jetbrains.compose.runtime:runtime")
        "implementation"("org.jetbrains.compose.foundation:foundation")
        "implementation"("org.jetbrains.compose.material3:material3")
        "implementation"("org.jetbrains.compose.ui:ui")
        "implementation"("org.jetbrains.compose.components:components-resources")
        
        // Add preview support
        "implementation"("org.jetbrains.compose.ui:ui-tooling-preview")
        "debugImplementation"("org.jetbrains.compose.ui:ui-tooling")
}