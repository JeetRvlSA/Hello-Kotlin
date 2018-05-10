package hellokotlin.example.com.hellowkotlinexample.network

import android.content.Context
import android.util.Log

import hellokotlin.example.com.hellowkotlinexample.R
import hellokotlin.example.com.hellowkotlinexample.model.responses.BaseResponse
import hellokotlin.example.com.hellowkotlinexample.model.requests.TestModelRequest
import hellokotlin.example.com.hellowkotlinexample.model.responses.TestModelResponase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RetroManager(private val context: Context) {

    private var mListener: OnAPIListener? = null

    private/*Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        Credentials.basic("aUsername", "aPassword"));*/ val httpClient: OkHttpClient
        get() = OkHttpClient().newBuilder().addInterceptor { chain ->
            val originalRequest = chain.request()

            val builder = originalRequest.newBuilder().header("Authorization", "security_key")

            Log.e("AUTHORIZATION", "key is " + "security_key")

            val newRequest = builder.build()
            chain.proceed(newRequest)
        }.build()

    fun signUp(listener: OnAPIListener, request: TestModelRequest) {

        mListener = listener

        val api = RetroClient.getApiService(httpClient)

        val call = api.signUp(request)

        Log.e("URL data: ", "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<TestModelResponase> {

            override fun onResponse(call: Call<TestModelResponase>, response: Response<TestModelResponase>) {

                if (response.code() == 401) {
                    mListener!!.onForceLogout(context.getString(R.string.lbl_force_logout))
                    return
                }

                if (response.body() != null) {
                    mListener!!.onResponse(ApiConstants.API_SIGN_UP, response.body(), response.code())
                } else {
                    mListener!!.onResponseNull(ApiConstants.API_SIGN_UP, response.code())
                }
            }


            override fun onFailure(call: Call<TestModelResponase>, t: Throwable) {
                mListener!!.onFailure(ApiConstants.API_SIGN_UP, t.toString())
            }
        })

    }

    interface OnAPIListener {
        fun onResponse(apiId: Int, response: BaseResponse?, statusCode: Int)

        fun onResponseNull(apiId: Int, statusCode: Int)

        fun onFailure(apiId: Int, msg: String)

        fun onForceLogout(msg: String)
    }
}
