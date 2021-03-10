package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICPoints(
        @Expose val predictions: MutableList<Predictions>
) {

    data class Predictions(
            @Expose val description: String,
            @Expose val placeId: String,
            @Expose val mainText: String,
            @Expose val secondaryText: String
    )
}