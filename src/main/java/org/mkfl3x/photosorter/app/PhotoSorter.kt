package org.mkfl3x.photosorter.app

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JOptionPane
import javax.swing.JTextArea
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isHidden

class PhotoSorter {

    private val supportedFTypes = listOf("png", "jpg", "jpeg")

    @Throws(DirectoryException::class)
    fun sortFiles(mode: SortMode, source: String, destination: String, log: JTextArea) {
        fun writeLog(message: String) = log.append("$message\n")
        checkFolder(source, FolderType.SOURCE)
        if (mode != SortMode.REPLACE)
            checkFolder(destination, FolderType.DESTINATION)
        Thread {
            // create directories structure
            if (mode != SortMode.REPLACE)
                Files.walk(Paths.get(source))
                    .filter { it.isDirectory() }
                    .map { Paths.get(it.toString().replace(source, destination)) }
                    .forEach { Files.createDirectory(it) }
            // collect all files
            writeLog("Collecting files... Supported formats: ${supportedFTypes.joinToString { it }}")
            val allFiles = Files.walk(Paths.get(source))
                .filter { it.isDirectory().not() && it.isHidden().not() && it.extension.lowercase() in supportedFTypes }
                .map { File(it, mode) }
                .toList()
            writeLog("Files found: ${allFiles.size}")
            // exclude duplicates
            writeLog("Duplicates detection...")
            val targetFiles = allFiles.toSet().apply {
                if (this.isNotEmpty()) {
                    writeLog("Following duplicates detected and ${if (mode == SortMode.REPLACE) "deleted" else "excluded from sorting"}:")
                    allFiles.map { it.getFilePath() }.subtract(this.map { it.getFilePath() }).forEach {
                        if (mode != SortMode.COPY)
                            Files.delete(Paths.get(it))
                        writeLog(it)
                    }
                }
                writeLog("")
            }
            // sorting
            writeLog("Sorting started")
            targetFiles.forEach { writeLog(it.sort(source, destination, mode)) }
            writeLog("Sorting finished. ${targetFiles.size} files handled")
        }.start()
    }

    fun getModeByText(text: String) = SortMode.values().first { it.text == text }

    private fun checkFolder(path: String, type: FolderType) {
        Paths.get(path).apply {
            if (path.isEmpty() || path.isBlank())
                throw DirectoryException("Please specify ${type.text} directory")
            when (type) {
                FolderType.SOURCE -> {
                    if (Files.exists(this).not())
                        throw DirectoryException("Source folder not found")
                    if (Files.isDirectory(this).not())
                        throw DirectoryException("Source folder should be a directory, not a file")
                }
                FolderType.DESTINATION -> {
                    if (path.isEmpty() || path.isBlank())
                        throw DirectoryException("Please specify destination directory")
                    if (Files.exists(this) && overrideDestinationDialog() == 0)
                        deleteFolder(this)
                }
            }
        }
    }

    private fun deleteFolder(path: Path) = Files.walk(path)
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete)

    private fun overrideDestinationDialog() = JOptionPane.showOptionDialog(
        null, "Destination folder already exists. Override?",
        "Destination folder exists",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        null,
        null
    )
}