package vn.icheck.android.model.country

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Nation(
        val name: String,
        @SerializedName("dial_code")
        val dialCode: String,
        val code: String
):Parcelable