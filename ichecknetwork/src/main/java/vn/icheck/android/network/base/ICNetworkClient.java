package vn.icheck.android.network.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.jvm.Volatile;
import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
//import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.icheck.android.ichecklibs.event.ICMessageEvent;
import vn.icheck.android.network.BuildConfig;
import vn.icheck.android.network.models.ICSessionData;
import vn.icheck.android.network.util.DeviceUtils;

public class ICNetworkClient {
    public static ICNetworkCallbackManager networkCallbackManager = ICNetworkCallbackManager.Factory.create();


    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .authenticator(new Authenticator(){
                @Nullable
                @Override
                public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                    if (!response.request().url().url().toString().contains("/ads")) {
                        ICNetworkManager.INSTANCE.onEndOfToken();
                    }
                    return null;
                }
            })
//            .authenticator(new TokenAuthenticator())
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(ICNetworkClient::requireLoginCallback).build();

    private static OkHttpClient getClient(int timeRequest) {
        return new OkHttpClient.Builder()
                .connectTimeout(timeRequest, TimeUnit.SECONDS)
                .readTimeout(timeRequest, TimeUnit.SECONDS)
                .writeTimeout(timeRequest, TimeUnit.SECONDS)
                .authenticator(new Authenticator(){
                    @Nullable
                    @Override
                    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                        if (!response.request().url().url().toString().contains("/ads")) {
                            ICNetworkManager.INSTANCE.onEndOfToken();
                        }
                        return null;
                    }
                })
//            .authenticator(new TokenAuthenticator())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(ICNetworkClient::requireLoginCallback).build();
    }

    private static final OkHttpClient client2 = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .authenticator(new Authenticator(){
                @Nullable
                @Override
                public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                    ICNetworkManager.INSTANCE.onEndOfToken();
                    return null;
                }
            })
//            .authenticator(new TokenAuthenticator())
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(ICNetworkClient::requireLoginCallback2).build();

    private static final OkHttpClient client3 = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .authenticator(new Authenticator(){
                @Nullable
                @Override
                public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                    ICNetworkManager.INSTANCE.onEndOfToken();
                    return null;
                }
            })
//            .authenticator(new TokenAuthenticator())
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(ICNetworkClient::requireLoginCallback3).build();

    private static final OkHttpClient client4 = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .authenticator(new Authenticator(){
                @Nullable
                @Override
                public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                    ICNetworkManager.INSTANCE.onEndOfToken();
                    return null;
                }
            })
//            .authenticator(new TokenAuthenticator())
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(ICNetworkClient::requireLoginCallback4).build();

    private static final OkHttpClient clientStamp = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .authenticator(new Authenticator(){
                @Nullable
                @Override
                public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                    ICNetworkManager.INSTANCE.onEndOfToken();
                    return null;
                }
            })
//            .authenticator(new TokenAuthenticator())
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(ICNetworkClient::requireLoginCallbackStamp).build();

    private static final OkHttpClient uploadClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .authenticator(new Authenticator(){
                @Nullable
                @Override
                public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                    ICNetworkManager.INSTANCE.onEndOfToken();
                    return null;
                }
            })
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(ICNetworkClient::requireLoginCallback).build();


    private static OkHttpClient clientNoHeader() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS)
                .authenticator(new Authenticator(){
                    @Nullable
                    @Override
                    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                        ICNetworkManager.INSTANCE.onEndOfToken();
                        return null;
                    }
                })
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private static volatile ICNetworkSocialApi newSocialApi = null;
    private static volatile ICNetworkAPI socialApi = null;
    private static volatile OkHttpClient chatClient = null;
    private static volatile ICNetworkAPI chatApi = null;
    private static volatile ICNetworkAPI socialStampApi = null;

    private static volatile ICNetworkAPI icheckApi = null;
    private static volatile ICNetworkAPI icheckApi2 = null;

    private static volatile ICNetworkAPI simpleIcheckApi = null;

    private static volatile ICNetworkAPI stampApi = null;
    private static volatile ICNetworkAPI stampApiHeaderThree = null;
    private static volatile ICNetworkAPI stampApi2 = null;
    private static volatile ICNetworkAPI stampApiV6 = null;

    private static volatile ICNetworkAPI iCheckLoyaltyApi;

    public static ICNetworkAPI getApiClientLoyalty() {
        if (iCheckLoyaltyApi == null) {
            synchronized (ICNetworkAPI.class) {
                String host;
                if (BuildConfig.FLAVOR.contentEquals("dev"))
                    host = "https://api.dev.icheck.vn/api/business/";
                else host = "https://api-social.icheck.com.vn/api/business/";
                iCheckLoyaltyApi = new Retrofit.Builder()
                        .baseUrl(host)
                        .client(new OkHttpClient.Builder()
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS)
//                                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                                .addInterceptor(ICNetworkClient::requireLoginCallback).build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI.class);
            }
        }
        return iCheckLoyaltyApi;
    }

    private static String deviceModel() {
        String model = DeviceUtils.getModel();
        if (model.contains("²")) {
            return model.replace("²", "2");
        } else {
            return model;
        }
    }

    @NotNull
    private static Response requireLoginCallback(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        builder.addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Model: " + deviceModel() + " + AppVersion:" + SettingManager.INSTANCE.getAppVersion())
                .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion());

        if (SessionManager.INSTANCE.isUserLogged() || SessionManager.INSTANCE.isDeviceLogged()) {
            ICSessionData sessionData = SessionManager.INSTANCE.getSession();

            String sessionId = SettingManager.INSTANCE.getGetSessionPvcombank();

            if (!sessionId.isEmpty()) {
                builder.addHeader("Cookie", sessionId);
            }

            builder.addHeader("Authorization", ("bearer " + sessionData.getToken()));
        }

        if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
            builder.addHeader("lat", "" + APIConstants.LATITUDE);
            builder.addHeader("lon", "" + APIConstants.LONGITUDE);
        }
        builder.method(original.method(), original.body());
        Request build = builder.build();
        return chain.proceed(build);
    }

    @NotNull
    private static Response requireLoginCallbackStamp(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        ICSessionData sessionData = SessionManager.INSTANCE.getSession();

        builder.addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Model: " + deviceModel() + " + AppVersion:" + SettingManager.INSTANCE.getAppVersion())
                .addHeader("device_id", DeviceUtils.getUniqueDeviceId())
                .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion());

        if (sessionData.getUser() != null && sessionData.getUser().getId() != 0) {
            builder.addHeader("icheck-id", "i-" + sessionData.getUser().getId())
                    .addHeader("icheck_id", "i-" + sessionData.getUser().getId());
        }

        if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
            builder.addHeader("lat", "" + APIConstants.LATITUDE)
                    .addHeader("lon", "" + APIConstants.LONGITUDE);
        }

        builder.method(original.method(), original.body());

        return chain.proceed(builder.build());
    }

    @NotNull
    private static Response requireLoginCallback2(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        builder.addHeader("Content-Type", "application/json")
                .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion());

        if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 00.0) {
            builder.addHeader("lat", "" + APIConstants.LATITUDE);
            builder.addHeader("lon", "" + APIConstants.LONGITUDE);
        }

        builder.method(original.method(), original.body());

//        return onIntercept(chain, builder.build());
        return chain.proceed(builder.build());
    }

    @NotNull
    private static Response requireLoginCallback3(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        builder.addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Model: " + deviceModel() + " + AppVersion:" + SettingManager.INSTANCE.getAppVersion())
                .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion());

        if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
            builder.addHeader("lat", "" + APIConstants.LATITUDE);
            builder.addHeader("lon", "" + APIConstants.LONGITUDE);
        }

        builder.method(original.method(), original.body());

//        return onIntercept(chain, builder.build());
        return chain.proceed(builder.build());
    }

    @NotNull
    private static Response requireLoginCallback4(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        builder.addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Model: " + deviceModel() + " + AppVersion:" + SettingManager.INSTANCE.getAppVersion())
                .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion());

        if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
            builder.addHeader("lat", "" + APIConstants.LATITUDE);
            builder.addHeader("lon", "" + APIConstants.LONGITUDE);
        }

        builder.method(original.method(), original.body());

//        return onIntercept(chain, builder.build());
        return chain.proceed(builder.build());
    }

    private static OkHttpClient client() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .authenticator(new Authenticator(){
                    @Nullable
                    @Override
                    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                        ICNetworkManager.INSTANCE.onEndOfToken();
                        return null;
                    }
                })
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .authenticator(new TokenAuthenticator())
//                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(ICNetworkClient::requireLoginCallback)
                .build();
    }

    public static ICNetworkAPI getSimpleApiClient() {
        if (simpleIcheckApi == null) {
            simpleIcheckApi = new Retrofit.Builder()
                    .client(client())
                    .baseUrl(APIConstants.INSTANCE.getDefaultHost())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(ICNetworkAPI.class);
        }

        return simpleIcheckApi;
    }

    public static ICNetworkSocialApi getNewSocialApi() {
        if (newSocialApi == null) {
            synchronized (ICNetworkSocialApi.class) {
                newSocialApi = new Retrofit.Builder()
                        .baseUrl(APIConstants.INSTANCE.getSocialHost())
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(ICNetworkSocialApi.class);
            }
        }
        return newSocialApi;
    }

    public static ICNetworkAPI getSocialApi() {
        if (socialApi == null) {
            synchronized (ICNetworkAPI.class) {
                socialApi = new Retrofit.Builder()
                        .baseUrl(APIConstants.INSTANCE.getSocialHost())
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI.class);
            }
        }
        return socialApi;
    }


    public static ICNetworkAPI getApiClient() {
        if (icheckApi == null) {
            synchronized (ICNetworkAPI.class) {
                icheckApi = new Retrofit.Builder()
                        .baseUrl(APIConstants.INSTANCE.getDefaultHost())
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI.class);
            }
        }
        return icheckApi;
    }

    public static ICNetworkAPI getApiClient(int timeRequest) {
        return new Retrofit.Builder()
                .baseUrl(APIConstants.INSTANCE.getDefaultHost())
                .client(getClient(timeRequest))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ICNetworkAPI.class);
    }

    public static ICNetworkAPI getApiClientNoHeaderWithRx() {
        if (icheckApi2 == null) {
            icheckApi2 = new Retrofit.Builder()
                    .baseUrl(APIConstants.INSTANCE.getDefaultHost())
                    .client(client3)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(ICNetworkAPI.class);
        }

        return icheckApi2;
    }

    public static ICNetworkAPI getApiClientNoHeader() {
        if (icheckApi2 == null) {
            icheckApi2 = new Retrofit.Builder()
                    .baseUrl(APIConstants.INSTANCE.getDefaultHost())
                    .client(clientNoHeader())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(ICNetworkAPI.class);
        }
        return icheckApi2;
    }

    public static ICNetworkAPI getShareClient() {
        return new Retrofit.Builder()
                .baseUrl("https://icheck.com.vn/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build()
                .create(ICNetworkAPI.class);
    }

    public static ICNetworkAPI getNewUploadClientV1() {
        return new Retrofit.Builder()
                .baseUrl(APIConstants.INSTANCE.uploadFileHostV1())
                .client(uploadClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ICNetworkAPI.class);
    }

    public static ICNetworkAPI getNewUploadClient() {

        return new Retrofit.Builder()
                .baseUrl(APIConstants.INSTANCE.uploadFileHost())
                .client(uploadClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ICNetworkAPI.class);
    }

    public static ICNetworkAPI getStampClientSocial() {
        if (socialStampApi == null) {
            synchronized (ICNetworkAPI.class) {
                socialStampApi = new Retrofit.Builder()
                        .baseUrl(APIConstants.INSTANCE.getDETAIL_STAMP_HOST())
                        .client(clientStamp)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI.class);
            }
        }
        return socialStampApi;
    }

    public static ICNetworkAPI getStampClient() {
        if (stampApi == null) {
            synchronized (ICNetworkAPI.class) {
                stampApi = new Retrofit.Builder()
                        .baseUrl(APIConstants.INSTANCE.getDETAIL_STAMP_HOST())
                        .client(client2)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI.class);
            }
        }
        return stampApi;
    }

    public static ICNetworkAPI getSimpleStampClient() {
        if (stampApiHeaderThree == null) {
            synchronized (ICNetworkAPI.class) {
                stampApiHeaderThree = new Retrofit.Builder()
                        .baseUrl(APIConstants.INSTANCE.getDETAIL_STAMP_HOST())
                        .client(client4)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI.class);
            }
        }
        return stampApiHeaderThree;
    }

    public static ICNetworkAPI getStampClient2() {
        if (stampApi2 == null) {
            stampApi2 = new Retrofit.Builder()
                    .client(clientNoHeader())
                    .baseUrl(APIConstants.INSTANCE.getDETAIL_STAMP_HOST())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(ICNetworkAPI.class);
        }
        return stampApi2;
    }

    public static ICNetworkAPI getStampClientV6() {
        if (stampApiV6 == null) {
            synchronized (ICNetworkAPI.class) {
                stampApiV6 = new Retrofit.Builder()
                        .baseUrl(APIConstants.INSTANCE.detailStampV6Host())
                        .client(client2)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI.class);
            }
        }
        return stampApiV6;
    }

    public static ICNetworkAPI getSimpleChat() {
        if (chatClient == null && SessionManager.INSTANCE.getSession().getFirebaseToken() != null) {
            chatClient = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + SessionManager.INSTANCE.getSession().getFirebaseToken())
                        .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                        .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion())
                        .build();
                return chain.proceed(newRequest);
            })
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
//                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();
        } else {
            chatClient = client;
        }
        return new Retrofit.Builder()
                .baseUrl("https://chat.icheck.com.vn/")
                .client(chatClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ICNetworkAPI.class);
    }

    public static ICNetworkAPI getChatClient() {
        if (chatApi == null) {
            synchronized (ICNetworkAPI.class) {
                if (chatClient == null && SessionManager.INSTANCE.getSession().getFirebaseToken() != null) {
                    chatClient = new OkHttpClient.Builder().addInterceptor(chain -> {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + SessionManager.INSTANCE.getSession().getFirebaseToken())
                                .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                                .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion())
                                .build();
                        return chain.proceed(newRequest);
                    })
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
//                            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                            .build();
                } else {
                    throw new NullPointerException("Firebase token not found");
                }

                chatApi = new Retrofit.Builder()
                        .baseUrl("https://chat.icheck.com.vn/")
                        .client(chatClient)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                        .build()
                        .create(ICNetworkAPI.class);
            }
        }
        return chatApi;
    }

    static class TokenAuthenticator implements Authenticator {

        @Override
        public Request authenticate(Route route, @NotNull Response response) {
            final Request.Builder[] builder = new Request.Builder[1];

            String refreshToken = SessionManager.INSTANCE.getSession().getRefreshToken();

            if (refreshToken != null && refreshToken.length() > 0) {
                HashMap<String, Object> body = new HashMap<>();
                body.put("refresh_token", refreshToken);

                getApiClient().refreshToken(body)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<ICSessionData>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {
                            }

                            @Override
                            public void onNext(ICSessionData icSessionData) {
                                if (icSessionData.getToken() != null && !icSessionData.getToken().isEmpty() && icSessionData.getTokenType() != null && !icSessionData.getTokenType().isEmpty()) {
                                    SessionManager.INSTANCE.setSession(icSessionData);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                                ICSessionData sessionData = SessionManager.INSTANCE.getSession();

                                if (sessionData != null && sessionData.getTokenType() != null && sessionData.getToken() != null) {
                                    builder[0] = response.request().newBuilder()
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("User-Agent", deviceModel())
                                            .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                                            .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion())
                                            .header("Authorization", (sessionData.getTokenType() + " " + sessionData.getToken()))
                                            .method(response.request().method(), response.request().body());
                                } else {
                                    builder[0] = response.request().newBuilder()
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("User-Agent", deviceModel())
                                            .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                                            .method(response.request().method(), response.request().body());
                                }
                            }
                        });
            } else {
                builder[0] = response.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("User-Agent", deviceModel())
                        .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                        .addHeader("appVersion", SettingManager.INSTANCE.getAppVersion())
                        .method(response.request().method(), response.request().body());
            }

            if (builder[0] != null) {
                return builder[0].build();
            } else return response.request();
        }
    }
}