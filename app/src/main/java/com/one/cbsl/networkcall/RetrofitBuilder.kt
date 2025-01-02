package com.one.cbsl.networkcall

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitBuilder {
    private const val BASE_URL =
        "https://hrisapi.cbslgroup.in/webmethods/apiwebservice.asmx/"
  /* private const val BASE_URL =
        "https://hrisapi.cbslgroup.in/webmethods/apiwebservice.asmx/"
  */  private const val BMD_COMPLAINT_URL =
        "http://crmapi.cbslprojects.com/webmethods/apiwebservice.asmx/"
    private const val SOLAR_COMPLAINT_URL =
        "https://dms.crconline.in/solarappnew/webmethods/apiwebservice.asmx/"
    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    private val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(40, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build() //Doesn't require the adapter
    }

    private fun getBMDApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BMD_COMPLAINT_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build() //Doesn't require the adapter
    }

    private fun getSolarApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SOLAR_COMPLAINT_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build() //Doesn't require the adapter
    }

    val apiService: RetrofitService = getRetrofit().create(RetrofitService::class.java)
    val bmdApiService: RetrofitService = getBMDApi().create(RetrofitService::class.java)
    val solarApiService: RetrofitService = getSolarApi().create(RetrofitService::class.java)


}