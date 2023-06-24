package org.mkfl3x.photosorter.app

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import javax.swing.JOptionPane
import javax.swing.JTextArea
import kotlin.io.path.isDirectory
import kotlin.io.path.isHidden

class PhotoSorter {

    @Throws(DirectoryException::class)
    fun sortFiles(mode: SortMode, source: String, destination: String, log: JTextArea) {
        // logging method
        fun writeLog(message: String) = log.append("$message\n")
        writeLog("Sorting started")

        // check folders
        checkFolder(source, FolderType.SOURCE)
        if (mode != SortMode.REPLACE)
            checkFolder(destination, FolderType.DESTINATION)

        // create directories if not replace mode
        if (mode != SortMode.REPLACE)
            Files.walk(Paths.get(source))
                .filter { it.isDirectory() }
                .map { Paths.get(it.toString().replace(source, destination)) }
                .forEach { Files.createDirectory(it) }

        // sort files
        Files.walk(Paths.get(source)).filter { it.isDirectory().not() && it.isHidden().not() }.map { File(it, mode) }
            .forEach { file ->
                file.getDestinationFilepath(source, destination).apply {
                    when (mode) {
                        SortMode.COPY -> Files.copy(file.filepath, this)
                        SortMode.MOVE,
                        SortMode.REPLACE -> Files.move(file.filepath, this)
                    }
                    writeLog("'${file.filepath}' -> '$this'")
                }
            }
        writeLog("Sorting finished")
    }

    fun getModeByText(text: String) = SortMode.values().first { it.text == text }

    private fun checkFolder(path: String, type: FolderType) {
        Paths.get(path).apply {
            when (type) {
                FolderType.SOURCE -> {
                    if (Files.exists(this).not())
                        throw DirectoryException("Source folder not found")
                    if (Files.isDirectory(this).not())
                        throw DirectoryException("Source folder should be a directory, not a file")
                }

                FolderType.DESTINATION -> {
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
        "Directory error",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        null,
        null
    )
}

private class File(val filepath: Path, private val mode: SortMode) {

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