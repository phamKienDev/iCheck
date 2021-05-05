package vn.icheck.android.chat.icheckchat.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DEVICE_ID
import vn.icheck.android.chat.icheckchat.base.ConstantChat.SOCIAL_HOST
import vn.icheck.android.chat.icheckchat.base.ConstantChat.TOKEN
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import java.io.IOException
import java.util.concurrent.TimeUnit

object MCNetworkClient {

    private val gson = GsonBuilder().setLenient().create()

    private var timeRequest = 20L

    @Volatile
    private var newSocialApi: MCNetworkApi? = null

    @Volatile
    private var client = OkHttpClient.Builder()
            .connectTimeout(timeRequest, TimeUnit.SECONDS)
            .readTimeout(timeRequest, TimeUnit.SECONDS)
            .writeTimeout(timeRequest, TimeUnit.SECONDS)
            .addInterceptor { chain: Interceptor.Chain -> requireLoginCallback(chain) }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    fun getNewSocialApi(timeRequest: Long? = null): MCNetworkApi {
        if (timeRequest != null) {
            this.timeRequest = timeRequest
        }

        if (newSocialApi == null) {
            synchronized(MCNetworkApi::class.java) {
                newSocialApi = Retrofit.Builder()
                        .baseUrl(SOCIAL_HOST)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(MCNetworkApi::class.java)
            }
        }
        return newSocialApi!!
    }

    @Throws(IOException::class)
    private fun requireLoginCallback(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        builder.addHeader("Content-Type", "application/json")

                .addHeader("Authorization", "Bearer ${ShareHelperChat.getString(TOKEN)}")
                .addHeader("device-id", "${ShareHelperChat.getString(DEVICE_ID)}")

        builder.method(original.method, original.body)

        return chain.proceed(builder.build())
    }
}