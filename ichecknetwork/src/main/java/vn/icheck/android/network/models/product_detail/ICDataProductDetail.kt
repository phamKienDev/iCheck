package vn.icheck.android.network.models.product_detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICOwner
import vn.icheck.android.network.util.JsonHelper

class ICDataProductDetail {
    @Expose val id: Long? = null
    @Expose var barcode:String? = null
    @Expose val status: String? = null
    @Expose val state: String? = null
    @Expose val manager: ICManager? = null
    @Expose val enableContribution: Boolean? = null
    @Expose val alert : ICAlertProduct? = null
    @Expose val criteria : MutableList<ICCriteriaNew>? = null
    @Expose val owner : ICOwner? = null
    @Expose val basicInfo : ICBasicInforProduct? = null
    @Expose val verified : Boolean? = null
    @Expose val media : List<ICMedia>? = null
    @Expose val categories : List<ICCategory>? = null
    @Expose @SerializedName("unverifiedOwner")
    val unverifiedOwner: ICOwner? = null

    override fun toString(): String {
        return JsonHelper.toJson(this)
    }
}