package buildlogic

import io.github.z4kn4fein.semver.Version
import java.io.File

// Create temporary interface until the actual implementation is available
interface InstallerTargetFormat {
    val fileExt: String
    fun fileExtensionWithoutDot(): String = fileExt.removePrefix(".")
    val outputDirName: String
}

// Create temporary enums to stand in for the Platform classes
enum class Platform {
    WINDOWS, LINUX, MACOS, ANDROID;
    
    companion object {
        fun getCurrentPlatform(): Platform = WINDOWS // Default to Windows for build
        fun fromExecutableFileExtension(ext: String): Platform? = when(ext.lowercase()) {
            "exe", "msi" -> WINDOWS
            "deb", "rpm", "appimage" -> LINUX
            "dmg", "pkg" -> MACOS
            "apk" -> ANDROID
            else -> null
        }
    }
    
    object Desktop {
        object Windows : Platform() {
            override fun toString() = "WINDOWS"
        }
        object Linux : Platform() {
            override fun toString() = "LINUX"
        }
        object MacOS : Platform() {
            override fun toString() = "MACOS"
        }
    }
    
    val name: String
        get() = toString()
}

// Create temporary enum for Arch
enum class Arch {
    X64, ARM64;
    
    companion object {
        fun getCurrentArch(): Arch = X64
    }
}

object CiUtils {
    fun getTargetFileName(
        packageName: String,
        appVersion: Version,
        target: InstallerTargetFormat?,
    ): String {
        val fileExtension = when (target) {
            // we use archived for app image distribution (app image is a folder actually so there is no installer so we zip it instead)
            null -> {
                when (Platform.getCurrentPlatform()) {
                    Platform.WINDOWS -> "zip"
                    Platform.LINUX -> "tar.gz"
                    Platform.MACOS -> "tar.gz"
                    Platform.ANDROID -> error("Android not available for now")
                    else -> "zip" // Default case
                }
            }
            else -> target.fileExtensionWithoutDot()
        }

        val platformName = when (target) {
            null -> Platform.getCurrentPlatform().name.lowercase()
            else -> {
                val packageFileExt = target.fileExtensionWithoutDot()
                val platform = requireNotNull(Platform.fromExecutableFileExtension(packageFileExt)) {
                    "can't find platform name with this file extension: ${packageFileExt}"
                }
                platform.name.lowercase()
            }
        }
        val archName = Arch.getCurrentArch().name.lowercase()
        return "${packageName}_${appVersion}_${platformName}_${archName}.${fileExtension}"
    }

    fun getFileOfPackagedTarget(
        baseOutputDir: File,
        target: InstallerTargetFormat,
    ): File {
        val folder = baseOutputDir
        val exeFile = kotlin.runCatching {
            folder.walk().first {
                it.name.endsWith(target.fileExt)
            }
        }.onFailure {
            println("error when finding packaged app for $target in: $baseOutputDir")
        }
        return exeFile.getOrThrow()
    }

    fun getFileOfDistributedArchivedTarget(
        baseOutputDir: File,
    ): File {
        val folder = baseOutputDir
        val extension = when (Platform.getCurrentPlatform()) {
            Platform.LINUX -> "tar.gz"
            Platform.MACOS -> "tar.gz"
            Platform.WINDOWS -> "zip"
            Platform.ANDROID -> "zip"
            else -> "zip" // Default case
        }
        val archiveFile = kotlin.runCatching {
            folder.walk().first {
                it.name.endsWith(extension)
            }
        }.onFailure {
            println("error when finding archive of unpackaged app in: $baseOutputDir")
        }
        return archiveFile.getOrThrow()
    }

    fun copyAndHashToDestination(
        src: File,
        destinationFolder: File,
        name: String,
    ) {
        val destinationExeFile = destinationFolder.resolve(name)
        src.copyTo(destinationExeFile)
        val md5File = destinationFolder.resolve("$name.md5")
        md5File.writeText(HashUtils.md5(src))
    }

    fun movePackagedAndCreateSignature(
        appVersion: Version,
        packageName: String,
        target: InstallerTargetFormat,
        basePackagedAppsDir: File,
        outputDir: File,
    ) {
        require(!outputDir.isFile) {
            "$outputDir is a file"
        }
        outputDir.mkdirs()
        require(outputDir.isDirectory) {
            "$outputDir is not directory"
        }

        val exeFile = getFileOfPackagedTarget(
            baseOutputDir = basePackagedAppsDir,
            target = target
        )

        val newName = getTargetFileName(packageName, appVersion, target)
        copyAndHashToDestination(
            src = exeFile,
            destinationFolder = outputDir,
            name = newName,
        )
    }
}