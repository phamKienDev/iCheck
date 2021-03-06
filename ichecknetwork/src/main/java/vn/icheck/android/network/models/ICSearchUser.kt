package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.R
import vn.icheck.android.network.models.wall.ICUserPrivacyConfig

data class ICSearchUser(
        @SerializedName("id") val id: Long,
        @SerializedName("icheckId") val icheckId: Long,
        @SerializedName("rank") val rank: ICRankOfUser?,
        @SerializedName("firstName") val firstName: String?,
        @SerializedName("lastName") val lastName: String?,
        @SerializedName("avatar") val avatar: String?,
        @SerializedName("birthDay") val birthDay: String?,
        @SerializedName("gender") val gender: Int?,
        @SerializedName("phone") val phone: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("address") val address: String?,
        @SerializedName("cityId") val cityId: Long?,
        @SerializedName("city") val city: ICProvince?,
        @SerializedName("districtId") val districtId: Long?,
        @SerializedName("district") val district: ICDistrict?,
        @SerializedName("wardId") val wardId: Long?,
        @SerializedName("ward") val ward: ICWard?,
        @SerializedName("titleId") val titleId: Long,
        @SerializedName("title") val title: ICTitle?,
        @SerializedName("roleId") val roleId: Long,
        @SerializedName("role") val role: ICRole,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("accountType") val accountType: Int,
        @SerializedName("emailVerified") val emailVerified: Int,
        @SerializedName("phoneVerified") val phoneVerified: Int,
        @SerializedName("background") val background: String?,
        @SerializedName("icheckStatus") val icheckStatus: Int,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?,
        @SerializedName("relateFriendCount") val relateFriendCount: Int = 0,
        @SerializedName("wall") val wall: ICWall,
        @SerializedName("userPrivacyConfig") val userPrivacyConfig: ICUserPrivacyConfig? = null,
        @Expose val kycStatus: Int? = null, // 0= ch??a g???i kyc, 1=???? g???i kyc, 2=kyc ???? verify, 3=kyc b??? t??? ch???i

        var requestStatus: Int = 0,
        var statusClient: Int = 0
) {

    val getName: String
        get() {
            val n = "${lastName ?: ""} ${firstName ?: ""}"
            return if (n.trim().isNotEmpty()) {
                n
            } else if (!phone?.trim().isNullOrEmpty()) {
                getPhoneOnly()
            } else {
                getString(R.string.chua_cap_nhat)
            }
        }

    fun getPhoneOnly(): String {
        return if (phone != null) {
            StringBuilder(phone!!).apply {
                replace(0, 2, "0").replace(7, length, "***")
            }.toString()
        } else {
            getString(R.string.chua_cap_nhat)
        }
    }

}