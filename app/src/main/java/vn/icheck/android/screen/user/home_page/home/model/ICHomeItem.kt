package vn.icheck.android.screen.user.home_page.home.model

import vn.icheck.android.network.models.ICCustom
import vn.icheck.android.network.models.ICNews

data class ICHomeItem(
        var viewType: Int = 0,
        var widgetID: String = "",
        var theme: ICCustom? = null,
        var data: Any? = null,
        var type: Int? = null
)