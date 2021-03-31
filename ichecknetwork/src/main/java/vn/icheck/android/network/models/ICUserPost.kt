package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.wall.ICUserPrivacyConfig
import java.io.Serializable

data class ICUserPost(
        @Expose var id: Long,
        @Expose var name: String,
        @Expose var avatar: String,
        @Expose var firstName: String?,
        @Expose var lastName: String?,
        @Expose var rank: ICRankOfUser,
        @Expose var userPrivacyConfig: ICUserPrivacyConfig? = null,
        @Expose val kycStatus: Int? = null // 0= chưa gửi kyc, 1=đã gửi kyc, 2=kyc đã verify, 3=kyc bị từ chối
        ): Serializable {
    val getName: String
        get() {
            val n = "${lastName ?: ""} ${firstName ?: ""}"
            return if (n.trim().isNotEmpty()) {
                n
            } else {
                "Chưa cập nhật"
            }
        }
}
