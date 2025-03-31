
plugins {
    kotlin("multiplatform") apply false
    id("com.android.library") apply false
    kotlin("plugin.serialization") apply false
    id("org.jetbrains.compose") apply false
    id("com.google.devtools.ksp") apply false
}

// This is a parent module that configures common dependencies and settings
// for all shared modules
