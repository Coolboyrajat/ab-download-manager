
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
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
    
    extensions.configure<KotlinMultiplatformExtension> {
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
            ).forEach { target ->
                target.binaries.framework {
                    baseName = project.name
                }
            }
        }
        
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(project.dependencies.platform("org.jetbrains.kotlin:kotlin-bom:${rootProject.extra["kotlin.version"]}"))
                    implementation("org.jetbrains.kotlin:kotlin-stdlib")
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines.version"]}")
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
                        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines.version"]}")
                    }
                }
            }
            
            if (enableDesktop) {
                val desktopMain by getting {
                    dependencies {
                        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${rootProject.extra["coroutines.version"]}")
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
