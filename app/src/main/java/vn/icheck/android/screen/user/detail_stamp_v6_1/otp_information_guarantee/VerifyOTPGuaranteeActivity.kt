package vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICUpdateCustomerGuarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.presenter.VerifyOTPGuaranteePresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.view.IVerifyOTPGuaranteeView
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.rText

class VerifyOTPGuaranteeActivity : BaseActivity<VerifyOTPGuaranteePresenter>(),IVerifyOTPGuaranteeView {

    override val getLayoutID: Int
        get() = R.layout.activity_verify_otpguarantee

    override val getPresenter: VerifyOTPGuaranteePresenter
        get() = VerifyOTPGuaranteePresenter(this)

    private var timer: CountDownTimer? = null

    @SuppressLint("SetTextI18n")
    override fun onInitView() {
        presenter.getDataByIntent(intent)
        if (DetailStampActivity.isVietNamLanguage == false) {
            txtTitle rText R.string.enter_verified_code
            txtOtp rText R.string.verified_code
            txtStatus rText R.string.resend_code
            btnConfirm rText R.string.confirm
        } else {
            txtTitle rText R.string.nhap_ma_xac_nhan
            txtOtp rText R.string.ma_xac_thuc
            txtStatus rText R.string.gui_lai_ma
            btnConfirm rText R.string.xac_nhan
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
            if (DetailStampActivity.isVietNamLanguage == false){
                if (txtStatus.text.toString() == rText(R.string.resend_code)) {
                    progressBar.visibility = View.VISIBLE
                    txtStatus rText R.string.sending_code

                    presenter.sendOtpConfirmPhone()
                }
            } else {
                if (txtStatus.text.toString() == rText(R.string.gui_lai_ma)) {
                    progressBar.visibility = View.VISIBLE
                    txtStatus rText R.string.dang_gui_ma

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
        if (DetailStampActivity.isVietNamLanguage == false){
            if (isShow) {
                txtTime.visibility = View.VISIBLE
                txtTime.text = getString(R.string.khong_nhan_duoc_ma_xac_nhan_gui_lai_sau_xxx_giay_en, "60")

                layoutStatus.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
            } else {
                txtTime.visibility = View.INVISIBLE

                layoutStatus.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                txtStatus rText R.string.resend_code
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
        if (DetailStampActivity.isVietNamLanguage == false) {
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

        if (DetailStampActivity.isVietNamLanguage == false){
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
                    if (DetailStampActivity.isVietNamLanguage == false) {
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
        if (DetailStampActivity.isVietNamLanguage == false) {
            showShortSuccess(rText(R.string.successfully_updated))
        } else {
            showShortSuccess(rText(R.string.cap_nhat_thong_tin_thanh_cong))
        }
        onBackPressed()
    }

    override fun updateInformationCusomterGuaranteeFail() {
        if (DetailStampActivity.isVietNamLanguage == false) {
            showShortError(rText(R.string.occurred_please_try_again))
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
