package vn.icheck.android.loyalty.model

import com.google.gson.annotations.SerializedName

data class TopupServiceResponse(

        @field:SerializedName("phoneTopup")
        val phoneTopup: List<TopupServices.Service>? = null,

        @field:SerializedName("phoneCard")
        val phoneCard: List<PhoneCardItem?>? = null
)

data class PhoneCardItem(

        @field:SerializedName("serviceType")
        val serviceType: String? = null,

        @field:SerializedName("code")
        val code: String? = null,

        @field:SerializedName("provider")
        val provider: String? = null,

        @field:SerializedName("avatar")
        val avatar: String? = null,

        @field:SerializedName("serviceId")
        val serviceId: Int? = null,

        @field:SerializedName("denomination")
        val denomination: List<String?>? = null
)