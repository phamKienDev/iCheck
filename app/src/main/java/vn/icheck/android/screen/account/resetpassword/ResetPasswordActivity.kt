package vn.icheck.android.screen.account.resetpassword

import android.os.CountDownTimer
import android.view.View
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.account.resetpassword.presenter.ResetPasswordPresenter
import vn.icheck.android.screen.account.resetpassword.view.IResetPasswordView
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 9/18/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ResetPasswordActivity : BaseActivity<ResetPasswordPresenter>(), IResetPasswordView, View.OnClickListener {
    private var timer: CountDownTimer? = null

    override val getLayoutID: Int
        get() = R.layout.activity_reset_password

    override val getPresenter: ResetPasswordPresenter
        get() = ResetPasswordPresenter(this)

    override fun onInitView() {
        setupTheme()
        presenter.getData(intent)
    }

    private fun setupTheme() {
//        val color = SettingManager.themeSetting?.color ?: return
//
//        val setupTheme = SetupThemeHelper()
//
//        setupTheme.setLayoutBackground(FileHelper.loginBackground, color.background_color, imgBackground, layoutContainer)
//
//        setupTheme.setIconColor(color.icon_color, arrayOf(imgBack))
//        setupTheme.setTextColor(color.primary, arrayOf(txtTitle))
//
//        setupTheme.setInputHintColor(color.text_holder, arrayOf(edtOtp, edtPassword, edtRePassword))
//        setupTheme.setInputTextColor(color.text_primary, arrayOf(edtOtp, edtPassword, edtRePassword))
//        setupTheme.setInputErrorColor(color.error, arrayOf(layoutInputOtp, layoutInputPassword, layoutInputRePassword))
//        setupTheme.setInputLineColor(color.input_border, arrayOf(edtOtp, edtPassword, edtRePassword))
//        setupTheme.setDrawableLeftColor(color.icon_color, arrayOf(edtPassword, edtRePassword), arrayOf(R.drawable.ic_password_unlock_blue_18, R.drawable.ic_password_unlock_blue_18))
//        setupTheme.setIconColor(color.icon_color_secondary, arrayOf(imgShowOrHidePassword, imgShowOrHideRePassword))
//
//        setupTheme.setTextColor(color.button_text_color, arrayOf(btnCreate))
//        setupTheme.setBackgroundBlueCorner(color.button_background_color, arrayOf(btnCreate))
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.xac_nhan_tao_mat_khau_moi)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initListener() {
        WidgetUtils.setClickListener(this, txtTime, imgShowOrHidePassword, imgShowOrHideRePassword, btnCreate)
    }

    override fun onGetDataError() {
        showError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        onBackPressed()
    }

    override fun onGetDataSuccess() {
        initToolbar()
        onCountDownOtp()
        initListener()

        KeyboardUtils.showSoftInput(edtOtp)
    }

    override fun onNoInternet(type: Int) {
        DialogHelper.showConfirm(this@ResetPasswordActivity, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, object : ConfirmDialogListener {
            override fun onDisagree() {

            }

            override fun onAgree() {
                when (type) {
                    1 -> {
                        if (txtTime.text == getString(R.string.gui_lai_upcase)) {
                            presenter.sendOtp()
                        }
                    }
                    2 -> {
                        presenter.resetPassword(edtOtp.text.toString().trim(), edtPassword.text.toString().trim(), edtRePassword.text.toString().trim())
                    }
                }
            }
        })
    }

    override fun showLoading() {
        DialogHelper.showLoading(this)
    }

    override fun closeLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onCountDownOtp() {
        edtOtp.text = null

        timer?.cancel()
        timer = null

        timer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                txtTime?.setText(R.string.gui_lai_upcase)
            }

            override fun onTick(millisecond: Long) {
                txtTime?.text = ((millisecond / 1000).toString() + "s")
            }
        }

        timer?.start()
    }

    override fun onErrorOtp(errorMessage: String) {
        layoutInputOtp.error = errorMessage
    }

    override fun onErrorPassword(errorMessage: String) {
        layoutInputPassword.error = errorMessage
    }

    override fun onErrorRePassword(errorMessage: String) {
        layoutInputRePassword.error = errorMessage
    }

    override fun onResetPasswordSuccess() {
        ToastUtils.showShortSuccess(this@ResetPasswordActivity, R.string.doi_mat_khau_thanh_cong)
        onBackPressed()
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        ToastUtils.showShortError(this@ResetPasswordActivity, errorMessage)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtTime -> {
                if (txtTime.text == getString(R.string.gui_lai_upcase)) {
                    presenter.sendOtp()
                }
            }
            R.id.imgShowOrHidePassword -> {
                WidgetUtils.showOrHidePassword(edtPassword, imgShowOrHidePassword)
            }
            R.id.imgShowOrHideRePassword -> {
                WidgetUtils.showOrHidePassword(edtRePassword, imgShowOrHideRePassword)
            }
            R.id.btnCreate -> {
                KeyboardUtils.hideSoftInput(view)
                presenter.resetPassword(edtOtp.text.toString().trim(), edtPassword.text.toString().trim(), edtRePassword.text.toString().trim())
            }
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        timer = null
        super.onDestroy()
    }
}