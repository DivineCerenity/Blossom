package com.jonathon.blossom.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DriveApiService {
    @POST("files")
    suspend fun createFile(
        @Header("Authorization") auth: String,
        @Body fileMetadata: FileMetadata
    ): Response<FileResponse>

    @Multipart
    @POST("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
    suspend fun uploadFile(
        @Header("Authorization") auth: String,
        @Part metadata: MultipartBody.Part,
        @Part media: MultipartBody.Part
    ): Response<FileResponse>

    @GET("files")
    suspend fun listFiles(
        @Header("Authorization") auth: String,
        @Query("q") query: String
    ): Response<FileListResponse>

    @GET("files/{fileId}")
    suspend fun getFile(
        @Header("Authorization") auth: String,
        @Path("fileId") fileId: String,
        @Query("alt") alt: String = "media"
    ): Response<ResponseBody>
}

data class FileMetadata(
    val name: String,
    val mimeType: String,
    val parents: List<String>? = null
)

data class FileResponse(
    val id: String,
    val name: String,
    val mimeType: String
)

data class FileListResponse(
    val files: List<FileResponse>
)
