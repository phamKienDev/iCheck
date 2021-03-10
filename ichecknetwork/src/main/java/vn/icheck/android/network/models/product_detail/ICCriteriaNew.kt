package vn.icheck.android.network.models.product_detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICCriteriaNew(

	@Expose
	val criteriaId: Int? = null,

	@Expose
	val criteriaName: String? = null,

	@Expose
	val weight: Double? = null,

	@Expose
	val position: Int? = null
)
