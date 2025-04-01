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
    configurations.implementation.get().dependencies.add(
        project.dependencies.platform("org.jetbrains.compose:compose-bom:$composeBomVersion")
    )
    configurations.implementation.get().dependencies.add(
        project.dependencies.create("org.jetbrains.compose.runtime:runtime")
    )
    configurations.implementation.get().dependencies.add(
        project.dependencies.create("org.jetbrains.compose.foundation:foundation")
    )
    configurations.implementation.get().dependencies.add(
        project.dependencies.create("org.jetbrains.compose.material3:material3")
    )
    configurations.implementation.get().dependencies.add(
        project.dependencies.create("org.jetbrains.compose.ui:ui")
    )
    configurations.implementation.get().dependencies.add(
        project.dependencies.create("org.jetbrains.compose.components:components-resources")
    )
    
    // Add preview support
    configurations.implementation.get().dependencies.add(
        project.dependencies.create("org.jetbrains.compose.ui:ui-tooling-preview")
    )
    configurations.debugImplementation.get().dependencies.add(
        project.dependencies.create("org.jetbrains.compose.ui:ui-tooling")
    )
}