package com.bassem.breakingnews.api

import com.bassem.newsapp.model.NewsResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {


    @GET("top-headlines")
    fun getBreakingNews(
        @Query("country") country: String,
        @Query("apiKey") key: String
    ): Call<NewsResponse>


    @GET("everything")
    fun searchNews(
        @Query("q") search: String,
        @Query("from") from: String,
        @Query("sortBy") sortby: String,
        @Query("apiKey") key: String
    ): Call<NewsResponse>


    companion object {
        val BASE_URL = "https://newsapi.org/v2/"
        fun create(): NewsApi {
            val retrofit =
                Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
                    BASE_URL
                ).build()
            return retrofit.create(NewsApi::class.java)

        }
    }
}