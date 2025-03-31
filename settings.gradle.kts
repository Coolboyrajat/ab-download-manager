
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    
    // Include the plugins from the composite build
    includeBuild("./compositeBuilds/plugins")
    includeBuild("./compositeBuilds/shared") {
        name = "shared-composite"
    }
    
    // Set resolutionStrategy for plugins to avoid conflicts
    resolutionStrategy {
        eachPlugin {
            // Use the correct implementation for Kotlin Multiplatform
            if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
            if (requested.id.id == "org.jetbrains.compose") {
                useModule("org.jetbrains.compose:compose-gradle-plugin:${requested.version}")
            }
        }
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

// Include composite builds
includeBuild("./compositeBuilds/shared") {
    name = "shared-composite"
}
