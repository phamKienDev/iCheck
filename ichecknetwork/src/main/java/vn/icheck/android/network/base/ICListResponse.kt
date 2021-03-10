package vn.icheck.android.network.base

import com.google.gson.annotations.Expose

data class ICListResponse<T>(
        @Expose var count: Int = 0,
        @Expose var rows: MutableList<T> = mutableListOf()
) : ICResponseCode()