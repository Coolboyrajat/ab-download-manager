
import io.github.z4kn4fein.semver.toVersion
import ir.amirab.git_version.core.semanticVersionRegex

plugins {
    // Use version catalog for all plugins
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("org.jetbrains.compose") version libs.versions.compose.get() apply false
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("com.google.devtools.ksp") version libs.versions.ksp.get() apply false
    id("com.mikepenz.aboutlibraries.plugin") version libs.versions.aboutLibraries.get() apply false
    id("com.github.gmazzo.buildconfig") version libs.versions.buildConfig.get() apply false
    id("org.jetbrains.changelog") version libs.versions.changelog.get() apply false
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
