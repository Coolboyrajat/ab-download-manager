
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:app"))
                implementation(project(":shared:resources"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }
    }
}
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

kotlin {
    androidTarget()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    cocoapods {
        summary = "Shared iOS module for ABDownloadManager"
        homepage = "https://github.com/yourusername/ABDownloadManager"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../Podfile")
        framework {
            baseName = "ABDownloadManagerShared"
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.coroutines.core)
                implementation(projects.shared.utils)
                implementation(projects.shared.app)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlin.coroutines.android)
            }
        }
        
        val iosMain by getting {
            dependsOn(commonMain)
        }
    }
}

android {
    namespace = "com.abdownloadmanager.ios.shared"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
