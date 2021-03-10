package vn.icheck.android.loyalty.base.network

data class BaseModel<T> (val listData: MutableList<T>?, val data: T?, val type: Int)