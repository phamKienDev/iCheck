package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICRespCart(
        @Expose val orders: MutableList<ICRespOrder>,
        @Expose val item_total: Int
)