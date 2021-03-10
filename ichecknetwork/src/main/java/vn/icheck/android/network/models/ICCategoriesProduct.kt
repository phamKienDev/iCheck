package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICCategoriesProduct (
        @Expose
        @SerializedName("categoryId")
        var id: Int?,
        @Expose
        @SerializedName("categoryName")
        val name: String,
        @Expose
        @SerializedName("products")
        var products: List<ICProductTrend>)