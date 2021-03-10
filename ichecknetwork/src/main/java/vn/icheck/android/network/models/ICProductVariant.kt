package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICProductVariant {
    @Expose
    var id: Long = 0

    @Expose
    var name: String? = null

    @Expose
    var image: String? = null

    @Expose
    var variant: String? = null

    @Expose
    var sale_off: Boolean = false

    @Expose
    var special_price: Long = 0

    @Expose
    var price: Long = 0

    @Expose
    var quantity: Int = 0

    @Expose
    var name_shop: String? = null

    @Expose
    var image_shop: String? = null
}