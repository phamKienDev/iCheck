package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICQuestions(
        @Expose var type: String,
        @Expose var title: String,
        @Expose var options: MutableList<ICOptions>
): Serializable