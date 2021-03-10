package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICCollection(
        @Expose var id: Long = 0,

        // data kiểu cũ
        @Expose var name: String? = null,
        @Expose var image: String? = null,
        @Expose var thumbnails: ICThumbnail? = null,
        @Expose var product_count: Int = 0,
        @Expose var published_at: String? = null,
        @Expose var actor: ICActorV2? = null,
        @Expose var created_at: String? = null,
        @Expose var updated_at: String? = null,

        // data kiểu mới
        @Expose val title: String?,
        @Expose val bannerId: String?,
        @Expose val bannerUrl: String?,
        @Expose val bannerSize: String?,
        @Expose val destinationUrl: String?,
        @Expose val productCount: Int = 0,
        @Expose val publishedAt: String?,
        @Expose var products: MutableList<ICProduct>? = null
)