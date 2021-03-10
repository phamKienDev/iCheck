package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICReview(
        @Expose val id: Long = 0,
        @Expose val content: String? = null,
        @Expose val rating: Float = 0F,
        @Expose val object_id: Long = 0,
        @Expose val created_at: String? = null,
        @Expose val updated_at: String? = null,
        @Expose val points: Double = 0.toDouble(),
        @Expose val account: ICAccount? = null,
        @Expose val attachments: MutableList<ICAttachments>? = null,
        @Expose val product: ICProduct? = null
) : Serializable