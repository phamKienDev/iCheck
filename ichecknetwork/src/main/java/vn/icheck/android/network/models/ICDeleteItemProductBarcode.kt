package vn.icheck.android.network.models


import com.google.gson.annotations.Expose

data class ICDeleteItemProductBarcode(
    @Expose
    var id: Int? = null,
    @Expose
    var product_id: Int? = null,
    @Expose
    var user_id: String? = null,
    @Expose
    var device_id: String? = null,
    @Expose
    var scans: Int? = null,
    @Expose
    var price: Int? = null,
    @Expose
    var scan_lat: Double? = null,
    @Expose
    var scan_lng: Double? = null,
    @Expose
    var scan_address: String? = null,
    @Expose
    var created_at: String? = null,
    @Expose
    var updated_at: String? = null
)