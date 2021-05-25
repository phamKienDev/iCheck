package vn.icheck.android.di

//import okhttp3.logging.HttpLoggingInterceptor
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.*
//import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.ICK_TOKEN
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.api.UploadApi
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.APIConstants.socialHost
import vn.icheck.android.network.base.ICNetworkManager.onEndOfToken
import vn.icheck.android.network.base.ICNetworkManager.onTokenTimeout
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.util.DeviceUtils
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

@InstallIn(ApplicationComponent::class)
@Module
object NetworkModule {

    @Provides
    fun provideOkHttp(sharedPreferences: SharedPreferences):OkHttpClient{
//        val httpLoggingInterceptor = HttpLoggingInterceptor()
        return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .authenticator(object : Authenticator {
                    override fun authenticate(route: Route?, response: Response): Request? {
                        if (!response.request.url.toUrl().toString().contains("/ads") &&
                                !response.request.url.toUrl().toString().contains("system-setting")
                                && !response.request.url.toUrl().toString().contains("relationships/information")) {
                            if (response.peekBody(1000).string().contains("U102")) {
                                onTokenTimeout()
                            } else {
                                onEndOfToken()
                            }
                        }
                        return null
                    }
                })
                .addInterceptor{ chain ->
                    try {
                        val request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer ${sharedPreferences.getString(ICK_TOKEN, "")}")
                                .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                                .addHeader("User-Agent", "Model:${DeviceUtils.getModel()}  +  AppVersion:${SettingManager.appVersion}")
                                .addHeader("appVersion", SettingManager.appVersion)

                                .build()
                        val hasMultipart = request.headers.names().contains("multipart")
//                        if (hasMultipart) httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE) else httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                        chain.proceed(request)
                    } catch (e: SocketTimeoutException) {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SOCKET_TIMEOUT))
                        val request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer ${sharedPreferences.getString(ICK_TOKEN, "")}")
                                .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                                .addHeader("appVersion", SettingManager.appVersion)
                                .addHeader("User-Agent", "Model:${DeviceUtils.getModel()}  +  AppVersion:${SettingManager.appVersion}")
                                .build()
                        val hasMultipart = request.headers.names().contains("multipart")
//                        if (hasMultipart) httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE) else httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                        chain.proceed(request)
                    }
                }
//                .addInterceptor(httpLoggingInterceptor)
                .build()
    }

    @Provides
    fun provideIckApi(okHttpClient: OkHttpClient): ICKApi {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(socialHost)
                .build()
                .create(ICKApi::class.java)
    }

    @Provides
    fun provideUploadApi(okHttpClient: OkHttpClient):UploadApi{
        return Retrofit.Builder()
                .baseUrl(APIConstants.uploadFileHost())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadApi::class.java)
    }

}