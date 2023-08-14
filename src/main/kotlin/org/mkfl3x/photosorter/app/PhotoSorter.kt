package org.mkfl3x.photosorter.app

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JOptionPane
import javax.swing.JTextArea
import kotlin.io.path.isDirectory
import kotlin.io.path.isHidden

class PhotoSorter {

    @Throws(DirectoryException::class, DummyException::class)
    fun sortFiles(mode: SortMode, source: String, destination: String, log: JTextArea) {
        fun writeLog(message: String) = log.append("$message\n")
        writeLog("Sorting started")

        checkFolder(source, FolderType.SOURCE)
        if (mode != SortMode.REPLACE)
            checkFolder(destination, FolderType.DESTINATION)

        if (mode != SortMode.REPLACE)
            Files.walk(Paths.get(source))
                .filter { it.isDirectory() }
                .map { Paths.get(it.toString().replace(source, destination)) }
                .forEach { Files.createDirectory(it) }

        Files.walk(Paths.get(source))
            .filter { it.isDirectory().not() && it.isHidden().not() }
            .map { File(it, mode) }
            .toList().toSet()
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
                    if (Files.exists(this))
                        if (overrideDestinationDialog() == 0)
                            deleteFolder(this)
                        else
                            throw DummyException()
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