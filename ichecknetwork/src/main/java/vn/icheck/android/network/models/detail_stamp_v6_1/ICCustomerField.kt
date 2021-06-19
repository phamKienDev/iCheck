package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICCustomerField(

//        @SerializedName("value_f")
//        val valueF: MutableList<ValueFItem>? = null,

        @SerializedName("value")
        val value: String? = null,

        @SerializedName("name")
        val name: String? = null,

        @SerializedName("require")
        val require: Int? = null,

        @SerializedName("id")
        val id: Int? = null,

        @SerializedName("type")
        val type: String? = null,

        @SerializedName("key")
        val key: String = "",

        @SerializedName("status")
        val status: Int? = null,

        @SerializedName("string_values")
        var string_values: String? = null,
) : Serializable

//class ValueFItem : Serializable {
//
//    @SerializedName("id")
//    var id: Int? = null
//
//    @SerializedName("value")
//    var value: String? = null
//
//    var isChecked: Boolean = false
//
//    override fun toString(): String {
//        return value ?: ""
//    }
//}
