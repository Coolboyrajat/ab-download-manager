
import io.github.z4kn4fein.semver.toVersion
import ir.amirab.git_version.core.semanticVersionRegex

plugins {
    // Use direct version strings for all plugins
    kotlin("jvm") version "2.1.10" apply false
    kotlin("android") version "2.1.10" apply false
    kotlin("plugin.serialization") version "2.1.10" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
    id("com.mikepenz.aboutlibraries.plugin") version "11.6.3" apply false
    id("com.github.gmazzo.buildconfig") version "5.3.5" apply false
    id("org.jetbrains.changelog") version "2.2.0" apply false
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
