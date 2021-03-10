package vn.icheck.android.network.models.product_detail

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICCountry
import java.io.Serializable

class ICBasicInforProduct : Serializable {
    @Expose
    var name: String? = null
    @Expose
    var barcode: String? = null
    @Expose
    var price: Long? = null
    @Expose
    var type: String? = null
    @Expose
    var country: ICCountry? = null
    @Expose
    var rating: Double? = null
    @Expose
    var scanCount: Int? = null
    @Expose
    var reviewCount: Int? = null
    @Expose
    var questionCount: Int? = null
    @Expose
    var isBookMark : Boolean? = null
    @Expose
    var iHasContribution: Boolean? = null
}