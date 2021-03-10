package vn.icheck.android.network.base

interface ICNewApiListener<T> {

    fun onSuccess(obj: T)
    fun onError(error: ICResponseCode?)
}