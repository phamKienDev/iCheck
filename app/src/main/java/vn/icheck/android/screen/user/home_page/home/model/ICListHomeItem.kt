package vn.icheck.android.screen.user.home_page.home.model

import vn.icheck.android.network.models.ICLayout
import vn.icheck.android.screen.user.home_page.home.model.ICHomeItem

data class ICListHomeItem(
        val widgetID: String,
        val list: MutableList<ICHomeItem> = mutableListOf(),
        val listLayout: MutableList<ICLayout> = mutableListOf()
)