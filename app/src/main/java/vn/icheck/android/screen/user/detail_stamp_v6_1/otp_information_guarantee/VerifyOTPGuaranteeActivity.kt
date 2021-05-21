package vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee

import android.annotation.SuppressLint
import android.app.Activity
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_verify_otpguarantee.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICUpdateCustomerGuarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.presenter.VerifyOTPGuaranteePresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.view.IVerifyOTPGuaranteeView
import vn.icheck.android.util.KeyboardUtils

class VerifyOTPGuaranteeActivity : BaseActivity<VerifyOTPGuaranteePresenter>(),IVerifyOTPGuaranteeView {

    override val getLayoutID: Int
        get() = R.layout.activity_verify_otpguarantee

    override val getPresenter: VerifyOTPGuaranteePresenter
        get() = VerifyOTPGuaranteePresenter(this)

    private var timer: CountDownTimer? = null

    @SuppressLint("SetTextI18n")
    override fun onInitView() {
        presenter.getDataByIntent(intent)
        if (StampDetailActivity.isVietNamLanguage == false) {
            txtTitle.text = "Enter verified code"
            txtOtp.text = "Verified code"
            txtStatus.text = "Resend code"
            btnConfirm.text = "Confirm"
        } else {
            txtTitle.text = "Nhập mã xác nhận"
            txtOtp.text = "Mã xác thực"
            txtStatus.text = "Gửi lại mã"
            btnConfirm.text = "Xác nhận"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun listener() {
        imgBack.setOnClickListener {
            finish()
        }

        edtOtp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                txtOtp.visibility = if (edtOtp.text.isNullOrEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        txtStatus.setOnClickListener {
            if (StampDetailActivity.isVietNamLanguage == false){
                if (txtStatus.text.toString() == "Resend code") {
                    progressBar.visibility = View.VISIBLE
                    txtStatus.text = "Sending code"

                    presenter.sendOtpConfirmPhone()
                }
            } else {
                if (txtStatus.text.toString() == "Gửi lại mã") {
                    progressBar.visibility = View.VISIBLE
                    txtStatus.text = "Đang gửi mã"

                    presenter.sendOtpConfirmPhone()
                }
            }
        }

        btnConfirm.setOnClickListener {
            presenter.confirmOtp(edtOtp.text.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTextTime(isShow: Boolean) {
        if (StampDetailActivity.isVietNamLanguage == false){
            if (isShow) {
                txtTime.visibility = View.VISIBLE
                txtTime.text = getString(R.string.khong_nhan_duoc_ma_xac_nhan_gui_lai_sau_xxx_giay_en, "60")

                layoutStatus.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
            } else {
                txtTime.visibility = View.INVISIBLE

                layoutStatus.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                txtStatus.text = "Resend code"
            }
        } else {
            if (isShow) {
                txtTime.visibility = View.VISIBLE
                txtTime.text = getString(R.string.khong_nhan_duoc_ma_xac_nhan_gui_lai_sau_xxx_giay, "60")

                layoutStatus.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
            } else {
                txtTime.visibility = View.INVISIBLE

                layoutStatus.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                txtStatus.setText(R.string.gui_lai_ma)
            }
        }
    }

    override fun onGetDataError() {
        if (StampDetailActivity.isVietNamLanguage == false) {
            DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai_en, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        } else {
            DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        }
    }

    override fun onGetDataIntentSuccess(obj: ICUpdateCustomerGuarantee) {
        KeyboardUtils.showSoftInput(edtOtp)

        if (StampDetailActivity.isVietNamLanguage == false){
            txtContent.text = Html.fromHtml(getString(R.string.otp_content_xxx_en, obj.phone))
        } else {
            txtContent.text = Html.fromHtml(getString(R.string.otp_content_xxx, obj.phone))
        }

        listener()
    }

    override fun onCountDownOtp() {
        showTextTime(true)

        timer?.cancel()
        timer = null

        timer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                showTextTime(false)
            }

            override fun onTick(millisecond: Long) {
                this@VerifyOTPGuaranteeActivity.let {
                    if (StampDetailActivity.isVietNamLanguage == false) {
                        txtTime?.text = it.getString(R.string.khong_nhan_duoc_ma_xac_nhan_gui_lai_sau_xxx_giay_en, (millisecond / 1000).toString())
                    } else {
                        txtTime?.text = it.getString(R.string.khong_nhan_duoc_ma_xac_nhan_gui_lai_sau_xxx_giay, (millisecond / 1000).toString())
                    }
                }
            }
        }

        timer?.start()
    }

    override fun updateInformationCusomterGuaranteeSuccess() {
        setResult(Activity.RESULT_OK)
        if (StampDetailActivity.isVietNamLanguage == false) {
            showShortSuccess("Successfully updated")
        } else {
            showShortSuccess("Cập nhật thông tin thành công")
        }
        onBackPressed()
    }

    override fun updateInformationCusomterGuaranteeFail() {
        if (StampDetailActivity.isVietNamLanguage == false) {
            showShortError("Occurred. Please try again")
        } else {
            showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun onErrorOtp(errorMessage: String) {
        layoutInputOtp.error = errorMessage
        edtOtp.setSelection(edtOtp.text.toString().length)
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
        timer = null
    }
}
