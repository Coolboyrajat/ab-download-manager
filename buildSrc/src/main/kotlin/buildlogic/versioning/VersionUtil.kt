package buildlogic.versioning

import buildlogic.Platform
import io.github.z4kn4fein.semver.Version
import org.gradle.api.Project
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

fun Project.getAppVersion(): Version {
    return rootProject.version as Version
}

fun Project.getAppVersionString(): String {
    return rootProject.version.toString()
}

fun Project.getAppName(): String {
    return rootProject.name
}

fun Project.getPrettifiedAppName(): String {
    return "AB Download Manager"
}
fun Project.getAppDataDirName(): String {
    return ".abdm"
}

fun Project.getApplicationPackageName(): String {
    return "com.abdownloadmanager"
}

private fun guessTargetFormatBasedOnCurrentOs() = when (Platform.getCurrentPlatform()) {
    Platform.LINUX -> TargetFormat.Deb
    Platform.MACOS -> TargetFormat.Dmg
    Platform.WINDOWS -> TargetFormat.Msi
    Platform.ANDROID -> error("we are executing gradle in desktop :D")
    else -> TargetFormat.Msi // Default to MSI
}

fun Project.getAppVersionStringForPackaging(targetFormat: TargetFormat? = null): String {
    val v = getAppVersion()
    val simple = { v.run { "$major.$minor.$patch" } }
    val semantic = { v.toString() }
    val forRpm = { semantic().replace("-", "_") }
    return when (targetFormat ?: guessTargetFormatBasedOnCurrentOs()) {
        TargetFormat.Rpm -> forRpm()
        TargetFormat.Deb, TargetFormat.AppImage -> semantic()
        TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Pkg -> simple()
    }
}
