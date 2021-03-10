package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICSurvey(
        @Expose var id: Long = 0,
        @Expose var title: String? = null,
        @Expose var start_date: String? = null,
        @Expose var end_date: String? = null,
        @Expose var created_at: String? = null,
        @Expose var updated_at: String? = null,
        @Expose var respond_url: String? = null,
        @Expose var questions: MutableList<ICQuestions> = mutableListOf(),
        var measuredHeight: Int = -1,
        var isExpand: Boolean = true,
        var totalAnswer: Int = 0
): Serializable