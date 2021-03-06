package vn.icheck.android.network.models

import kotlin.Any

data class ICSuggestPage(
        val nationalCode: String? = null,
        val mail: String? = null,
        val provinceCode: String? = null,
        val isVerify: Boolean = false,
        val referenceId: String? = null,
        val zipCode: Any? = null,
        val objectType: Any? = null,
        val createdAt: String? = null,
        val wardName: String? = null,
        val id: Int? = null,
        val fax: Any? = null,
        val referenceSource: String? = null,
        val nationalName: String? = null,
        val updatedAt: String? = null,
        val managementName: Any? = null,
        val website: String? = null,
        val address: String? = null,
        val userSupporterId: Any? = null,
        val updatedBy: String? = null,
        val districtName: String? = null,
        val tax: String? = null,
        val avatar: String? = null,
        val isVisible: Boolean? = null,
        val provinceId: String? = null,
        val glnCode: String? = null,
        val userManagementId: Any? = null,
        val enterprisePrefixCode: String? = null,
        val districtId: String? = null,
        val nationalId: Int? = null,
        val phone: String? = null,
        val createdBy: String? = null,
        val name: String? = null,
        val provinceName: String? = null,
        val gltType: String? = null,
        val wardId: String? = null,
        val userSupporterName: Any? = null,
        val followCount: Int = 0,
        val likeCount: Int? = null,
        val parentId: Int? = null,
//		val followers: List<ICUser?>? = null,
        var selected: Boolean = false
)

