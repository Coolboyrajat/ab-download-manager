
rootProject.name = "buildSrc"

// Include the composite builds with correct naming
includeBuild("../compositeBuilds/plugins")
includeBuild("../compositeBuilds/shared") {
    name = "shared-composite"
}
