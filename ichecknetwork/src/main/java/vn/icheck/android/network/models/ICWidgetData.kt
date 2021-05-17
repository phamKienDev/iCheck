package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.v1.ICImage

data class ICWidgetData(
        @Expose val id: Long?,
        @Expose val name: String?,
        @Expose val barcode: String?,
        @Expose val price: Long?,
        @Expose val country: String?,
        @Expose val ensign: String?,
        @Expose val atts: MutableList<ICMedia>?,
        @Expose val success: Int?,
        @Expose val text: String?,
        @Expose var serial: String?,
        @Expose val scanCount: Long?,
        @Expose val peopleCount: Long?,
        @Expose val guaranteeDay: Long?,
        @Expose val expireDate: String?,
        @Expose val dayRemaining: Long?,
        @Expose val activeDate: String?,
        @Expose val customerId: Long?,
        @Expose val createTime: String?,
        @Expose val storeName: String?,
        @Expose val status: String?,
        @Expose val state: String?,
//        @Expose val returnTime: null
        @Expose val code: String?,
        @Expose val website: String?,
//        @Expose val city: null,
//        @Expose val district: null,
        @Expose val address: String?,
        @Expose val phone: String?,
        @Expose val email: String?,
        @Expose val description: String?,
        @Expose val city: String?,
        @Expose val district: String?,
        @Expose val infors: MutableList<ICInfo>?,
        @Expose val productLinks: MutableList<ICProductLink>?




//"attachments": []
)