plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "ir.amirab"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.7.0.202309050840-r")
    implementation("io.github.z4kn4fein:semver:1.4.2")
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
}

gradlePlugin {
    plugins {
        create("git-version-plugin") {
            id = "ir.amirab.git-version-plugin"
            implementationClass = "ir.amirab.git_version.GitVersionPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "git-version-plugin"
            from(components["java"])
        }
    }
}