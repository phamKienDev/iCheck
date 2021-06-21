package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.R
import java.io.Serializable

data class ICUserFollowPage (
        @Expose var name:String,
        @Expose var avatar:String,
        @Expose var firstName:String,
        @Expose var lastName:String
) : Serializable{
    val getName: String
        get() {
            val n = "$lastName $firstName"
            return if (n.trim().isNotEmpty()) {
                n
            }else {
                getString(R.string.chua_cap_nhat)
            }
        }
}
