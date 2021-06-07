package vn.icheck.android.network.base

interface RequestChat {
    fun onCheckSessionWhenChat(onIsUserLogged:() -> Unit, onRequestUserLoginSuccess:() -> Unit)
}

interface OnClickBtnChatListener {
    fun onClick(id: Long?)
}