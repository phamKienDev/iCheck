package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICQrScan : Serializable {

    @Expose
    val code: String? = null

    @Expose
    val url: String? = null

    @Expose
    val type: Long? = null
}