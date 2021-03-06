package vn.icheck.android.network.base

import com.google.gson.annotations.Expose
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.R

open class ICResponseCode {
    @Expose var statusCode: String = ""
    @Expose var status: String? = ""
    @Expose var code: Int = 0
    @Expose var message: String? = getString(R.string.khong_tai_duoc_du_lieu_vui_long_thu_lai)
}