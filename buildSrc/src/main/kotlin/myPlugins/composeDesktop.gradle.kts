package myPlugins

plugins {
    id("myPlugins.composeBase")
}

dependencies {
    api(compose.desktop.currentOs){
        exclude("org.jetbrains.compose.material")
    }
}
plugins {
    id("myPlugins.composeBase")
    id("org.jetbrains.kotlin.jvm")
}

// Desktop-specific compose dependencies
dependencies {
    implementation(compose.desktop.currentOs)
}
