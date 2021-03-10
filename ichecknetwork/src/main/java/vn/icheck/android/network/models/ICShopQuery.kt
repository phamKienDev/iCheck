package vn.icheck.android.network.models

import java.io.Serializable

data class ICShopQuery(
        val isSyncedFromGateway: Boolean? = null,
        val createdAt: String? = null,
        val sales: Int? = null,
        val minOrderValue: Int? = null,
        val cover: String? = null,
        val isOffline: Boolean = false,
        val pageId: Int? = null,
        val blocked: Boolean? = null,
        val updatedAt: String? = null,
        val verifiedByUserId: Int? = null,
        val id: Long,
        val isOnline: Boolean? = null,
        val email: String? = null,
        val address: String? = null,
        val address1: String? = null,
        val verified: Boolean? = null,
        val avatar: String? = null,
        val deletedAt: String? = null,
        val phone: String? = null,
        val blockedReason: Any? = null,
        val hideAllProduct: Boolean? = null,
        val name: String? = null,
        val location: ICLocation? = null,
        val countryId: Int? = null,
        val status: Int? = null,
        val productCount: Int? = null,
        val rating: Double? = null
):Serializable




