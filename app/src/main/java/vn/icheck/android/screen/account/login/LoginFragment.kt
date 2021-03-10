package vn.icheck.android.screen.account.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.base.ICNetworkManager2
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.screen.account.registeruser.register.RegisterUserActivity
import vn.icheck.android.screen.account.verifyfacebookphone.VerifyFacebookPhoneActivity
import vn.icheck.android.screen.account.verifyforgetpasswordphone.VerifyForgetPasswordPhoneActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.screen.user.welcome.WelcomeActivity
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class LoginFragment : BaseFragmentMVVM(), View.OnClickListener {
    private var callbackManager: CallbackManager? = null

    private val requestRegisterFacebookPhone = 1
    private val requestRegisterAccount = 2

    private lateinit var viewModel: LoginViewModel

    companion object {
        fun newInstance(data: String?, phone: String?): LoginFragment {
            val fragment = LoginFragment()

            val bundle = Bundle()
            if (data != null) {
                bundle.putString(Constant.DATA_1, data)
            }

            if (phone != null) {
                bundle.putString(Constant.DATA_2, phone)
            }
            fragment.arguments = bundle

            return fragment
        }
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setupTheme()
        setupToolbar()
        initLoginFacebook()
        initListener()
        removeOtherFragments()
        checkBundleData()

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
//        setupTheme.setTextColor(color.primary, arrayOf(txtTitle, tvRegister, txtForgetPassword, txtProblem, txtContent))
//        setupTheme.setBackgroundColor(color.primary, arrayOf(viewLeft, viewRight))
//
//        setupTheme.setInputHintColor(color.text_holder, arrayOf(edtPhone, edtPassword))
//        setupTheme.setInputTextColor(color.text_primary, arrayOf(edtPhone, edtPassword))
//        setupTheme.setInputErrorColor(color.error, arrayOf(layoutInputPhone, layoutInputPassword))
//        setupTheme.setInputLineColor(color.input_border, arrayOf(edtPhone, edtPassword))
//        setupTheme.setDrawableLeftColor(color.icon_color, arrayOf(edtPhone, edtPassword), arrayOf(R.drawable.ic_account_blue_18, R.drawable.ic_password_unlock_blue_18))
//        setupTheme.setIconColor(color.icon_color_secondary, arrayOf(imgLogo, imgShowOrHidePassword))
//
//        setupTheme.setTextColor(color.button_text_color, arrayOf(btnLogin))
//        setupTheme.setBackgroundBlueCorner(color.button_background_color, arrayOf(btnLogin))
    }

    private fun setupToolbar() {
        val layoutParam = layoutToolbar.layoutParams as ConstraintLayout.LayoutParams
        layoutParam.setMargins(0, getStatusBarHeight, 0, 0)
        layoutToolbar.layoutParams = layoutParam

        txtTitle.text = getString(R.string.dang_nhap)

        if (WelcomeActivity.isWelcome) {
            WelcomeActivity.isWelcome = false
            txtSkip.visibility = View.VISIBLE
            imgBack.visibility = View.GONE

            txtSkip.setOnClickListener {
                startActivity<HomeActivity>()
            }
        } else {
            imgBack.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    private fun initLoginFacebook() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val params = Bundle()
                params.putString("fields", "name,picture.type(large)")

                GraphRequest(loginResult.accessToken, "me", params, HttpMethod.GET, GraphRequest.Callback { response ->
                    val json = response?.jsonObject

                    if (json != null) {
                        val name = if (json.has("name")) {
                            json.getString("name")
                        } else {
                            null
                        }

                        val avatar = if (json.has("picture")) {
                            json.getJSONObject("picture").getJSONObject("data").getString("url")
                        } else {
                            null
                        }

                        viewModel.loginFacebook(loginResult.accessToken.token, name, avatar)
                    } else {
                        viewModel.loginFacebook(loginResult.accessToken.token, null, null)
                    }
                }).executeAsync()
            }

            override fun onCancel() {}

            override fun onError(exception: FacebookException) {
                showLongError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun initListener() {
        viewModel.errorPhone.observe(viewLifecycleOwner, Observer {
            layoutInputPhone.error = it
        })

        viewModel.errorPassword.observe(viewLifecycleOwner, Observer {
            layoutInputPassword.error = it
        })

        viewModel.statusCode.observe(viewLifecycleOwner, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(context, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {

                        }

                        override fun onAgree() {
                            viewModel.loginUser(edtPhone.text.toString(), edtPassword.text.toString())
                        }
                    })
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                ICMessageEvent.Type.ON_REGISTER_FACEBOOK_PHONE -> {
                    val intent = Intent(context, VerifyFacebookPhoneActivity::class.java)
                    intent.putExtra(Constant.DATA_1, viewModel.facebookToken)
                    viewModel.name?.let {
                        intent.putExtra(Constant.DATA_2, it)
                    }
                    viewModel.avatar?.let {
                        intent.putExtra(Constant.DATA_3, it)
                    }
                    ActivityHelper.startActivityForResult(this@LoginFragment, intent, requestRegisterFacebookPhone)
                }
                ICMessageEvent.Type.ON_LOGIN_SUCCESS -> {
                    activity?.setResult(Activity.RESULT_OK)
                    activity?.finish()
                    activity?.overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
                    ICNetworkManager2.getLoginProtocol?.onLogin()
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
                }
                else -> {
                }
            }
        })

        imgShowOrHidePassword.setOnClickListener(this)
        WidgetUtils.setClickListener(this, txtForgetPassword, txtProblem, btnLogin, btnFacebook, tvRegister)
    }

    private fun checkBundleData() {
        when (arguments?.get(Constant.DATA_1)) {
            Constant.REGISTER_TYPE -> {
                tvRegister.performClick()
            }
            Constant.LOGIN_FACEBOOK_TYPE -> {
                btnFacebook.performClick()
            }
        }

        edtPhone.setText(arguments?.getString(Constant.DATA_2))
    }

    override fun onClick(view: View?) {
        view?.let {
            KeyboardUtils.hideSoftInput(it)

            when (it.id) {
                R.id.txtForgetPassword -> {
                    startActivity<VerifyForgetPasswordPhoneActivity>(Constant.DATA_1, edtPhone.text.toString().trim())
                }
                R.id.txtProblem -> {
                    val supportLinks = SettingManager.clientSetting?.support_links

                    if (!supportLinks.isNullOrEmpty()) {
                        for (link in supportLinks) {
                            if (link.key == "auth_help") {
                                WebViewActivity.start(activity, link.link)
                                return
                            }
                        }
                    }

                    showShortWarning(R.string.vui_long_thu_lai_sau)
                }
                R.id.imgShowOrHidePassword -> {
                    WidgetUtils.showOrHidePassword(edtPassword, imgShowOrHidePassword)
                }
                R.id.btnLogin -> {
                    viewModel.loginUser(edtPhone.text.toString(), edtPassword.text.toString())
//                    presenter.loginUser(edtPhone.text.toString(), edtPassword.text.toString())
                }
                R.id.btnFacebook -> {
                    LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
                }
                R.id.tvRegister -> {
                    startActivityForResult<RegisterUserActivity>(requestRegisterAccount)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestRegisterAccount -> {
                    edtPhone.setText(data?.getStringExtra(Constant.DATA_1))
                }
                requestRegisterFacebookPhone -> {
                    viewModel.onLoginSuccess()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            LoginManager.getInstance().unregisterCallback(callbackManager)
            callbackManager = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}