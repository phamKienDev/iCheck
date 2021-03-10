package vn.icheck.android.network.models

import java.io.Serializable
import kotlin.Any

data class ICCustomer(
        var id: Long = 0,
        var name: String = "",
        val level: Int = 0,
        val icheckId: Int? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        val createdAt: String? = null,
        val deletedAt: Any? = null,
        val roleId: Any? = null,
        val titleId: Int? = null,
        val rank: ICRankOfUser? = null,
        var avatar: String? = null,
        val updatedAt: String? = null,
        var selected: Boolean = false,
        val blocked: Boolean? = null,
        val gender: String? = null,
        val phone: String? = null,
        val email: String? = null
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
