package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICReqAttachments (
        @Expose val file_id:String,
        @Expose val type:String
)