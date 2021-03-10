package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.presenter

import vn.icheck.android.R
import vn.icheck.android.helper.AddressHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.room.entity.ICProvince
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.view.ISelectCityView

class SelectCityBottomSheetPresenter(val view: ISelectCityView) {
    private lateinit var addressHelper: AddressHelper

    fun setupAddressHelper() {
        view.mContext?.let {
            addressHelper = AddressHelper(it)
        }
    }

    fun getListProvince() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.showError(view.mContext?.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        addressHelper.getListProvince(0, 30, object : ICApiListener<MutableList<ICProvince>> {
            override fun onSuccess(obj: MutableList<ICProvince>) {
                view.onSetListProvince(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                val errorMessage = error?.message ?: view.mContext?.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.showError(errorMessage)
            }
        })
    }
}