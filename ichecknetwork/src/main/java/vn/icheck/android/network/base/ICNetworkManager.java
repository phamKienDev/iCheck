package vn.icheck.android.network.base;

import android.content.Intent;

import okhttp3.Interceptor;

public class ICNetworkManager {
    private static volatile ICNetworkManager instance;

    /**
     * Getter for the network manager.
     *
     * @return The network manager.
     */
    public static ICNetworkManager getInstance() {
        if (instance == null) {
            synchronized (ICNetworkManager.class) {
                if (instance == null) {
                    instance = new ICNetworkManager();
                }
            }
        }

        return instance;
    }

    /**
     * Registers a LOGIN callback to the given callback manager.
     *
     * @param callbackManager The callback manager that will encapsulate the callback.
     */
    public void registerCallback(final ICNetworkCallbackManager callbackManager, final ICNetworkCallback networkCallback) {
        ((CallbackManagerImpl) callbackManager).registerCallback(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode(), new CallbackManagerImpl.Callback() {
                    @Override
                    public void onRequiredLogin(int code, Interceptor.Chain chain) {
                        ICNetworkManager.this.onRequiredLogin(code, chain, networkCallback);
                    }

                    @Override
                    public void onLoginSuccess(int code) {
                        ICNetworkManager.this.onLoginSuccess(code);
                    }

                    @Override
                    public void onNetworkError(Throwable throwable) {
                        ICNetworkManager.this.onNetworkError(throwable, networkCallback);
                    }
                }
        );
    }

    /**
     * Unregisters a LOGIN callback to the given callback manager.
     *
     * @param callbackManager The callback manager that will encapsulate the callback.
     */
    public void unregisterCallback(final ICNetworkCallbackManager callbackManager) {
        ((CallbackManagerImpl) callbackManager).unregisterCallback(
                CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode());
    }

    void onRequiredLogin(int code, Interceptor.Chain chain, ICNetworkCallback networkCallback) {
        networkCallback.forceRequiredLogin();

    }

    void onLoginSuccess(int code) {

    }

    void onNetworkError(Throwable throwable, ICNetworkCallback networkCallback) {
        networkCallback.onNetworkError(throwable);
    }
}
