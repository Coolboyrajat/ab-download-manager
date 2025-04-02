
plugins {
    id("myPlugins.kotlinMultiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.shared.app)
                implementation(projects.shared.appUtils)
                implementation(projects.shared.utils)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.animation)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(projects.downloader.core)
                implementation(projects.downloader.monitor)
            }
        }
        
        val iosMain by creating {
            dependsOn(commonMain)
        }
        
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

android {
    namespace = "com.abdownloadmanager.mobile"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 21
    }
}
