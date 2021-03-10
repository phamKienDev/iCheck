package vn.icheck.android.screen.account.registeruser.inputotp

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.CountDownTimer
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_register_user_otp.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.account.home.AccountActivity
import vn.icheck.android.screen.account.registeruser.inputotp.presenter.RegisterUserOtpPresenter
import vn.icheck.android.screen.account.registeruser.inputotp.view.IRegisterUserOtpView
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.ActivityUtils

/**
 * Created by VuLCL on 9/15/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class RegisterUserOtpActivity : BaseActivity<RegisterUserOtpPresenter>(), IRegisterUserOtpView {
    private var timer: CountDownTimer? = null

    override val getLayoutID: Int
        get() = R.layout.fragment_register_user_otp

    override val getPresenter: RegisterUserOtpPresenter
        get() = RegisterUserOtpPresenter(this)

    override fun onInitView() {
        setupTheme()
        initToolbar()
        presenter.getData(intent)
    }

    private fun setupTheme() {
//        val color = SettingManager.getTheme?.color ?: return
//
//        val setupTheme = SetupThemeHelper()
//
//        setupTheme.setLayoutBackground(FileHelper.loginBackground, color.background_color, imgBackground, layoutContainer)
//
//        setupTheme.setIconColor(color.icon_color, arrayOf(imgBack))
//        setupTheme.setTextColor(color.primary, arrayOf(txtTitle, txtStatus, txtTime, txtContent))
//
//        setupTheme.setTextColor(color.text_holder, arrayOf(txtOtp))
//        setupTheme.setInputHintColor(color.text_holder, arrayOf(edtOtp))
//        setupTheme.setInputTextColor(color.text_primary, arrayOf(edtOtp))
//        setupTheme.setInputErrorColor(color.error, arrayOf(layoutInputOtp))
//        setupTheme.setInputLineColor(color.input_border, arrayOf(edtOtp))
//
//        setupTheme.setTextColor(color.button_text_color, arrayOf(btnConfirm))
//        setupTheme.setBackgroundBlueCorner(color.button_background_color, arrayOf(btnConfirm))
    }

    private fun initToolbar() {
        txtTitle.text = getString(R.string.nhap_ma_xac_nhan)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initListener() {
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
            if (txtStatus.text.toString() == getString(R.string.gui_lai_ma)) {
                progressBar.visibility = View.VISIBLE
                txtStatus.setText(R.string.dang_gui_ma)

                presenter.sendOtpConfirmPhone()
            }
        }

        btnConfirm.setOnClickListener {
            presenter.confirmOtp(edtOtp.text.toString())
        }
    }

    private fun showTextTime(isShow: Boolean) {
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

    override fun onGetDataError() {
        DialogHelper.showNotification(this@RegisterUserOtpActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onGetDataSuccess(phone: String) {
        KeyboardUtils.showSoftInput(edtOtp)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                startActivity(callIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val contentStart = getString(R.string.otp_content_start)
        val contentEnd = getString(R.string.otp_content_end)
        val spannable = SpannableString(("$contentStart $phone $contentEnd"))

        val startPosition = contentStart.length
        val endPosition = spannable.length - contentEnd.length

        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)), startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(clickableSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        txtContent.text = spannable
        txtContent.movementMethod = LinkMovementMethod.getInstance()
//        txtContent.text = Html.fromHtml(getString(R.string.otp_content_xxx, phone))

        initListener()
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
                txtTime?.text = getString(R.string.khong_nhan_duoc_ma_xac_nhan_gui_lai_sau_xxx_giay, (millisecond / 1000).toString())
            }
        }

        timer?.start()
    }

    override fun onErrorOtp(errorMessage: String) {
        layoutInputOtp.error = errorMessage
        edtOtp.setSelection(edtOtp.text.toString().length)
    }

    override fun onNoInternet(type: Int) {
        DialogHelper.showConfirm(this@RegisterUserOtpActivity, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, object : ConfirmDialogListener {
            override fun onDisagree() {
                if (type == 1) {
                    showTextTime(false)
                }
            }

            override fun onAgree() {
                when(type) {
                    1 -> {
                        presenter.sendOtpConfirmPhone()
                    }
                    2 -> {
                        presenter.confirmOtp(edtOtp.text.toString())
                    }
                }
            }
        })
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onRegisterSuccess(phone: String) {
        if (presenter.getIsGoToLogin) {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra(Constant.DATA_2, phone)
            ActivityUtils.startActivity(this, intent)
            ActivityUtils.finishActivity(this)
        } else {
            val intent = Intent()
            intent.putExtra(Constant.DATA_1, phone)
            setResult(Activity.RESULT_OK, intent)
            ActivityUtils.finishActivity(this)
        }
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showLongError(errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
        timer = null
    }
}