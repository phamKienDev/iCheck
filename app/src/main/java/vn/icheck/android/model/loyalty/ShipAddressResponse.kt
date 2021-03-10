package vn.icheck.android.model.loyalty

import com.google.gson.annotations.SerializedName
import vn.icheck.android.model.icklogin.City
import vn.icheck.android.model.icklogin.District
import vn.icheck.android.model.icklogin.Ward

data class ShipAddressResponse(

        @field:SerializedName("lastName")
        val lastName: String? = null,

        @field:SerializedName("zipCode")
        val zipCode: Int? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("city")
        val city: City? = null,

        @field:SerializedName("address1")
        val address1: String? = null,

        @field:SerializedName("latitude")
        val latitude: String? = null,

        @field:SerializedName("ward")
        val ward: Ward? = null,

        @field:SerializedName("userId")
        val userId: Long? = null,

        @field:SerializedName("firstName")
        val firstName: String? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("district")
        val district: District? = null,

        @field:SerializedName("company")
        val company: String? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("state")
        val state: Int? = null,

        @field:SerializedName("fax")
        val fax: String? = null,

        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("longitude")
        val longitude: String? = null
){
        var isChecked = false

        fun getFullAddress() = "${address}, ${ward?.name}, ${district?.name}, ${city?.name}"

        fun getName() = ("$lastName $firstName")
}
