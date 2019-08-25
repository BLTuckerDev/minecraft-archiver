package com.abnormallydriven.minecraftarchiver

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import java.io.File
import java.nio.ByteBuffer

class ZipFileUploader(private val bucket: Bucket,
                      private val storageService: Storage) {

    fun upload(zipFile: File){
        val archiveId = BlobId.of(bucket.name, zipFile.name)
        val blobInfo = BlobInfo.newBuilder(archiveId)
            .setContentType("application/zip")
            .build()


        storageService.writer(blobInfo).use { serviceWriter ->
            zipFile.inputStream().use { fileStream ->
                do{
                    val readBuffer = ByteArray(4096)
                    val bytesRead = fileStream.read(readBuffer)
                    serviceWriter.write(ByteBuffer.wrap(readBuffer))
                }while(bytesRead != -1)
            }
        }
    }
}