package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICOptions(
        @Expose var title: String? = null,
        var isChecked: Boolean = false
):Serializable