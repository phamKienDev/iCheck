package vn.icheck.android.screen.account.verifyforgetpasswordphone

import kotlinx.android.synthetic.main.activity_verify_forget_password_phone.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.account.resetpassword.ResetPasswordActivity
import vn.icheck.android.screen.account.verifyforgetpasswordphone.presenter.VerifyPhonePresenter
import vn.icheck.android.screen.account.verifyforgetpasswordphone.view.IVerifyPhoneView
import vn.icheck.android.util.KeyboardUtils

/**
 * Created by VuLCL on 9/18/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class VerifyForgetPasswordPhoneActivity : BaseActivity<VerifyPhonePresenter>(), IVerifyPhoneView {

    override val getLayoutID: Int
        get() = R.layout.activity_verify_forget_password_phone

    override val getPresenter: VerifyPhonePresenter
        get() = VerifyPhonePresenter(this)

    override fun onInitView() {
        setupTheme()
        setupToolbar()
        initListener()
        presenter.getData(intent)
        KeyboardUtils.showSoftInput(edtPhone)
    }

    private fun setupTheme() {
//        val color = SettingManager.getTheme?.color ?: return
//
//        val setupTheme = SetupThemeHelper()
//
//        setupTheme.setLayoutBackground(FileHelper.loginBackground, color.background_color, imgBackground, layoutContainer)
//
//        setupTheme.setIconColor(color.icon_color, arrayOf(imgBack))
//        setupTheme.setTextColor(color.primary, arrayOf(txtTitle))
//
//        setupTheme.setInputHintColor(color.text_holder, arrayOf(edtPhone))
//        setupTheme.setInputTextColor(color.text_primary, arrayOf(edtPhone))
//        setupTheme.setInputErrorColor(color.error, arrayOf(layoutInputPhone))
//        setupTheme.setInputLineColor(color.input_border, arrayOf(edtPhone))
//        setupTheme.setDrawableLeftColor(color.icon_color, arrayOf(edtPhone), arrayOf(R.drawable.ic_baseline_phone_24))
//        setupTheme.setIconColor(color.icon_color_secondary, arrayOf(imgLogo))
//
//        setupTheme.setTextColor(color.button_text_color, arrayOf(btnConfirm))
//        setupTheme.setBackgroundBlueCorner(color.button_background_color, arrayOf(btnConfirm))
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.quen_mat_khau)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initListener() {
        btnConfirm.setOnClickListener {
            presenter.validPhone(edtPhone.text.toString().trim())
        }
    }

    override fun onSetPhone(phone: String) {
        edtPhone.setText(phone)
    }

    override fun onErrorPhone(errorMessage: String) {
        layoutInputPhone.error = errorMessage
    }

    override fun onNoInternet() {
        DialogHelper.showConfirm(this@VerifyForgetPasswordPhoneActivity, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, object : ConfirmDialogListener {
            override fun onDisagree() {

            }

            override fun onAgree() {
                presenter.validPhone(edtPhone.text.toString().trim())
            }
        })
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onResetPassword() {
        startActivityAndFinish<ResetPasswordActivity, String>(Constant.DATA_1, edtPhone.text.toString().trim())
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)
        showShortError(errorMessage)
    }
}