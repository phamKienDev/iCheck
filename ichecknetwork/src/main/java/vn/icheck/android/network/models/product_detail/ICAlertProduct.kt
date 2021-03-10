package vn.icheck.android.network.models.product_detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICAlertProduct(

	@Expose
	val images: MutableList<String>? = null,

	@Expose
	val alertLevel: String? = null,

	@Expose
	val id: Int? = null,

	@Expose
	val title: String? = null,

	@Expose
	val alertDescription: String? = null,

	@Expose
	val content: String? = null
) : Serializable
