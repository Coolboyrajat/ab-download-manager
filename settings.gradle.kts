pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
    }

    plugins {
        // Use version from version catalog for Kotlin plugins
        val kotlinVersion = extra["kotlin.version"] as String
        // Get the compose version from the version catalog or use a default
        val composeVersion = "1.7.3" // Use the version from libs.versions.toml

        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)
        kotlin("plugin.serialization").version(kotlinVersion)
        id("org.jetbrains.compose").version(composeVersion)
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// Enable type-safe project accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ABDownloadManager"

// Include composite builds
includeBuild("compositeBuilds/plugins")
includeBuild("compositeBuilds/shared") {
    name = "shared-composite"
}

// Desktop modules
include(":desktop:app")
include(":desktop:app-utils")
include(":desktop:custom-window-frame")
include(":desktop:shared")
include(":desktop:tray:common")
include(":desktop:tray:windows")
include(":desktop:tray:linux")
include(":desktop:tray:mac")
include(":desktop:mac_utils")

// Downloader modules
include(":downloader:core")
include(":downloader:monitor")

// Integration module
include(":integration:server")

// Shared modules
include(":shared:utils")
include(":shared:app")
include(":shared:app-utils")
include(":shared:compose-utils")
include(":shared:resources")
include(":shared:resources:contracts")
include(":shared:config")
include(":shared:updater")
include(":shared:auto-start")
include(":shared:nanohttp4k")

// Android module
include(":android:app")

// iOS modules
include(":ios:app")
include(":ios:shared")
include(":mobile:huawei:app")

// Expose project properties
val composeVersion: String by settings
val kotlinVersion: String by settings