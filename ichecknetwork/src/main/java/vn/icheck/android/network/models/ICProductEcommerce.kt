package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICProductCommerce (
    @Expose val avatar: String?,
    @Expose val name: String?,
    @Expose val sellPrice: String?,
    @Expose val finalPrice: String?
)