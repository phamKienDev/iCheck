package vn.icheck.android.loyalty.network

import okhttp3.Interceptor

interface ICNetworkCallbackManager {

    fun onRequiredLogin(code: Int, chain: Interceptor.Chain?)

    fun onLoginSuccess(code: Int)

    fun onNetworkError(throwable: Throwable?)

    object Factory {
        /**
         * Creates an instance of [ICNetworkCallbackManager].
         *
         * @return an instance of [ICNetworkCallbackManager].
         */
        fun create(): ICNetworkCallbackManager {
            return CallbackManagerImpl()
        }
    }
}