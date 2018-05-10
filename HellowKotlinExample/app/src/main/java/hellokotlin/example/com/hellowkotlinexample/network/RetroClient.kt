package hellokotlin.example.com.hellowkotlinexample.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroClient {

    private val BASE_URL = "http://your_path/v1/"

    /**
     * Get Retrofit Instance
     */
    private fun getRetrofitInstance(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    fun getApiService(okHttpClient: OkHttpClient): ApiService {
        return getRetrofitInstance(okHttpClient).create(ApiService::class.java)
    }

}
