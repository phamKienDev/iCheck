package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICMessage(
        @Expose val key: String,
        @Expose val title: String,
        @Expose val content: String
)