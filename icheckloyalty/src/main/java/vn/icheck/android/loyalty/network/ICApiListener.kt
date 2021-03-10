package vn.icheck.android.loyalty.network

import vn.icheck.android.loyalty.model.ICKBaseResponse

interface ICApiListener<T> {

    fun onSuccess(obj: T)
    fun onError(error: ICKBaseResponse?)
}