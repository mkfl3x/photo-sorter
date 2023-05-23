package org.mkfl3x.photosorter.app

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