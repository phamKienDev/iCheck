package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICListHistoryGuarantee(
        @Expose
        var batch_id: Long? = null,
        @Expose
        var created_time: String? = null,
        @Expose
        var expired_time: String? = null,
        @Expose
        var _id: String? = null,
        @Expose
        var number: Int? = null,
        @Expose
        var prefix: String? = null,
        @Expose
        var product_id: Long? = null,
        @Expose
        var return_time: String? = null,
        @Expose
        var store_id: Int? = null,
        @Expose
        var type: String? = null,
        @Expose
        var updated_time: String? = null,
        @Expose
        var user_created: Int? = null,
        @Expose
        var user_id: Long? = null,
        @Expose
        var fields: MutableList<ICObjectFields>? = null,
        @Expose
        var customer_id: Long? = null,
        @Expose
        var customer: ICObjectCustomerHistoryGurantee? = null,
        @Expose
        var images: MutableList<String>? = null,
        @Expose
        var status: ICObjectStatus? = null,
        @Expose
        var state: ICObjectStatus? = null,
        @Expose
        var __v: Int? = null,
        @Expose
        var note:String? = null,
        @Expose
        var store: ICObjectStoreGurantee? = null,
        @Expose
        var extra: ICVariantProductStampV6_1.ICVariant.ICObjectVariant? = null
) : Serializable