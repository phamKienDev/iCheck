package vn.icheck.android.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class ICPostMeta(
        @SerializedName("product") val product: ICProduct?,
        @SerializedName("page") val page: ICRelatedPage?,
        @SerializedName("post") val post: ICPost?,
        @SerializedName("user") val user: ICUser?,
        @SerializedName("criteria") val criteria: List<ICCriteriaReview>?
) : Serializable