package vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_detai_history_guarantee_v6.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6
import vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.presenter.DetaiHistoryGuaranteeV6Presenter
import vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.view.IDetaiHistoryGuaranteeV6View
import vn.icheck.android.util.ick.rText

class DetaiHistoryGuaranteeV6Activity : BaseActivity<DetaiHistoryGuaranteeV6Presenter>(), IDetaiHistoryGuaranteeV6View {

    override val getLayoutID: Int
        get() = R.layout.activity_detai_history_guarantee_v6

    override val getPresenter: DetaiHistoryGuaranteeV6Presenter
        get() = DetaiHistoryGuaranteeV6Presenter(this)

    override fun onInitView() {
        presenter.getDataIntent(intent)
        txtTitle rText R.string.chi_tiet_bao_hanh
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getDataIntentSuccess(item: RESP_Log_History_v6) {
        tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>${rText(R.string.ho_va_ten)} : </font>" + "<b>" + item.customer?.name + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${rText(R.string.ho_va_ten)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>${rText(R.string.so_dien_thoai)} : </font>" + "<b>" + item.customer?.phone + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${rText(R.string.so_dien_thoai)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvTimeGuarantee.text = if (item.created_time != null) {
            Html.fromHtml("<font color=#434343>${rText(R.string.thoi_gian)} : </font>" + "<b>" +  TimeHelper.convertMillisecondToFomateSv(item.created_time!! * 1000L, "HH:mm dd/MM/yyyy") + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${rText(R.string.thoi_gian)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>${rText(R.string.diem_bao_hanh)} : </font>" + "<b>" + item.store?.name + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${rText(R.string.diem_bao_hanh)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }


        when (item.type) {
            0 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>${rText(R.string.tinh_trang_bao_hanh)} : </font>" + "<b>" + rText(R.string.da_kich_hoat) + "</b>")
            }
            1 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>${rText(R.string.tinh_trang_bao_hanh)} : </font>" + "<b>" + rText(R.string.tiep_nhan_bao_hanh) + "</b>")
            }
            2 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>${rText(R.string.tinh_trang_bao_hanh)} : </font>" + "<b>" + rText(R.string.tra_bao_hanh) + "</b>")
            }
            else -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>${rText(R.string.tinh_trang_bao_hanh)} : </font>" + "<b>" + rText(R.string.tu_choi_bao_hanh) + "</b>")
            }
        }

        tvNguoiPhuTrach.text = if (!item.user?.fullname.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>${rText(R.string.nhan_vien_phu_trach)} : </font>" + "<b>" + item.user?.fullname + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${rText(R.string.nhan_vien_phu_trach)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }


        if (!item.note.isNullOrEmpty()) {
            tvNoteGuarantee.text = item.note
        } else {
            tvSubInforGuarantee.visibility = View.GONE
            layoutInforGuarantee.visibility = View.GONE
        }
    }
}
