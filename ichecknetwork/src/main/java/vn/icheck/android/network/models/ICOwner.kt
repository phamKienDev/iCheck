package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class ICOwner(
        @Expose var id: Long? = null,
        @Expose var pageId: Long? = null,
        @Expose var name: String? = null,
        @Expose var avatar: String? = null,
        @Expose var cover: String? = null,
        @Expose val phone: String? = null,
        @Expose val email: String? = null,
        @Expose val address: String? = null,
        @Expose val tax: String? = null,
        @Expose val website: String? = null,
        @Expose val facebook: String? = null,
        @Expose val youtube: String? = null,
        @Expose val fax: String? = null,
        @Expose val verified: Boolean? = null,
        @Expose val title: String? = null,
        @Expose val glnCode: String? = null,
        @Expose val ward: ICWard? = null,
        @Expose val district: ICDistrict? = null,
        @Expose val city: ICProvince? = null,
        var icon: Int = 0,
        var background: Int = 0
) : Serializable