
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
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
        
        // Configure source sets using type-safe source set configuration
        val kotlinVersion = project.rootProject.property("kotlin.version").toString()
        val coroutinesVersion = project.rootProject.property("coroutines.version").toString()
        
        // Configure common main source set
        sourceSets.maybeCreate("commonMain").dependencies {
            implementation(project.dependencies.platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
            implementation("org.jetbrains.kotlin:kotlin-stdlib")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        }
        
        // Configure common test source set
        sourceSets.maybeCreate("commonTest").dependencies {
            implementation(kotlin("test"))
        }
        
        // Configure platform-specific source sets if enabled
        if (enableAndroid) {
            sourceSets.maybeCreate("androidMain").dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
            }
        }
        
        if (enableDesktop) {
            sourceSets.maybeCreate("desktopMain").dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
            }
        }
        
        // Apply additional configuration if provided
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
