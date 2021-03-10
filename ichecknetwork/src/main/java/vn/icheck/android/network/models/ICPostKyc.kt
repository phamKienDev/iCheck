package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICPostKyc(
        @SerializedName("requestKycLevel") val requestKycLevel: Int,
        @SerializedName("kycDocuments") val kycDocment: MutableList<KycDocuments>
) {
    data class KycDocuments(
            @SerializedName("type") val type: Int,
            @SerializedName("documentName") val documentName: String?,
            @SerializedName("document") val document: MutableList<String>?
    )
}