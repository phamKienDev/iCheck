package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.SerializedName

data class ICFieldGuarantee(

		@SerializedName("value_f")
		val valueF: MutableList<ValueFItem>? = null,

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

//		var inputContent:String? = null,
//		var inputArea:String? = null,
//		var date:String? = null
)

class ValueFItem {

    @SerializedName("id")
	var id: Int? = null

    @SerializedName("value")
	var value: String? = null

    var isChecked: Boolean = false

    override fun toString(): String {
        return value ?: ""
    }
}
