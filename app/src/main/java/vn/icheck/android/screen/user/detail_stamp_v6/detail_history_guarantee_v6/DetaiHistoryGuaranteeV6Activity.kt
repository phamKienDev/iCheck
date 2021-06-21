package vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_detai_history_guarantee_v6.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6
import vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.presenter.DetaiHistoryGuaranteeV6Presenter
import vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.view.IDetaiHistoryGuaranteeV6View

class DetaiHistoryGuaranteeV6Activity : BaseActivityMVVM(), IDetaiHistoryGuaranteeV6View {

    val presenter = DetaiHistoryGuaranteeV6Presenter(this@DetaiHistoryGuaranteeV6Activity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detai_history_guarantee_v6)
        onInitView()
    }

    fun onInitView() {
        presenter.getDataIntent(intent)
        txtTitle.setText(R.string.chi_tiet_bao_hanh)
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getDataIntentSuccess(item: RESP_Log_History_v6) {
        tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>${getString(R.string.ho_va_ten)} : </font>" + "<b>" + item.customer?.name + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${getString(R.string.ho_va_ten)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>${getString(R.string.so_dien_thoai)} : </font>" + "<b>" + item.customer?.phone + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${getString(R.string.so_dien_thoai)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvTimeGuarantee.text = if (item.created_time != null) {
            Html.fromHtml("<font color=#434343>${getString(R.string.thoi_gian)} : </font>" + "<b>" +  TimeHelper.convertMillisecondToFomateSv(item.created_time!! * 1000L, "HH:mm dd/MM/yyyy") + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${getString(R.string.thoi_gian)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>${getString(R.string.diem_bao_hanh)} : </font>" + "<b>" + item.store?.name + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${getString(R.string.diem_bao_hanh)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }


        when (item.type) {
            0 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>${getString(R.string.tinh_trang_bao_hanh)} : </font>" + "<b>" + getString(R.string.da_kich_hoat) + "</b>")
            }
            1 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>${getString(R.string.tinh_trang_bao_hanh)} : </font>" + "<b>" + getString(R.string.tiep_nhan_bao_hanh) + "</b>")
            }
            2 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>${getString(R.string.tinh_trang_bao_hanh)} : </font>" + "<b>" + getString(R.string.tra_bao_hanh) + "</b>")
            }
            else -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>${getString(R.string.tinh_trang_bao_hanh)} : </font>" + "<b>" + getString(R.string.tu_choi_bao_hanh) + "</b>")
            }
        }

        tvNguoiPhuTrach.text = if (!item.user?.fullname.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>${getString(R.string.nhan_vien_phu_trach)} : </font>" + "<b>" + item.user?.fullname + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>${getString(R.string.nhan_vien_phu_trach)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }


        if (!item.note.isNullOrEmpty()) {
            tvNoteGuarantee.text = item.note
        } else {
            tvSubInforGuarantee.visibility = View.GONE
            layoutInforGuarantee.visibility = View.GONE
        }
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@DetaiHistoryGuaranteeV6Activity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@DetaiHistoryGuaranteeV6Activity, isShow)
    }
}
