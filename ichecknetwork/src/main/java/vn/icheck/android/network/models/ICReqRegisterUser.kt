package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICReqRegisterUser(
        @Expose val username: String,
        @Expose val phone: String,
        @Expose val password: String,
        @Expose val last_name: String,
        @Expose val first_name: String,
        @Expose val locale: String = "vi_VN",
        @Expose val gender: String = "male",
        @Expose val birth_day: Int = 1,
        @Expose val birth_month: Int = 1,
        @Expose val birth_year: Int = 1990,
        @Expose var timestamp: String? = null
)