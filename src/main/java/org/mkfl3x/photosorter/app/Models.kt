package org.mkfl3x.photosorter.app

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.ZoneId
import kotlin.io.path.pathString

enum class SortMode(val text: String) {
    COPY("Copy"),
    COPY_BY_YEARS("Copy by years"),
    MOVE("Move"),
    MOVE_BY_YEARS("Move by years"),
    REPLACE("Replace")
}

enum class FolderType(val text: String) {
    SOURCE("source"),
    DESTINATION("destination")
}

class DirectoryException(message: String?) : Exception(message)

class File(private val filepath: Path, private val mode: SortMode) {

    private val folder =
        filepath.parent.pathString

    private val size =
        Files.readAttributes(filepath, BasicFileAttributes::class.java).size()

    private val pathDelimiter =
        System.getProperty("os.name").let { if (it.startsWith("Windows")) "\\" else "/" }

    private val extension =
        Files.probeContentType(filepath)?.substringAfterLast(pathDelimiter)!!.lowercase()

    private val lastModifiedTime =
        Files.readAttributes(filepath, BasicFileAttributes::class.java).lastModifiedTime()

    private val filenamePattern =
        if (System.getProperty("os.name").startsWith("Mac")) "yyyy-MMM-dd_HH-mm-ss" else "yyyy-MMM-dd_HH:mm:ss"

    private val md5Hash = MessageDigest.getInstance("MD5").digest(Files.readAllBytes(filepath))
        .joinToString("") { String.format("%02x", it) }

    fun getFilePath() = filepath.pathString

    fun sort(source: String, destination: String, mode: SortMode, copyIndex: Int = 0): String {
        val destinationFilepath = getDestinationFilepath(source, destination)
        try {
            val targetDestination = if (copyIndex > 0) addCopyIndex(destinationFilepath, copyIndex) else destinationFilepath
            when (mode) {
                SortMode.COPY,
                SortMode.COPY_BY_YEARS -> Files.copy(filepath, targetDestination)
                SortMode.MOVE,
                SortMode.MOVE_BY_YEARS,
                SortMode.REPLACE -> Files.move(filepath, targetDestination)
            }
            return "'$filepath' -> '$targetDestination'"
        } catch (e: NoSuchFileException) {
            Files.createDirectories(destinationFilepath.parent)
            return sort(source, destination, mode, copyIndex)
        } catch (e: FileAlreadyExistsException) {
            return sort(source, destination, mode, copyIndex + 1)
        }
    }

    private fun addCopyIndex(filepath: Path, copyIndex: Int): Path {
        val extensionPointIndex = filepath.toString().indexOfLast { it == '.' }
        return Paths.get(filepath.toString().replaceRange(extensionPointIndex, extensionPointIndex, " ($copyIndex)"))
    }

    private fun getDestinationFilepath(sourceFolder: String, destinationFolder: String): Path {
        val path = if (mode == SortMode.COPY_BY_YEARS)
            "$destinationFolder$pathDelimiter${getFileYear()}$pathDelimiter${formatFilename()}"
        else
            filepath.toString().replace(filepath.fileName.toString(), formatFilename()).let {
                if (mode == SortMode.REPLACE) it else it.replace(sourceFolder, destinationFolder)
            }
        return Paths.get(path)
    }

    private fun formatFilename() =
        SimpleDateFormat(filenamePattern).format(lastModifiedTime.toMillis()) + ".$extension"

    private fun getFileYear() =
        lastModifiedTime.toInstant().atZone(ZoneId.of("UTC")).year

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (folder != other.folder) return false
        if (size != other.size) return false
        if (extension != other.extension) return false
        if (md5Hash != other.md5Hash) return false

        return true
    }

    override fun hashCode(): Int {
        var result = folder.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + extension.hashCode()
        result = 31 * result + md5Hash.hashCode()
        return result
    }
}