package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICRelatedProduct(
        @SerializedName("rows")
        val rows: MutableList<ICProduct>
)