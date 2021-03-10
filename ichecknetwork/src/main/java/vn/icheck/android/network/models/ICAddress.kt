package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICAddress(
        @Expose var id: Long? = null,
        @Expose var customer_id: Long? = null,
        @Expose var account_id: Long? = null,
        @Expose var first_name: String? = null,
        @Expose var last_name: String? = null,
        @Expose var phone: String? = null,
        @Expose var email: String? = null,
        @Expose var address: String? = null,
        @Expose var address1: String? = null,
        //@Expose("var ") var state": null,
        @Expose var zipcode: String? = null,
        @Expose var company: String? = null,
        @Expose var country_id: Int? = null,
        @Expose var city_id: Int? = null,
        @Expose var district_id: Int? = null,
        @Expose var ward_id: Int? = null,
        @Expose var is_default: Boolean? = null,
        @Expose var latitude: Double? = null,
        @Expose var longitude: Double? = null,
        @Expose var created_at: String? = null,
        @Expose var updated_at: String? = null,
        //@Expose("var ") var deleted_at": null = null,
        @Expose var user_id: Long? = null,
        @Expose var country: ICCountry? = null,
        @Expose var city: ICProvince? = null,
        @Expose var district: ICDistrict? = null,
        @Expose var ward: ICWard? = null,
        @Expose val firstName: String? = null,
        @Expose val lastName: String? = null,
        @Expose val zipCode: Int? = null,
        @Expose val state: Int? = null
)