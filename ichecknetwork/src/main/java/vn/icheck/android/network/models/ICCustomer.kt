package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable
import kotlin.Any

data class ICCustomer(
        @Expose var id: Long = 0,
        @Expose var name: String = "",
        @Expose val level: Int = 0,
        @Expose val icheckId: Int? = null,
        @Expose var firstName: String? = null,
        @Expose var lastName: String? = null,
        @Expose val createdAt: String? = null,
        @Expose val deletedAt: Any? = null,
        @Expose val roleId: Any? = null,
        @Expose val titleId: Int? = null,
        @Expose val rank: ICRankOfUser? = null,
        @Expose var avatar: String? = null,
        @Expose val updatedAt: String? = null,
        @Expose var selected: Boolean = false,
        @Expose val blocked: Boolean? = null,
        @Expose val gender: String? = null,
        @Expose val phone: String? = null,
        @Expose val email: String? = null,
        @Expose val kycStatus: Int? = null
) : Serializable {
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
