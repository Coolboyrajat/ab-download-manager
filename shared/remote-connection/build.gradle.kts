
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm("desktop")
    
    android()

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":shared:utils"))
                implementation(project(":shared:app-utils"))
                implementation(project(":shared:config"))
                implementation(project(":shared:nanohttp4k"))
                
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.koin.core)
                implementation(libs.zxing.core)
            }
        }
        
        named("androidMain") {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.zxing.android.embedded)
            }
        }
        
        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.webcam.capture)
            }
        }
    }
}
