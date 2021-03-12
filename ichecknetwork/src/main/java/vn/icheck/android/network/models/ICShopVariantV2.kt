package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameCity
import java.io.Serializable

data class ICShopVariantV2(
        @Expose val sales : Long? = null,
        @Expose val cover : String? = null,
        @Expose val title : String? = null,
        @Expose val id : Long? = null,
        @Expose val address : String? = null,
        @Expose val verified : Boolean? = null,
        @Expose val avatar : String? = null,
        @Expose val phone : String? = null,
        @Expose val name : String? = null,
        @Expose val location : ICLocation? = null,
        @Expose val status : Int? = null,
        @Expose val minOrderValue : Long? = null,
        @Expose val isOffline : Boolean? = null,
        @Expose val pageId : Long? = null,
        @Expose val isOnline : Boolean? = null,
        @Expose val referenceId : Long? = null,
        @Expose val distance : Long? = null,
        @Expose val city : ICDistrict? = null,
        @Expose val district : ICDistrict? = null,
        @Expose val rating : Float? = null,
        @Expose val price : Long? = null,
        @Expose val specialPrice : Long? = null,
        @Expose val saleOff : Boolean? = null
) : Serializable
