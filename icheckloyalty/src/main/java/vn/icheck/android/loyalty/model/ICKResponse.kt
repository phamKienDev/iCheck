package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose

class ICKResponse<T>(
        @Expose val statusCode: Int?,
        @Expose var data: T?,
        @Expose var status: String?
)