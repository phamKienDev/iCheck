package vn.icheck.android.network.models.product_detail

import com.google.gson.annotations.Expose

class ICLayoutItem {
    @Expose val id: String? = null
    @Expose val key: String? = null
    @Expose val request: ICRequest? = null
    @Expose var dataWidget: Any? = null
}