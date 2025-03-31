plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}
repositories {
    mavenCentral()
}
group = "ir.amirab.plugin"
version = "1"

dependencies {
    implementation("ir.amirab.util:platform:1")
    implementation(libs.handlebarsJava)
}
gradlePlugin {
    plugins {
        create("installer-plugin") {
            id = "ir.amirab.installer-plugin"
            implementationClass = "ir.amirab.installer.InstallerPlugin"
        }
    }
}

