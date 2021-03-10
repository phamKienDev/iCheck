package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICButtonSuggestionModel(
        @Expose
        var message: String? = null,
        @Expose
        var allowClose: Boolean? = null,
        @Expose
        var image: String? = null,
        @Expose
        var button: ICButton? = null
) : Serializable