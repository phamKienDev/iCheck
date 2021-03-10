package vn.icheck.android.screen.user.createqrcode.createevent.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.screen.user.createqrcode.createevent.view.ICreateEventQrCodeView

/**
 * Created by VuLCL on 10/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateEventQrCodePresenter(val view: ICreateEventQrCodeView) : BaseFragmentPresenter(view) {

    fun validData(eventName: String, eventAddress: String, eventLink: String, startDate: String, endDate: String) {
        var isSuccess = true

        var isStartDataSuccess = true
        var isEndDataSuccess = true

        val current = TimeHelper.convertDateTimeVnToMillisecond(TimeHelper.getCurrentDateTime()) ?: -1
        val start = TimeHelper.convertDateTimeVnToMillisecond(startDate) ?: -1

        if (current != -1L && start != -1L) {
            if (start < current) {
                isSuccess = false
                isStartDataSuccess = false
                view.onInvalidStartDate(getString(R.string.thoi_gian_bat_dau_khong_the_nho_hon_thoi_gian_hien_tai))
            }
        } else {
            isSuccess = false
        }

        val end = TimeHelper.convertDateTimeVnToMillisecond(endDate) ?: -1

        if (end != -1L) {
            if (start > end) {
                isSuccess = false
                isEndDataSuccess = false
                view.onInvalidEndDate(getString(R.string.thoi_gian_ket_thuc_khong_the_nho_hon_thoi_gian_bat_dau))
            }
        } else {
            isSuccess = false
        }

        if (endDate.isEmpty()) {
            isSuccess = false
            view.onInvalidEndDate(getString(R.string.ngay_ket_thuc_khong_duoc_de_trong))
        }

        if (startDate.isEmpty()) {
            isSuccess = false
            view.onInvalidStartDate(getString(R.string.ngay_bat_dau_khong_duoc_de_trong))
        }

        val validLink = ValidHelper.validLink(view.mContext, eventLink)

        if (validLink != null) {
            isSuccess = false
            view.onInvalidEventLink(validLink)
        } else {
            view.onInvalidEventLinkSuccess()
        }

        val validAddress = ValidHelper.validAddress(view.mContext, eventAddress)

        if (validAddress != null) {
            isSuccess = false
            view.onInvalidEventAddress(validAddress)
        } else {
            view.onInvalidEventAddressSuccess()
        }

        if (eventName.trim().isEmpty()) {
            isSuccess = false
            view.onInvalidEventName(getString(R.string.vui_long_nhap_du_lieu))
        } else {
            view.onInvalidEventNameSuccess()
        }

        if (!isSuccess) {
            return
        }

        val mStartDate = TimeHelper.convertDateTimeVnToEventFormat(start) ?: ""
        val mEndDate = TimeHelper.convertDateTimeVnToEventFormat(end) ?: ""
        val code = view.mContext!!.getString(R.string.qr_code_event_format, eventName, eventAddress, eventLink, mStartDate, mEndDate)

        view.onValidSuccess(code)
    }
}