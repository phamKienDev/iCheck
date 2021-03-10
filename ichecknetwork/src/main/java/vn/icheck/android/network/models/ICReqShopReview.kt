package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICReqShopReview(
        @Expose val object_id: Long,
        @Expose val object_type: String,
        @Expose val criteria: MutableList<Criteria>,
        @Expose val message: String?,
        @Expose val images: MutableList<String>?
) {

    data class Criteria(
            @Expose val criteria_id: Long,
            @Expose val point: Float
    )
}