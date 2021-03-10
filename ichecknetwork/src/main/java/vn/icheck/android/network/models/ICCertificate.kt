package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICCertificate(
        @SerializedName("id") val id: Long,
        @SerializedName("thumbnails") val thumbnails: ICThumbnail?
) : Serializable