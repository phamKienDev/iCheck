package vn.icheck.android.loyalty.model

import java.io.Serializable

class ICUser(var id: Long = 0,
             var name: String? = null,
             var email: String? = null,
             var phone: String? = null,
             var avatar: String? = null,
             var address: String? = null,
             var city: ICProvince? = null,
             var district: ICDistrict? = null,
             var country: ICCountry? = null,
             var city_id: Int? = null,
             var district_id: Int? = null,
             var country_id: Int? = null,
             var avatar_thumbnails: ICThumbnail? = null,
             var ward_id: Int? = null,
             var ward: ICWard? = null,
             var device_id: String? = null
) : Serializable