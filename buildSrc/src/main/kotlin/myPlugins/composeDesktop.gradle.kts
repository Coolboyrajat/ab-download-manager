package myPlugins

plugins {
    id("org.jetbrains.kotlin.jvm")
}

// Apply the composeBase plugin by directly applying the script
apply(plugin = "myPlugins.composeBase")

dependencies {
    // Get access to the dependencies DSL
    val api by configurations

    api("org.jetbrains.compose.desktop:desktop") {
        exclude("org.jetbrains.compose.material")
    }
    api("org.jetbrains.compose.desktop:desktop-jvm") {
        exclude(group = "org.jetbrains.compose.material")
    }
}
