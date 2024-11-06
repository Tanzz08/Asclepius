package com.dicoding.asclepius.data.retrofit

import com.dicoding.asclepius.data.response.NewsHealthResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything?q=cancer%2Bhealth%2Bmedical&sortBy=publishedAt")
    fun getNews(@Query("apikey") apiKey: String): Call<NewsHealthResponse>
}