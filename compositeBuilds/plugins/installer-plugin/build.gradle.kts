
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "ir.amirab.installer"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation("ir.amirab.util:platform:1")
    implementation(libs.handlebarsJava)
}

gradlePlugin {
    plugins {
        create("installerPlugin") {
            id = "ir.amirab.installer"
            implementationClass = "ir.amirab.installer.InstallerPlugin"
        }
    }
}
