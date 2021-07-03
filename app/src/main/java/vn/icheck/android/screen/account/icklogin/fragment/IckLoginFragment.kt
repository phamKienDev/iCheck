package vn.icheck.android.screen.account.icklogin.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.databinding.FragmentIckLoginBinding
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.helper.ShareSessionToModule
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.dpToPx
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICSessionData
import vn.icheck.android.screen.account.icklogin.FORGOT_PW
import vn.icheck.android.screen.account.icklogin.LOGIN_OTP
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.WidgetUtils
import javax.inject.Inject

@AndroidEntryPoint
class  IckLoginFragment : BaseFragmentMVVM() {
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()

    @Inject
    lateinit var callbackManager: CallbackManager

    @Inject
    lateinit var loginManager: LoginManager

    lateinit var binding: FragmentIckLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIckLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        TrackingAllHelper.trackLoginStart()
        binding.edtPhone.apply {
            setHintTextColor(ColorManager.getDisableTextColor(context))
            addTextChangedListener(object : AfterTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
//                if (s.toString().startsWith("84")) {
//                    binding.edtPhone.setText(s.toString().replace("84","0", true))
//                    binding.edtPhone.setSelection(1)
//                }
                    if (s.toString().isNotEmpty()) {
                        binding.edtPhone.setPadding(24.dpToPx(), 0, 0, 20.dpToPx())
                    } else {
                        binding.edtPhone.setPadding(0, 0, 0, 20.dpToPx())
                    }
                    checkForm()
                }
            })
        }
        binding.edtPassword.apply {
            setHintTextColor(ColorManager.getDisableTextColor(context))
            addTextChangedListener(object : AfterTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    checkForm()
                }
            })
        }
        binding.btnLoginFacebook.setOnSingleClickListener {
            loginManager.logInWithReadPermissions(this, listOf("public_profile"))
        }
        binding.btnLogin.setOnSingleClickListener {
            when {
                binding.edtPhone.text?.trim().toString().isPhoneNumber() -> {
                    when {
                        binding.edtPassword.text.isNullOrEmpty() -> {
                            binding.edtPassword.apply {
                                error = context.getString(R.string.ban_chua_nhap_mat_khau)
                                showFocus(this)
                            }
                        }
                        binding.edtPassword.text?.toString()?.length ?: 0 < 6 -> {
                            binding.edtPassword.apply {
                                error = context.getString(R.string.mat_khau_phai_lon_hon_hoac_bang_6_ki_tu)
                                showFocus(this)
                            }
                        }
                        else -> {
                            login()
                        }
                    }

                }
                else -> {
                    binding.edtPhone.error = getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
                    showFocus(binding.edtPhone)
                }

            }
        }
        binding.btnLoginOtp.apply {
            background = ViewHelper.bgWhiteStrokePrimary1Corners4(context)
            setOnSingleClickListener {
                requireActivity().forceHideKeyboard()
                val action = IckLoginFragmentDirections.actionIckLoginFragmentToIckLoginOtpFragment(LOGIN_OTP)
                findNavController().navigate(action)
            }
        }
        binding.btnForgotPw.setOnSingleClickListener {
            requireActivity().forceHideKeyboard()
            ickLoginViewModel.hideLoginRegister()
            val action = IckLoginFragmentDirections.actionIckLoginFragmentToIckLoginOtpFragment(FORGOT_PW)
            findNavController().navigate(action)
        }
        binding.tvProblem.setOnSingleClickListener {
            SettingHelper.getSystemSetting("app-login.support-url", "app-login", object : ISettingListener {
                override fun onRequestError(error: String) {

                }

                override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                    WebViewActivity.start(requireActivity(), list?.firstOrNull()?.value, null, it.context.getString(R.string.ho_tro_dang_nhap))
                }
            })
//            WebViewActivity.start(requireActivity(), "http://quotes.icheck.com.vn/van-de-khi-dang-nhap/")
        }
        ickLoginViewModel.stateRegister.observe(viewLifecycleOwner, Observer {
            if (it == 1) {
                binding.edtPhone.setText("")
                binding.edtPassword.setText("")
            }
        })

        setupListener()
    }

    private fun setupView() {
        binding.btnLogin.disable()
        binding.btnLoginFacebook.background = ViewHelper.bgBtnFacebook(requireContext())
        binding.edtPassword.setHintTextColor(ColorManager.getDisableTextColor(requireContext()))
    }

    private fun setupListener() {
        binding.edtPassword.setOnFocusChangeListener { _, _ ->
            WidgetUtils.setButtonKeyboardMargin(binding.btnKeyboard, binding.edtPassword)
            WidgetUtils.setButtonKeyboardMargin(binding.btnKeyboard, binding.edtPassword)
        }
        binding.edtPassword.setCenterView(binding.btnKeyboard)

        binding.btnKeyboard.setOnClickListener {
            WidgetUtils.changePasswordInput(binding.edtPassword)
        }
    }

    private fun login() {
        showLoadingTimeOut(30000)
        try {
            ickLoginViewModel.login(binding.edtPhone.text.toString(), binding.edtPassword.text.toString()).observe(viewLifecycleOwner, Observer { loginRes ->
                dismissLoadingScreen()

                if (loginRes?.data?.token != null) {
                    TrackingAllHelper.trackLoginSuccess()

                    ickLoginViewModel.getUserInfo().observe(viewLifecycleOwner, Observer {
                        it?.data?.let { userInfoRes ->
                            loginRes.data?.user = userInfoRes.createICUser()
                            SessionManager.session = loginRes.data ?: ICSessionData()
                            ShareSessionToModule.setSession(loginRes.data ?: ICSessionData())
                            CartHelper().getCartSocial()
                            InsiderHelper.onLogin()

                            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
                            requireActivity().finish()
                        }
                    })
                } else {
                    showFocus(binding.edtPhone)
                    requireContext().showShortErrorToast(loginRes?.msg)
                }
            })
        } catch (e: Exception) {
            showFocus(binding.edtPhone)
            requireContext().showShortErrorToast(e.localizedMessage)
            dismissLoadingScreen()
        }
    }

    private fun showFocus(editText: AppCompatEditText) {
        editText.requestFocus()
        editText.setSelection(editText.text?.length ?: 0)
    }

    private fun checkForm() {
        //&& binding.edtPassword.text?.length?:0 >= 6
        if (binding.edtPhone.text?.length ?: 0 >= 1) {
            binding.btnLogin.enable()
        } else {
            binding.btnLogin.disable()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}