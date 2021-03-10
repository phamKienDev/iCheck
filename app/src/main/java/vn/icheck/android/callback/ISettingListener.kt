package vn.icheck.android.callback

import vn.icheck.android.network.models.ICClientSetting

interface ISettingListener {
    fun onRequestError(error: String)
    fun onGetClientSuccess(list: MutableList<ICClientSetting>?)
}