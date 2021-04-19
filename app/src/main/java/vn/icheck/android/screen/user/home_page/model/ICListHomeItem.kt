package vn.icheck.android.screen.user.home_page.model

import vn.icheck.android.network.models.ICLayout

data class ICListHomeItem(
        val widgetID: String,
        val list: MutableList<ICHomeItem> = mutableListOf(),
        val listLayout: MutableList<ICLayout> = mutableListOf()
)