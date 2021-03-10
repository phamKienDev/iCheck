package vn.icheck.android.network.models.criterias

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICCustomerCriterias(
        @Expose var id: Long = 0L,
        @Expose var name: String? = null,
        @Expose var point: Float = 0F
) : Serializable