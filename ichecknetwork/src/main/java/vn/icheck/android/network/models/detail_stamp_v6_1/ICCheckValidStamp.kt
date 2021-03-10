package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ICCheckValidStamp(
        @Expose
        val status: Int,
        @Expose
        val data: Data
) {
    class Data(
            @SerializedName("suggest_apps")
            val suggestApp: MutableList<SuggestApp>?,
            @SerializedName("code")
            val code: String?,
            @SerializedName("theme")
            val theme: Int?
    )

    class SuggestApp(
            @Expose
            val title: String?,
            @Expose
            val target: String?,
            @Expose
            val content: String?,
            @Expose
            val type: String?
    )
}
