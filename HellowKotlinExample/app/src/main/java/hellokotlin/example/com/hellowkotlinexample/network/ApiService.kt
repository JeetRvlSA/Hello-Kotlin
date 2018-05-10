package hellokotlin.example.com.hellowkotlinexample.network

import hellokotlin.example.com.hellowkotlinexample.model.requests.TestModelRequest
import hellokotlin.example.com.hellowkotlinexample.model.responses.TestModelResponase
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("signup")
    fun signUp(@Body request: TestModelRequest): Call<TestModelResponase>

}