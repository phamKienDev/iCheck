package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICActionDataStampHistory(
        @Expose var stampIcheck: Boolean? = null,
        @Expose var imageUrl: String? = null,
        @Expose var productName: String? = null,
        @Expose var qrBarcode: String? = null,
) : Serializable
