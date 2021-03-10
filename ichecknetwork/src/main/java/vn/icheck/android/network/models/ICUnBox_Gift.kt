package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICUnBox_Gift {
    @Expose
    var index: Int? = null

    // 0 - Chuc ban may man , 1 - vat pham , 2 - voucher , 3 - point
    @Expose
    var type: Int? = null

    @Expose
    var icoin: Int? = null

    @Expose
    var image: String? = null

    @Expose
    var name: String? = null
}