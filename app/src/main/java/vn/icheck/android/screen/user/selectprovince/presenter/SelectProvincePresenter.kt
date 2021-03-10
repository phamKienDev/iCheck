package vn.icheck.android.screen.user.selectprovince.presenter

import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.address.AddressInteractor
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.screen.user.selectprovince.view.SelectProvinceView

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SelectProvincePresenter(val view: SelectProvinceView) : BaseActivityPresenter(view) {
    private val addressInteraction = AddressInteractor()

    private var offset = 0
    private val limit = 30

    fun getListProvince(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        if (!isLoadMore) {
            view.onShowLoading()
            offset = 0
        }

        addressInteraction.getListProvince(offset, limit, object : ICApiListener<ICListResponse<ICProvince>> {
            override fun onSuccess(obj: ICListResponse<ICProvince>) {
                view.onCloseLoading()

                for (it in obj.rows) {
                    it.searchKey = TextHelper.unicodeToKoDauLowerCase(it.name)
                }

                view.onSetListProvince(obj.rows, isLoadMore)

                if (obj.rows.size >= limit) {
                    offset += limit
                    getListProvince(true)
                }
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