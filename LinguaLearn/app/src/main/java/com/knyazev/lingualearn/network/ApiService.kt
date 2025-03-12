package com.knyazev.lingualearn.network

import com.knyazev.lingualearn.model.Word
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import com.knyazev.lingualearn.model.Quiz

private const val BASE_URL = "file:///android_asset/"

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface ApiService {
    @GET("words.json")
    suspend fun getWords(): List<Word>

    @GET("quizzes.json")
    suspend fun getQuizzes(): List<Quiz>
}

object LinguaApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}