package vn.icheck.android.screen.user.selectdistrict.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.address.AddressInteractor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICDistrictStamp
import vn.icheck.android.screen.user.selectdistrict.view.SelectDistrictStampView

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SelectDistrictStampPresenter(val view: SelectDistrictStampView) : BaseActivityPresenter(view) {
    private val addressInteraction = AddressInteractor()

    private var provinceID = -1

    private var offset = 0
    private val limit = 30

    fun getData(intent: Intent?) {
        provinceID = try {
            intent?.getIntExtra(Constant.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        getListDistrict()
    }

    fun getListDistrict() {
        if (provinceID == -1) {
            view.onGetDataError()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        addressInteraction.getListDistrictStamp(provinceID, object : ICApiListener<ICDistrictStamp> {
            override fun onSuccess(obj: ICDistrictStamp) {
                view.onCloseLoading()

                for (it in obj.data?.districts ?: mutableListOf()) {
                    it.searchKey = TextHelper.unicodeToKoDauLowerCase(it.name)
                }

                view.onSetListDistrict(obj.data?.districts)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }
}