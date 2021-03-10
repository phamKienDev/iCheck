package vn.icheck.android.network.base

interface ICTokenListener<T> : ICApiListener<T> {

    fun onRefreshTokenSuccess()
    fun onRefreshTokenError()
}