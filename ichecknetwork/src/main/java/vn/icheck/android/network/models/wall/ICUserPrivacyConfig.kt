package vn.icheck.android.network.models.wall

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICUserPrivacyConfig(
        @SerializedName("WHO_VIEW_YOUR_INFO")
        val whoViewYourInfo: String? = null,

        @SerializedName("WHO_INVITE_FRIEND")
        val whoInviteFriend: String? = null,

        @SerializedName("WHO_VIEW_YOUR_POST")
        val whoViewYourPost: String? = null,

        @SerializedName("WHO_COMMENT_YOUR_POST")
        val whoCommentYourPost: String? = null,

        @SerializedName("WHO_POST_YOUR_WALL")
        val whoPostYourWall: String? = null
) : Serializable
