@file:JvmName("App")

package com.abnormallydriven.minecraftarchiver

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.StorageOptions
import java.io.File
import java.time.format.DateTimeFormatter


fun main(args: Array<String>) {
    if(args.size < 4){
        System.err.println("Invalid program arguments. Please provide a project id, google service account file, bucket name," +
                " and folder location. java -jar minecraft-archiver.jar <project id> <account service file> <bucket name> <folder location>")
        return
    }

    val projectId = args[0]
    val googleServiceAccountJsonFile = args[1]
    val bucketName = args[2]
    val minecraftFolderLocation = args[3]

    val credentialFile = File(googleServiceAccountJsonFile)
    val googleCredentials = credentialFile.inputStream().use {
        ServiceAccountCredentials.fromStream(it)
    }


    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss-SSS")
    val directoryZipper = DirectoryZipper(dateFormatter)
    val folderToBackUp = File(minecraftFolderLocation)

    val zipFile = directoryZipper.zip(folderToBackUp)


    val storageOptionsBuiler = StorageOptions.newBuilder()

    storageOptionsBuiler.setProjectId(projectId)
    storageOptionsBuiler.setCredentials(googleCredentials)

    val storageOptions: StorageOptions = storageOptionsBuiler.build()
    val storageService = storageOptions.service
    val minecraftBackupBucket = storageService.get(bucketName)
    val uploader = ZipFileUploader(minecraftBackupBucket, storageService)

    uploader.upload(zipFile)
}
