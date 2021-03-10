package vn.icheck.android.network.base

interface ICApiListener<T> {

    fun onSuccess(obj: T)
    fun onError(error: ICBaseResponse?)
}