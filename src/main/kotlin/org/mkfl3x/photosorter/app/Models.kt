package org.mkfl3x.photosorter.app

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat

enum class SortMode(val text: String) {
    COPY("Copy"),
    MOVE("Move"),
    REPLACE("Replace")
}

enum class FolderType {
    SOURCE,
    DESTINATION
}

class DirectoryException(message: String?) : Exception(message)

class File(val filepath: Path, private val mode: SortMode) {

    private val size =
        Files.readAttributes(filepath, BasicFileAttributes::class.java).size()

    private val extension =
        Files.probeContentType(filepath)?.substringAfterLast("/")

    private val lastModifiedTime =
        Files.readAttributes(filepath, BasicFileAttributes::class.java).lastModifiedTime()

    private val filenamePattern =
        if (System.getProperty("os.name").startsWith("Mac")) "yyyy-MMM-dd_HH-mm-ss" else "yyyy-MMM-dd_HH:mm:ss"

    fun getDestinationFilepath(sourceFolder: String, destinationFolder: String): Path = Paths.get(
        filepath.toString().replace(filepath.fileName.toString(), formatFilename()).let {
            if (mode != SortMode.REPLACE) it.replace(sourceFolder, destinationFolder) else it
        }
    )

    private fun formatFilename() =
        SimpleDateFormat(filenamePattern).format(lastModifiedTime.toMillis()) + ".${extension ?: "?"}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (size != other.size) return false
        if (extension != other.extension) return false
        return lastModifiedTime == other.lastModifiedTime
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + (extension?.hashCode() ?: 0)
        result = 31 * result + (lastModifiedTime?.hashCode() ?: 0)
        return result
    }
}