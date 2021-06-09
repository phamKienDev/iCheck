package vn.icheck.android.screen.user.detail_stamp_v5.detail_history_guarantee_v5

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_detai_history_guarantee_v5.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6
import vn.icheck.android.screen.user.detail_stamp_v5.detail_history_guarantee_v5.presenter.DetaiHistoryGuaranteeV5Presenter
import vn.icheck.android.screen.user.detail_stamp_v5.detail_history_guarantee_v5.view.IDetaiHistoryGuaranteeV5View

class DetaiHistoryGuaranteeV5Activity : BaseActivityMVVM(), IDetaiHistoryGuaranteeV5View {

    val presenter = DetaiHistoryGuaranteeV5Presenter(this@DetaiHistoryGuaranteeV5Activity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detai_history_guarantee_v5)

        onInitView()
    }

    fun onInitView() {
        presenter.getDataIntent(intent)
        txtTitle.text = "Chi tiết bảo hành"
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getDataIntentSuccess(item: RESP_Log_History_v6) {
        tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>Họ và tên : </font>" + "<b>" + item.customer?.name + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>Họ và tên : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>Số điện thoại : </font>" + "<b>" + item.customer?.phone + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>Số điện thoại : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvTimeGuarantee.text = if (item.created_time != null) {
            Html.fromHtml("<font color=#434343>Thời gian : </font>" + "<b>" +  TimeHelper.convertMillisecondToFomateSv(item.created_time!! * 1000L, "HH:mm dd/MM/yyyy") + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>Thời gian : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }

        tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>Điểm bảo hành : </font>" + "<b>" + item.store?.name + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>Điểm bảo hành : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
        }


        when (item.type) {
            0 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>Tình trạng bảo hành : </font>" + "<b>" + "Đã kích hoạt" + "</b>")
            }
            1 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>Tình trạng bảo hành : </font>" + "<b>" + "Tiếp nhận bảo hành" + "</b>")
            }
            2 -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>Tình trạng bảo hành : </font>" + "<b>" + "Trả bảo hành" + "</b>")
            }
            else -> {
                tvStateGuarantee.text = Html.fromHtml("<font color=#434343>Tình trạng bảo hành : </font>" + "<b>" + "Từ chối bảo hành" + "</b>")
            }
        }

        tvNguoiPhuTrach.text = if (!item.user?.fullname.isNullOrEmpty()) {
            Html.fromHtml("<font color=#434343>Nhân viên phụ trách : </font>" + "<b>" + item.user?.fullname + "</b>")
        } else {
            Html.fromHtml("<font color=#434343>Nhân viên phụ trách : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
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
        get() = this@DetaiHistoryGuaranteeV5Activity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@DetaiHistoryGuaranteeV5Activity, isShow)
    }
}
