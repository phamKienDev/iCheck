package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose

data class MCListResponse<T>(
        @Expose var count: Int = 0,
        @Expose var rows: MutableList<T> = mutableListOf()
) : MCResponseCode()