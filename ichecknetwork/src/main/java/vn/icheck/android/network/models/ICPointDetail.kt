package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICPointDetail(
        @Expose val location: Location?
) {
    data class Location(
            @Expose val lat: Double,
            @Expose val lng: Double
    )
}