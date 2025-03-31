package ir.amirab.git_version

import ir.amirab.git_version.core.GitVersionExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger

class GitVersionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        val gitVersionExtension = GitVersionExtension()
        target.extensions.add("gitVersion", gitVersionExtension)
        gitVersionExtension.currentWorkingDirectory = target.rootDir
        gitVersionExtension.setLogger(target.logger)
    }
}
package ir.amirab.git_version

import ir.amirab.git_version.core.CiReferenceProvider
import org.gradle.api.Plugin
import org.gradle.api.Project

class GitVersionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Basic implementation for the Git version plugin
        target.extensions.create("gitVersion", GitVersionExtension::class.java)
    }
}

open class GitVersionExtension {
    var isInCi: Boolean = CiReferenceProvider.isInCi()
    
    fun getVersionName(): String {
        return "1.0.0" // Default fallback version
    }
}
