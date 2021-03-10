package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductQuestionV2Model (
        @Expose
        @SerializedName("content")
        val content: String,
        @Expose
        @SerializedName("attachments")
        var attachments: List<String>)