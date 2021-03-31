package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICProductLink(
        @Expose val id: Long,
        @Expose val images: String?,
        @Expose val name: String?,
        @Expose val listPrice: Long?, // Giá niêm yết
        @Expose val promotionPrice: Long?, // Giá km
        @Expose val link: String?
): Serializable