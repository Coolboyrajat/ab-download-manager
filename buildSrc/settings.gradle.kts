// Include the composite builds
includeBuild("../compositeBuilds/plugins")
includeBuild("../compositeBuilds/shared") {
    name = "shared-composite"
}

dependencyResolutionManagement{
    versionCatalogs {
        create("libs"){
            from(files("../gradle/libs.versions.toml"))
        }
    }
}