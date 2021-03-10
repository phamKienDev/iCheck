package vn.icheck.android.network.models.stamp_hoa_phat

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICGetIdPageSocial(
        @Expose var id: Long? = null,
        @Expose var name: String? = null,
        @Expose var avatar: String? = null,
        @Expose var isVerify: Boolean? = null,
        @Expose var follower: Long? = null
) : Serializable
