package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICRespOrder(
        @Expose val id: Long,
        @Expose val shop_id: Long,
        @Expose val shop: ICShop,
        @Expose val items: MutableList<ICItemCart>
)