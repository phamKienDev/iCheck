package vn.icheck.android.network.models

import java.io.Serializable

data class ICCriteriaReview(
        val updatedAt: String? = null,
        val criteriaId: Long? = null,
        val criteriaSetId: Long? = null,
        val ccsvId: Int? = null,
        val customerId: String? = null,
        val weight: Double? = null,
        val createdAt: String? = null,
        val id: Long? = null,
        val position: Int? = null,
        var point: Float = 0f,
        val objectId: String? = null,
        val objectType: String? = null,
        val name: String? = null,
        val criteriaName: String? = null
) : Serializable