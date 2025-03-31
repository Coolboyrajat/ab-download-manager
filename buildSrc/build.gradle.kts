plugins{
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google() // Added for Android dependencies
}
dependencies{
    implementation(libs.pluginKotlin)
    implementation(libs.pluginComposeCompiler)
    implementation(libs.pluginKsp)
    implementation(libs.pluginSerialization)
    implementation(libs.pluginComposeMultiplatform)
    implementation(libs.pluginChangeLog)
    implementation(libs.pluginBuildConfig)
    implementation(libs.pluginAboutLibraries)
    implementation(libs.semver)
    
    // Add Kotlin Multiplatform plugin dependencies
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:${libs.versions.kotlin.get()}")
    implementation("com.android.tools.build:gradle:7.4.2") // For android target support
    
    // Use the actual artifacts from the composite builds
    implementation("ir.amirab.util:platform:1")
    implementation("ir.amirab.plugin:git-version-plugin:1")
    implementation("ir.amirab.plugin:installer-plugin:1")
}