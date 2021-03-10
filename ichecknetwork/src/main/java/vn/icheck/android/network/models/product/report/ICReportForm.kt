package vn.icheck.android.network.models.product.report

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICReportForm(
        @Expose var id: Int?,
        @Expose var name: String?,
        @Expose var description: String? = null
) : Serializable