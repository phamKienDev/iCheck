package vn.icheck.android.loyalty.network;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;

public final class CallbackManagerImpl implements ICNetworkCallbackManager {
    private static final String TAG = CallbackManagerImpl.class.getSimpleName();
    private static Map<Integer, Callback> staticCallbacks = new HashMap<>();

    /**
     * If there is no explicit callback, but we still need to call the Facebook component,
     * because it's going to update some state, e.g., LOGIN, like. Then we should REGISTER a
     * static callback that can still handle the response.
     *
     * @param code     The request code.
     * @param callback The callback for the feature.
     */
    public synchronized static void registerStaticCallback(
            int code,
            Callback callback) {
        if (staticCallbacks.containsKey(code)) {
            return;
        }
        staticCallbacks.put(code, callback);
    }

    private static synchronized Callback getStaticCallback(Integer code) {
        return staticCallbacks.get(code);
    }

    private static void runStaticRequiredLoginCallback(
            int code, Interceptor.Chain chain
    ) {
        Callback callback = getStaticCallback(code);
        if (callback != null) {
            callback.onRequiredLogin(code, chain);
        }
    }

    private static void runStaticLoginSuccessCallback(
            int code
    ) {
        Callback callback = getStaticCallback(code);
        if (callback != null) {
            callback.onLoginSuccess(code);
        }
    }

    private static void runStaticNetworkErrorCallback(
            Throwable throwable
    ) {
        Callback callback = getStaticCallback(111);
        if (callback != null) {
            callback.onNetworkError(throwable);
        }
    }

    private Map<Integer, Callback> callbacks = new HashMap<>();

    public void registerCallback(int code, Callback callback) {
        callbacks.put(code, callback);
    }

    public void unregisterCallback(int requestCode) {
        callbacks.remove(requestCode);
    }

    @Override
    public void onRequiredLogin(int code, Interceptor.Chain chain) {
        Callback callback = callbacks.get(code);
        if (callback != null) {
            callback.onRequiredLogin(code, chain);
        } else runStaticRequiredLoginCallback(code, chain);
    }

    @Override
    public void onLoginSuccess(int code) {
        Callback callback = callbacks.get(code);
        if (callback != null) {
            callback.onLoginSuccess(code);
        } else runStaticLoginSuccessCallback(code);
    }

    @Override
    public void onNetworkError(Throwable throwable) {
        Callback callback = callbacks.get(111);

        if (callback != null) {
            callback.onNetworkError(throwable);
        } else runStaticNetworkErrorCallback(throwable);
    }


    public interface Callback {

        void onRequiredLogin(int code, Interceptor.Chain chain);

        void onLoginSuccess(int code);

        void onNetworkError(Throwable throwable);
    }

    public enum RequestCodeOffset {
        Login(0);
        private final int offset;

        RequestCodeOffset(int offset) {
            this.offset = offset;
        }

        public int toRequestCode() {
            return 111 + offset;
        }
    }
}

