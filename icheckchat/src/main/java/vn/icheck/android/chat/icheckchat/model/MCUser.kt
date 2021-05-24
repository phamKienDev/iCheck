package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class MCUser : Serializable {

    @Expose
    val id: Long? = null

    @Expose
    val name: String? = null

    @Expose
    val rank: MCRank? = null

    @Expose
    val avatar: String? = null

    @Expose
    val lastName: String? = null

    @Expose
    val firstName: String? = null

    @Expose
    val phone: String? = null

    @Expose
    val kycStatus: Int? = null

    fun getPhoneOnly(): String {
        return if (phone != null) {
            StringBuilder(phone!!).apply {
                replace(0, 2, "0").replace(7, length, " ***")
                        .insert(4, " ")
            }.toString()
        } else {
            "Chưa cập nhật"
        }
    }

    val getName: String
        get() {
            val n = "${lastName ?: ""} ${firstName ?: ""}"
            return if (n.trim().isNotEmpty()) {
                n
            } else if (!phone?.trim().isNullOrEmpty()) {
                getPhoneOnly()
            } else {
                "Chưa cập nhật"
            }
        }
}