rootProject.name = "plugins"

include(":git-version-plugin")
include(":installer-plugin")

// Include the shared composite build
includeBuild("../shared") {
    name = "shared-composite"
}

dependencyResolutionManagement{
    versionCatalogs {
        create("libs"){
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}