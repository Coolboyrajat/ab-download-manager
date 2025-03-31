
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Configure Kotlin Multiplatform for shared modules
 */
fun Project.configureKmp(
    enableAndroid: Boolean = true,
    enableIos: Boolean = true,
    enableDesktop: Boolean = true,
    additionalConfig: (KotlinMultiplatformExtension.() -> Unit)? = null
) {
    plugins.apply("org.jetbrains.kotlin.multiplatform")
    
    kotlin {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        applyDefaultHierarchyTemplate()
        
        if (enableAndroid) {
            androidTarget {
                compilations.all {
                    kotlinOptions {
                        jvmTarget = "17"
                    }
                }
            }
        }
        
        if (enableDesktop) {
            jvm("desktop") {
                compilations.all {
                    kotlinOptions.jvmTarget = "17"
                }
            }
        }
        
        if (enableIos) {
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64()
            ).forEach {
                it.binaries.framework {
                    baseName = project.name
                }
            }
        }
        
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(libs.kotlin.stdlib)
                    implementation(libs.kotlin.coroutines.core)
                }
            }
            
            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
            
            if (enableAndroid) {
                val androidMain by getting {
                    dependencies {
                        implementation(libs.kotlin.coroutines.android)
                    }
                }
            }
            
            if (enableDesktop) {
                val desktopMain by getting {
                    dependencies {
                        implementation(libs.kotlin.coroutines.swing)
                    }
                }
            }
        }
        
        additionalConfig?.invoke(this)
    }
    
    // Configure all Kotlin compile tasks
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers"
            )
        }
    }
}

val Project.libs get() = extensions.getByName("libs") as org.gradle.accessors.dm.LibrariesForLibs
