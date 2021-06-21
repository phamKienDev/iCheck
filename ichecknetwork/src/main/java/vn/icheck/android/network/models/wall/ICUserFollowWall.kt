package vn.icheck.android.network.models.wall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.ichecklibs.util.RStringUtils
import vn.icheck.android.network.R
import vn.icheck.android.network.models.ICDisplayConfig
import vn.icheck.android.network.models.ICPrivacy
import vn.icheck.android.network.models.ICRankOfUser

data class ICUserFollowWall(
        @Expose var id: Long?,
        @Expose var icheckId: Long?,
        @Expose var firstName: String?,
        @Expose var lastName: String?,
        @Expose var phone: String?,
        @Expose var email: String?,
        @Expose var name: String?,
        @Expose var avatar: String?,
        @Expose var rank: ICRankOfUser?,
        @Expose var titleId: Int?,
        @Expose var roleId: Int?,
        @Expose var createdAt: String?,
        @Expose var updatedAt: String?,
        @Expose var deletedAt: String?,
        @Expose var userRelationStatus: Int?,
        @Expose var userPrivacyConfig: ICUserPrivacyConfig? = null,
        @Expose var kycStatus: Int? = null, // 0= chưa gửi kyc, 1=đã gửi kyc, 2=kyc đã verify, 3=kyc bị từ chối
        var sendAddFriend:Boolean?,
        @SerializedName("relateFriendCount") val relateFriendCount: Int=0
){
    fun getUserName():String {
            val n = "${lastName ?: ""} ${firstName ?: ""}"
            return if (n.trim().isNotEmpty()) {
                n
            }else if (!phone?.trim().isNullOrEmpty()) {
                getPhoneOnly()
            } else {
                RStringUtils.rText(R.string.chua_cap_nhat)
            }
        }

    fun getPhoneOnly(): String {
        return if (phone != null) {
            StringBuilder(phone!!).apply {
                replace(0, 2, "0").replace(7, length, "***")
            }.toString()
        } else {
            RStringUtils.rText(R.string.chua_cap_nhat)
        }
    }

}