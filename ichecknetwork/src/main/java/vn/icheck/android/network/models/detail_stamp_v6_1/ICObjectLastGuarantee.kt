package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectLastGuarantee(
        @Expose
        var _id: String? = null,
        @Expose
        var type: String? = null,
        @Expose
        var prefix: String? = null,
        @Expose
        var number: Int? = null,
        @Expose
        var batch_id: Long? = null,
        @Expose
        var product_id: Long? = null,
        @Expose
        var product_code: String? = null,
        @Expose
        var variant_id: Long? = null,
        @Expose
        var user_id: Long? = null,
        @Expose
        var icheck_id: String? = null,
        @Expose
        var user_created: Long? = null,
        @Expose
        var store_id: Long? = null,
        @Expose
        var return_time: String? = null,
        @Expose
        var created_time: String? = null,
        @Expose
        var expired_time: String? = null,
        @Expose
        var updated_time: String? = null,
        @Expose
        var fields: MutableList<ICObjectFields>? = null,
        @Expose
        var customer_id: Long? = null,
        @Expose
        var customer: ICObjectCustomer? = null,
        @Expose
        var status: ICObjectStatus? = null,
        @Expose
        var state: ICObjectStatus? = null,
        @Expose
        var __v: Int? = null,
        @Expose
        var images: MutableList<String>? = null,
        @Expose
        var note: String? = null,
        @Expose
        var store: ICObjectStoreGurantee? = null,
        @Expose
        var variant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant? = null
) : Serializable