package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose

data class ICKListResponse<T>(
        @Expose val count: Int = 0,
        @Expose val rows: MutableList<T> = mutableListOf()
) : ICKBaseResponse()