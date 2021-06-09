package vn.icheck.android.screen.account.registeruser.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.toolbar_account.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEvent
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEventListener
import vn.icheck.android.lib.keyboard.Unregistrar
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.account.registeruser.inputotp.RegisterUserOtpActivity
import vn.icheck.android.screen.account.registeruser.register.presetner.RegisterUserPresenter
import vn.icheck.android.screen.account.registeruser.register.view.IRegisterUserView
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class RegisterUserActivity : BaseActivityMVVM(), IRegisterUserView, View.OnClickListener {
    private var unregister: Unregistrar? = null

    private var keyboardHeight = 0

    private val requestRegister = 1

    private val presenter = RegisterUserPresenter(this@RegisterUserActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register)

        onInitView()
    }

    fun onInitView() {
        setupTheme()
        initToolbar()
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
//        setupTheme.setTextColor(color.primary, arrayOf(txtLeft, txtTitle, txtRight))
//
//        setupTheme.setInputHintColor(color.text_holder, arrayOf(edtPhone, edtLastName, edtFirstName, edtPassword, edtRePassword))
//        setupTheme.setInputTextColor(color.text_primary, arrayOf(edtPhone, edtLastName, edtFirstName, edtPassword, edtRePassword))
//        setupTheme.setInputErrorColor(color.error, arrayOf(layoutInputPhone, layoutInputLastName, layoutInputFirstName, layoutInputPassword, layoutInputRePassword))
//        setupTheme.setInputLineColor(color.input_border, arrayOf(edtPhone, edtLastName, edtFirstName, edtPassword, edtRePassword))
//        setupTheme.setDrawableLeftColor(color.icon_color, arrayOf(edtPhone, edtLastName, edtFirstName, edtPassword, edtRePassword), arrayOf(R.drawable.ic_baseline_phone_24, R.drawable.ic_account_blue_18, R.drawable.ic_account_blue_18, R.drawable.ic_password_unlock_blue_18, R.drawable.ic_password_unlock_blue_18))
//        setupTheme.setIconColor(color.icon_color_secondary, arrayOf(imgIcon, imgShowOrHidePassword, imgShowOrHideRePassword))
//
//        setupTheme.setTextColor(color.button_text_color, arrayOf(btnRegister))
//        setupTheme.setBackgroundBlueCorner(color.button_background_color, arrayOf(btnRegister))

    }

    private fun initKeyboardListener() {
        unregister = KeyboardVisibilityEvent.registerEventListener(this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                val containerParam = layoutContent.layoutParams as FrameLayout.LayoutParams

                if (isOpen) {
                    containerParam.setMargins(0, 0, 0, keyboardHeight)
                } else {
                    containerParam.setMargins(0, 0, 0, 0)
                }

                layoutContent.layoutParams = containerParam
            }
        })
    }

    private fun initToolbar() {
        txtTitle.text = getString(R.string.dang_ky)

        txtLeft.text = getString(R.string.dang_nhap)

        txtLeft.setOnClickListener {
            if (presenter.getIsGoToLogin) {
                startActivityForResult<IckLoginActivity>(requestRegister)
            } else {
                onBackPressed()
            }
        }
    }

    private fun initListener() {
        WidgetUtils.setClickListener(this, imgShowOrHidePassword, imgShowOrHideRePassword, btnRegister)
    }

    override fun onErrorPhone(errorMessage: String) {
        layoutInputPhone.error = errorMessage
    }

    override fun onErrorLastName(errorMessage: String) {
        layoutInputLastName.error = errorMessage
    }

    override fun onErrorFirstName(errorMessage: String) {
        layoutInputFirstName.error = errorMessage
    }

//    override fun onErrorEmail(errorMessage: String) {
//        layoutInputEmail.error = errorMessage
//    }

    override fun onErrorPassword(errorMessage: String) {
        layoutInputPassword.error = errorMessage
    }

    override fun onErrorRePassword(errorMessage: String) {
        layoutInputRePassword.error = errorMessage
    }

    override fun onNoInternet() {
        DialogHelper.showConfirm(this@RegisterUserActivity, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, object : ConfirmDialogListener {
            override fun onDisagree() {

            }

            override fun onAgree() {
                presenter.registerAccount(edtPhone.text.toString(),
                        edtLastName.text.toString(),
                        edtFirstName.text.toString(),
                        edtPassword.text.toString(),
                        edtRePassword.text.toString())
            }
        })
    }

    override fun showLoading() {
        DialogHelper.showLoading(this)
    }

    override fun closeLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onCheckPhoneSuccess(json: String) {
        val intent = Intent(this, RegisterUserOtpActivity::class.java)
        intent.putExtra(Constant.DATA_1, json)
        intent.putExtra(Constant.DATA_2, presenter.getIsGoToLogin)
        startActivity(intent)
    }

    override fun showError(errorMessage: String) {
        ToastUtils.showShortError(this, errorMessage)
    }

    override val mContext: Context
        get() = this@RegisterUserActivity

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@RegisterUserActivity, isShow)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgShowOrHidePassword -> {
                WidgetUtils.showOrHidePassword(edtPassword, imgShowOrHidePassword)
            }
            R.id.imgShowOrHideRePassword -> {
                WidgetUtils.showOrHidePassword(edtRePassword, imgShowOrHideRePassword)
            }
            R.id.btnRegister -> {
                presenter.registerAccount(edtPhone.text.toString(),
                        edtLastName.text.toString(),
                        edtFirstName.text.toString(),
                        edtPassword.text.toString(),
                        edtRePassword.text.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initKeyboardListener()
    }

    override fun onPause() {
        super.onPause()
        unregister?.unregister()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestRegister) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                onBackPressed()
            }
        }
    }
}