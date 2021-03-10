package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICProductTrend {
    @Expose
    var id: Long = 0

    @Expose
    var name: String? = null

    @Expose
    var barcode: String? = null

    @Expose
    var price: Long? = null

    @Expose
    var rating: Float = 0F

    @Expose
    var media: List<ICMedia>? = null

    @Expose
    var owner: ICOwner? = null

    @Expose
    var verified: Boolean = false

    @Expose
    var reviewCount: Int = 0

    @Expose
    var userId: String? = null

    @Expose
    var productId: String? = null

    @Expose
    var product: ICProductV2? = null
}

class ICProductV2 {
    @Expose
    var id: Long = 0

    @Expose
    var barcode: String? = null

    @Expose
    var name: String? = null

    @Expose
    var media: MutableList<ICMedia>? = null

    @Expose
    var status: String? = null

    @Expose
    var state: String? = null

    @Expose
    var verified: Boolean = false

    @Expose
    var rating: Float = 0F

    @Expose
    var owner: ICPage? = null

    @Expose
    var price: Double = 0.0
    @Expose
    var reviewCount: Int = 0
}