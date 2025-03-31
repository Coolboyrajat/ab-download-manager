plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "ir.amirab.plugin"
version = "1"

repositories {
    mavenCentral()
}

dependencies {
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