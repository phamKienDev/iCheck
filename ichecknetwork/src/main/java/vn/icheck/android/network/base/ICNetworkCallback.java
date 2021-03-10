package vn.icheck.android.network.base;

public interface ICNetworkCallback {
    void forceRequiredLogin();

    void refreshToken();

    void onNetworkError(Throwable throwable);
}
