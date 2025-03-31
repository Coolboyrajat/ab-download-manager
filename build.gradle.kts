
import io.github.z4kn4fein.semver.toVersion
import ir.amirab.git_version.core.semanticVersionRegex

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.pluginKsp) apply false
    alias(libs.pluginAboutLibraries) apply false
    alias(libs.pluginBuildConfig) apply false
    alias(libs.pluginChangeLog) apply false
    id("ir.amirab.git-version-plugin")
}

val defaultSemVersion = "1.0.0"
val fallBackVersion = "$defaultSemVersion-untagged"

gitVersion {
    on {
        branch(".+") {
            "$defaultSemVersion-${it.refInfo.shortenName}-snapshot"
        }
        tag("v?${semanticVersionRegex}") {
            it.matchResult.groups.get("version")!!.value
        }
        commit {
            "$defaultSemVersion-sha.${it.refInfo.commitHash.take(5)}"
        }
    }
}

// Set version using the git version plugin with fallback
version = (gitVersion.getVersion() ?: fallBackVersion).toVersion()
logger.lifecycle("version: $version")

// Common configuration for all subprojects
subprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
