package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICValidStampSocial : Serializable {
    @Expose
    val suggest_apps: MutableList<ICSuggestApp>? = null

    @Expose
    val code: String? = null

    @Expose
    val theme: Int? = null

    @Expose
    val onlyIdentity: Boolean? = null

    @Expose
    val urlIdentity: String? = null
}

class ICSuggestApp(
        @Expose
        val title: String?,
        @Expose
        val target: String?,
        @Expose
        val content: String?,
        @Expose
        val type: String?
) : Serializable