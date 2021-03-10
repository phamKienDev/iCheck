package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICOwner(
        @Expose var id: Long,
        @Expose var pageId: Long?,
        @Expose var name: String,
        @Expose var avatar: String?,
        @Expose var cover: String?,
        @Expose val phone: String?,
        @Expose val email: String?,
        @Expose val address: String?,
        @Expose val tax: String?,
        @Expose val website: String?,
        @Expose val facebook: String?,
        @Expose val youtube: String?,
        @Expose val fax: String?,
        @Expose val verified: Boolean?,
        @Expose val title: String?,
        @Expose val glnCode: String?,
        @Expose val ward: ICWard?,
        @Expose val district: ICDistrict?,
        @Expose val city: ICProvince?
) : Serializable