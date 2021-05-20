package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICNews(
        @Expose
        var id: Long = 0,

        @Expose
        var title: String? = null,

        @Expose
        var alias: String? = null,

        @Expose
        var introtext: String? = null,

        @Expose
        var introtext2: String? = null,

        @Expose
        var fulltext: String? = null,

        @Expose
        var thumbnail: String? = null,

        @Expose
        var created: String? = null,

        @Expose
        var feature: String? = null,

        @Expose
        var is_new: String? = null,

        @Expose
        var createdAt: String? = null,

        @Expose
        var ctaLabel: String? = null,

        @Expose
        var ctaUrl: String? = null,

        @Expose
        var media: ICMedia? = null,

        @Expose
        val pageIds: List<Int>? = null,

        @Expose
        val pages: MutableList<ICPage>? = null,

        @Expose
        val articleCategory: ICArticleCategory? = null
) : Serializable