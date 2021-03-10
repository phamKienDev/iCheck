package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICStoreiCheck {
    @Expose
    var id: Long = 0

    @Expose
    var barcode: String? = null

    @Expose
    var media: MutableList<ICMedia>? = null

    @Expose
    var imageUrl: String? = null

    @Expose
    var originId: Long? = null

    @Expose
    var price: Long? = null

    @Expose
    var name: String? = null

    @Expose
    var totalAvailable: Int = 0

    @Expose
    var icheckShop: ICProvider? = null

    @Expose
    val orderItem: ICOrderItem? = null

    var addToCart = false
}

class ICProvider {
    @Expose
    var totalCount: Long = 0

    @Expose
    var availableCount: Long = 0

    @Expose
    var icoin: Long = 0

    @Expose
    var partnerCode: String? = null

    @Expose
    var slogan: String? = null

    @Expose
    var logo: String? = null

    @Expose
    var description: String? = null
}