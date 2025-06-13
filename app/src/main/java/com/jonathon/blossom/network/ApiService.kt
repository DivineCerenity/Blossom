package com.jonathon.blossom.network

import retrofit2.http.GET

interface ApiService {
    @GET("get?format=json&order=random")
    suspend fun getRandomVerse(): ApiResponse
}