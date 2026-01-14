package com.one.cbsl.networkcall

import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.one.cbsl.utils.Cbsl
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager
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
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    fun getBaseUrl(): String {
        return if (SessionManager.getInstance().getBoolean(Constants.IsChangeServer)) {
            "https://bmdapi.cbslprojects.com/webmethods/apiwebservice.asmx/"
        } else {
            "https://hrisapi.cbslgroup.in/webmethods/apiwebservice.asmx/"
        }
    }

    // Function to build Retrofit instance with dynamic base URL
    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Function to get the API service dynamically
    fun getApi(): RetrofitService {
        val baseUrl = getBaseUrl()
        if (Constants.isDeveloperModeEnabled(Cbsl.getInstance())) {
            Toast.makeText(Cbsl.getInstance(),"Developer mode is enabled. API calls are blocked.",Toast.LENGTH_SHORT).show()
            throw IllegalStateException("Developer mode is enabled. API calls are blocked.")
        }
        // Call this each time to get the correct base URL
        return getRetrofit(baseUrl).create(RetrofitService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getBaseUrl())
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

    fun cancelAllRequests() {
        okHttpClient.dispatcher.cancelAll()
    }
}