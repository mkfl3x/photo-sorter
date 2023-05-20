package org.mkfl3x.photosorter.app

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.text.SimpleDateFormat
import javax.swing.JTextArea
import kotlin.io.path.isDirectory

class PhotoSorter {

    @Throws(BadFolderException::class)
    fun sortFiles(mode: SortMode, source: String, destination: String, logWindow: JTextArea) {

        // check folders
        checkFolder(source, FolderType.SOURCE)
        checkFolder(destination, FolderType.DESTINATION)

        // sort
        Files.walk(Paths.get(source)).forEach {
            if (it.isDirectory() && mode != SortMode.REPLACE) {
                if (it.toString() == source)
                    return@forEach // ignore first level directory
                Files.createDirectory(Paths.get(it.toString().replace(source, destination)))
            } else {
                val file = getFileInfo(it) ?: return@forEach // TODO: refactor it
                val newFileName = formatFileName(file)
                // TODO: handle duplicates
                when (mode) {
                    SortMode.COPY -> {
                        val newPath = Paths.get(
                            file.filepath.toString()
                                .replace(source, destination)
                                .replace(file.filename, newFileName)
                        )
                        Files.copy(file.filepath, newPath)
                    }
                    SortMode.MOVE -> {
                        val newPath = Paths.get(
                            file.filepath.toString()
                                .replace(source, destination)
                                .replace(file.filename, newFileName)
                        )
                        Files.move(file.filepath, newPath)
                    }
                    SortMode.REPLACE -> {
                        val newPath = Paths.get(
                            file.filepath.toString()
                                .replace(file.filename, newFileName)
                        )
                        Files.move(file.filepath, newPath)
                    }
                }
                logWindow.append("${file.filename} -> $newFileName\n")
            }
        }
    }

    fun getModeByText(text: String): SortMode {
        return SortMode.values().first { it.text == text }
    }

    // TODO: refactor it
    private fun checkFolder(path: String, type: FolderType) {
        Paths.get(path).apply {
            when (type) {
                FolderType.SOURCE -> {
                    if (Files.exists(this).not())
                        throw BadFolderException("Source folder not found")
                    if (Files.isDirectory(this).not())
                        throw BadFolderException("Source folder should be a directory")
                }

                FolderType.DESTINATION -> {
                    if (Files.notExists(this))
                        Files.createDirectory(this)
                    else {
                        // TODO: already exists TODO()
                        if (Files.isDirectory(this).not())
                            throw BadFolderException("Destination folder should be a directory")
                    }
                }
            }
        }
    }

    // TODO: refactor it
    private fun getFileInfo(filepath: Path): FileInfo? = try {
        FileInfo(
            filepath.fileName.toString(),
            filepath,
            Files.probeContentType(filepath).substringAfterLast("/"),
            Files.readAttributes(filepath, BasicFileAttributes::class.java).lastModifiedTime()
        )
    } catch (e: Exception) {
        println("${filepath.fileName} : ${e.message}")
        null
    }

    private fun formatFileName(fileInfo: FileInfo) =
        // TODO: fix '/' instead ':' for MacOS
        SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS").format(fileInfo.creationTime.toMillis()) + ".${fileInfo.extension}"

    private data class FileInfo(
        val filename: String,
        val filepath: Path,
        val extension: String,
        val creationTime: FileTime
    )
}

