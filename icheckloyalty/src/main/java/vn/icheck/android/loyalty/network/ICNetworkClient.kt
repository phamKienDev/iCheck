package vn.icheck.android.loyalty.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import vn.icheck.android.loyalty.base.network.APIConstants
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

internal object ICNetworkClient {

    private val gson = GsonBuilder().setLenient().create()

    @Volatile
    var iCheckLoyaltyApi: ICNetworkAPI? = null

    fun getApiClientLoyalty(): ICNetworkAPI {
        if (iCheckLoyaltyApi == null) {
            synchronized(ICNetworkAPI::class.java) {
                iCheckLoyaltyApi = Retrofit.Builder()
                        .baseUrl(APIConstants.LOYALTY_HOST)
                        .client(OkHttpClient.Builder()
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS)
                                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                                .addInterceptor { chain -> requireLoginCallback(chain) }.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI::class.java)
            }
        }
        return iCheckLoyaltyApi!!
    }

    @Throws(IOException::class)
    private fun requireLoginCallback(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
        val sessionData = SessionManager.session

        builder.addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", SessionManager.getModel())
                .addHeader("device-id", SessionManager.getUniqueDeviceId()!!)
                .addHeader("Authorization", "Bearer" + " " + sessionData.accessToken)

        builder.method(original.method, original.body)

        return chain.proceed(builder.build())
    }
}