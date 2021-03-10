package vn.icheck.android.screen.user.selectprovincestamp.presenter

import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.address.AddressInteractor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICProvinceStamp
import vn.icheck.android.screen.user.selectprovincestamp.view.SelectProvinceStampView

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SelectProvinceStampPresenter(val view: SelectProvinceStampView) : BaseActivityPresenter(view) {
    private val addressInteraction = AddressInteractor()

    private var offset = 0
    private val limit = 30

    fun getListProvince() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        addressInteraction.getListProvinceStamp( object : ICApiListener<ICProvinceStamp> {
            override fun onSuccess(obj: ICProvinceStamp) {
                view.onCloseLoading()

                for (it in obj.data?.cities ?: mutableListOf()) {
                    it.searchKey = TextHelper.unicodeToKoDauLowerCase(it.name)
                }

                view.onSetListProvince(obj.data?.cities)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }

    fun disposeApi() {
        addressInteraction.dispose()
    }
}