package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICAttachments(
        @Expose
        var id: Long? = 0,
        @Expose
        var file_id: String? = null,
        @Expose
        var content: String? = null,
        @Expose
        var type: String? = null,
        @Expose
        var thumbnails: ICThumbnailAttachment? = null,
        @Expose
        var size: Int? = 0
)