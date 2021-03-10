package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICReqOrder(
        @Expose val shop_id: Long?,
        @Expose val shipping_method_id: Int?,
        @Expose val items: MutableList<ICReqItem>?,
        @Expose val note: String?
)