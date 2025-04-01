
plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // Load version from the root project's versions
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.1.10")

    // Use explicit plugin dependencies with consistent versions
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.7.3")
    // Remove reference to non-existent kotlin-plugin-compose
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.1.10-1.0.31")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.1.10")
    implementation("org.jetbrains.intellij.plugins:gradle-changelog-plugin:2.2.0")
    implementation("com.github.gmazzo.buildconfig:plugin:5.3.5")
    implementation("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:11.6.3")
    implementation("io.github.z4kn4fein:semver:2.0.0")

    // Android target support
    implementation("com.android.tools.build:gradle:8.1.0") 

    // Version catalog libraries replaced with direct dependencies
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.1.10")
    implementation("io.github.z4kn4fein:semver:2.0.0")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")

    // Add dependencies from shared composite module with correct group names
    implementation("ir.amirab.util:platform:1")
}

