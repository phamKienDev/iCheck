package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.presenter

import vn.icheck.android.R
import vn.icheck.android.helper.AddressHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.room.entity.ICDistrict
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.view.ISelectDistrictView

class SelectDistrictBottomSheetPresenter(val view: ISelectDistrictView) {
    private lateinit var addressHelper: AddressHelper

    private var provinceID = -1

    fun setupAddressHelper() {
        view.mContext?.let {
            addressHelper = AddressHelper(it)
        }
    }

    fun getData(id: Int?) {
        provinceID = try {
            id ?: -1
        } catch (e: Exception) {
            -1
        }

        getListDistrict()
    }

    fun getListDistrict() {
        if (provinceID == -1) {
            view.showError(view.mContext?.getString(R.string.ban_can_phai_chon_tinh_thanh))
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.showError(view.mContext?.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        addressHelper.getListDistrict(provinceID,object : ICApiListener<MutableList<ICDistrict>> {
            override fun onSuccess(obj: MutableList<ICDistrict>) {
                view.onSetListDistrict(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                val errorMessage = error?.message ?: view.mContext?.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.showError(errorMessage)
            }
        })
    }
}