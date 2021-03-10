package vn.icheck.android.screen.user.selectward.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.address.AddressInteractor
import vn.icheck.android.network.models.ICWard
import vn.icheck.android.screen.user.selectward.view.SelectWardView

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SelectWardPresenter(val view: SelectWardView) : BaseActivityPresenter(view) {
    private val addressInteraction = AddressInteractor()

    private var districtID = -1

    private var offset = 0
    private val limit = 30

    fun getData(intent: Intent?) {
        districtID = try {
            intent?.getIntExtra(Constant.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        getListWard(false)
    }

    fun getListWard(isLoadMore: Boolean) {
        if (districtID == -1) {
            view.onGetDataError()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        if (!isLoadMore) {
            view.onShowLoading()
            offset = 0
        }

        addressInteraction.getListWard(districtID, offset, limit, object : ICApiListener<ICListResponse<ICWard>> {
            override fun onSuccess(obj: ICListResponse<ICWard>) {
                view.onCloseLoading()

                for (it in obj.rows) {
                    it.searchKey = TextHelper.unicodeToKoDauLowerCase(it.name)
                }

                view.onSetListWard(obj.rows, isLoadMore)

                if (obj.rows.size >= limit) {
                    offset += limit
                    getListWard(true)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }
}