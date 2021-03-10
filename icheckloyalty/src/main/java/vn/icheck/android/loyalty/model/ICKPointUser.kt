package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICKPointUser(

        @Expose
        val total_points: Long? = null,

        @Expose
        val points: Long? = null,

        @Expose
        val campaign_id: Long? = null,

        @Expose
        val image: ICThumbnail? = null,

        @Expose
        val avatar: ICThumbnail? = null,

        @Expose
        val name: String? = null,

        @Expose
        val created_at: String? = null,

        @Expose
        val phone: String? = null
) : Serializable