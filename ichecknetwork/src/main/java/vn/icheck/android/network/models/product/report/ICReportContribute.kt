package vn.icheck.android.network.models.product.report

import com.google.gson.annotations.Expose

data class ICReportContribute(
        @Expose var reports: MutableList<ICReportForm>,
        @Expose var isVote: Boolean?,
        @Expose var reportMessage: String?
)