
rootProject.name = "shared-composite"
include("platform")

// Enable access to the parent project's version catalog
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

// Make version catalogs available to buildscript
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
