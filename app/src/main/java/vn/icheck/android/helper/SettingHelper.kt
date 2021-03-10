package vn.icheck.android.helper

import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.models.ICClientSetting

object SettingHelper {

    private val repository = SettingRepository()

    fun getSystemSetting(key: String?, keyGroup: String?, listener: ISettingListener) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            listener.onRequestError(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        repository.getSystemSetting(key, keyGroup, object : ICNewApiListener<ICResponse<ICListResponse<ICClientSetting>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICClientSetting>>) {
                listener.onGetClientSuccess(obj.data?.rows)
//                if (!obj.data?.rows.isNullOrEmpty()) {
//                    listener.onGetClientSuccess(obj.data?.rows)
//                } else {
//                    listener.onRequestError(ICheckApplication.getString(R.string.khong_co_du_lieu))
//                }
            }

            override fun onError(error: ICResponseCode?) {
                listener.onRequestError(ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }
}