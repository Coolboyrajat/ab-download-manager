import io.github.z4kn4fein.semver.toVersion
import ir.amirab.git_version.core.semanticVersionRegex

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("ir.amirab.git-version-plugin") version "0.0.8"
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

//version="0.0.8"
version = gitVersion.getVersion() ?: fallBackVersion
logger.lifecycle("version: $version")