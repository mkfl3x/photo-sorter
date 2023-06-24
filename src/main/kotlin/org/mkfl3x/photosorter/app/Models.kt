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

    private val filenamePattern =
        if (System.getProperty("os.name").startsWith("Mac")) "yyyy-MMM-dd_HH-mm-ss" else "yyyy-MMM-dd_HH:mm:ss"

    fun getDestinationFilepath(sourceFolder: String, destinationFolder: String): Path = Paths.get(
        filepath.toString().replace(filepath.fileName.toString(), formatFilename()).let {
            if (mode != SortMode.REPLACE) it.replace(sourceFolder, destinationFolder) else it
        }
    )

    private fun formatFilename() =
        SimpleDateFormat(filenamePattern).format(getLastModifiedTime().toMillis()) + ".${getExtension() ?: "?"}"

    private fun getLastModifiedTime() =
        Files.readAttributes(filepath, BasicFileAttributes::class.java).lastModifiedTime()

    private fun getExtension() =
        Files.probeContentType(filepath)?.substringAfterLast("/")
}