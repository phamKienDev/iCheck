package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICCoinHistory(
        @Expose var id: Long? = null,
        @Expose var type: Int = 0,
        @Expose var title: String? = null,
        @Expose var amount: Long = 0,
        @Expose var description: String? = null,
        @Expose var createdAt: String? = null
)
