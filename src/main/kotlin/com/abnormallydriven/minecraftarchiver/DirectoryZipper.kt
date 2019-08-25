package com.abnormallydriven.minecraftarchiver

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DirectoryZipper(private val dateFormatter: DateTimeFormatter) {

    fun zip(directory: File): File {
        val formattedDateString = LocalDateTime.now().format(dateFormatter)
        val zipFile = File("/tmp", formattedDateString)

        ZipOutputStream(zipFile.outputStream().buffered()).use {
            recursiveZip(it, directory, zipFile.name + "_backup")
            it.closeEntry()
        }

        zipFile.deleteOnExit()
        return zipFile
    }

    private fun recursiveZip(zipOutputStream: ZipOutputStream, currentFile: File, parentName: String) {
        val fileList = currentFile.listFiles() ?: emptyArray()

        for (file in fileList) {
            if (file.isDirectory) {
                zipDirectory(parentName, file, zipOutputStream)
            } else {
                zipFile(file, parentName, zipOutputStream)
            }
        }
    }

    private fun zipDirectory(parentName: String, file: File, zipOutputStream: ZipOutputStream) {
        val entry = ZipEntry(parentName + File.separator + file.name + File.separator)

        entry.time = file.lastModified()
        entry.isDirectory
        entry.size = file.length()

        zipOutputStream.putNextEntry(entry)

        recursiveZip(zipOutputStream, file, parentName + File.separator + file.name)
    }

    private fun zipFile(file: File, parentName: String, zipOutputStream: ZipOutputStream) {
        file.inputStream().buffered().use { bufferedInputStream ->
            val path = parentName + File.separator + file.name
            val entry = ZipEntry(path)

            entry.time = file.lastModified()
            entry.isDirectory
            entry.size = file.length()

            zipOutputStream.putNextEntry(entry)
            bufferedInputStream.copyTo(zipOutputStream)
        }
    }
}