
plugins{
    `kotlin-dsl`
}

// Define Kotlin version for consistent use
val kotlinVersion = "2.1.10"

repositories {
    gradlePluginPortal()
    mavenCentral()
    google() // Added for Android dependencies
}

dependencies{
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
    
    // Use explicit plugin dependencies instead of version catalog in buildSrc
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.7.3")
    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:$kotlinVersion")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.1.10-1.0.31")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    implementation("org.jetbrains.intellij.plugins:gradle-changelog-plugin:2.2.0")
    implementation("com.github.gmazzo.buildconfig:plugin:5.3.5")
    implementation("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:11.6.3")
    implementation("io.github.z4kn4fein:semver:2.0.0")
    
    // Android target support
    implementation("com.android.tools.build:gradle:7.4.2") 
    
    // Composite builds
    implementation("ir.amirab.util:platform:1")
    implementation("ir.amirab.plugin:git-version-plugin:1")
    implementation("ir.amirab.plugin:installer-plugin:1")
}
