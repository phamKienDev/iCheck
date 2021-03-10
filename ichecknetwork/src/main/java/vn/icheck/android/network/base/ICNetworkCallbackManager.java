package vn.icheck.android.network.base;

import okhttp3.Interceptor;

public interface ICNetworkCallbackManager {


    void onRequiredLogin(int code, Interceptor.Chain chain);

    void onLoginSuccess(int code);

    void onNetworkError(Throwable throwable);

    /**
     * The factory class for the {@link ICNetworkCallbackManager}.
     */
    class Factory {
        /**
         * Creates an instance of {@link ICNetworkCallbackManager}.
         *
         * @return an instance of {@link ICNetworkCallbackManager}.
         */
        public static ICNetworkCallbackManager create() {
            return new CallbackManagerImpl();
        }
    }
}
