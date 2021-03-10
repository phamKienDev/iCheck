package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICListGifExchange(
    @Expose
    var id: Long? = null,
    @Expose
    var image: String? = null,
    @Expose
    var name: String? = null,
    @Expose
    var price: Long? = null,
    @Expose
    var remain: Long? = null
):Serializable