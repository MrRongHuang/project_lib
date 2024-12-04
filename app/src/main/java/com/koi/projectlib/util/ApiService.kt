package com.koi.projectlib.util

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @Multipart
    @POST("upload") // 替换为你的上传接口路径
    fun uploadFile(
        @Part file: MultipartBody.Part,  // 上传文件
        @Query("path") query: String   // 查询参数
    ): Call<ResponseBody>


    @Multipart
    @POST("upload") // 替换为你的上传接口路径
    fun uploadFileList(
        @Part files: List<MultipartBody.Part>,  // 上传文件
        @Query("path") query: String   // 查询参数
    ): Call<ResponseBody>
}