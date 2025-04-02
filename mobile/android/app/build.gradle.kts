
plugins {
    id("myPlugins.kotlin")
    id("com.android.application")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.abdownloadmanager.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.abdownloadmanager.android"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        
        // Enable multidex for better performance with large apps
        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }
    
    // Enable R8 full mode
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    
    // Add resource compression for smaller APK size
    aaptOptions {
        cruncherEnabled = true
    }
    
    // Enable code optimization
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
    
    // Configure split APKs for different ABIs
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
    
    // Enable incremental compilation for faster builds
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    
    packaging {
        resources {
            // Exclude unnecessary files from the APK
            excludes += listOf(
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/*.kotlin_module"
            )
        }
    }
}

dependencies {
    implementation(projects.mobile.common)
    implementation(projects.shared.app)
    implementation(projects.shared.appUtils)
    implementation(projects.shared.utils)
    implementation(projects.downloader.core)
    implementation(projects.downloader.monitor)
    
    // Compose
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.animation)
    
    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // Android specific
    coreLibraryDesugaring(libs.android.desugar)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    
    // Optimize app size with multidex
    implementation(libs.androidx.multidex)
}
