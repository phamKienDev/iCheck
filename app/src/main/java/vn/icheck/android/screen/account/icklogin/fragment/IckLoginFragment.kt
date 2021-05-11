package vn.icheck.android.screen.account.icklogin.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.fragment.CoroutineFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.databinding.FragmentIckLoginBinding
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.RelationshipHelper
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.helper.ShareSessionToModule
import vn.icheck.android.ichecklibs.visibleOrGone
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEvent
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEventListener
import vn.icheck.android.lib.keyboard.Unregistrar
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICRelationshipsInformation
import vn.icheck.android.network.models.ICSessionData
import vn.icheck.android.screen.account.icklogin.FORGOT_PW
import vn.icheck.android.screen.account.icklogin.LOGIN_OTP
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.*
import javax.inject.Inject

@AndroidEntryPoint
class IckLoginFragment : CoroutineFragment() {
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()

    @Inject
    lateinit var callbackManager: CallbackManager

    @Inject
    lateinit var loginManager: LoginManager

    lateinit var binding: FragmentIckLoginBinding

    private var unregistrar: Unregistrar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIckLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        unregistrar = KeyboardVisibilityEvent.registerEventListener(requireActivity(), object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                binding.btnKeyboard.visibleOrGone(isOpen && binding.edtPassword.isFocused)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        unregistrar?.unregister()
        unregistrar = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.disable()
        TrackingAllHelper.trackLoginStart()
        binding.edtPhone.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().startsWith("84")) {
//                    binding.edtPhone.setText(s.toString().replace("84","0", true))
//                    binding.edtPhone.setSelection(1)
//                }
                if (s.toString().isNotEmpty()) {
                    binding.edtPhone.setPadding(24.toPx(), 0, 0, 20.toPx())
                } else {
                    binding.edtPhone.setPadding(0, 0, 0, 20.toPx())
                }
                checkForm()
            }
        })
        binding.edtPassword.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                checkForm()
            }
        })
        binding.btnLoginFacebook.setOnSingleClickListener {
            loginManager.logInWithReadPermissions(this, listOf("public_profile"))
        }
        binding.btnLogin.setOnSingleClickListener {
            when {
                binding.edtPhone.text?.trim().toString().isPhoneNumber() -> {
                    when {
                        binding.edtPassword.text.isNullOrEmpty() -> {
                            binding.edtPassword.requestFocus()
                            binding.edtPassword.setSelection(binding.edtPassword.text?.toString()?.length
                                    ?: 0)
                            binding.edtPassword.setError("Bạn chưa nhập mật khẩu")
                        }
                        binding.edtPassword.text?.toString()?.length ?: 0 < 6 -> {
                            binding.edtPassword.requestFocus()
                            binding.edtPassword.setSelection(binding.edtPassword.text?.toString()?.length
                                    ?: 0)
                            binding.edtPassword.setError("Mật khẩu phải lớn hơn hoặc bằng 6 kí tự")
                        }
                        else -> {
                            login()
                        }
                    }

                }
                else -> {
                    showFocus()
                    binding.edtPhone.setError("Số điện thoại không đúng định dạng")
                }

            }
        }
        binding.btnLoginOtp.setOnSingleClickListener {
            requireActivity().forceHideKeyboard()
            val action = IckLoginFragmentDirections.actionIckLoginFragmentToIckLoginOtpFragment(LOGIN_OTP)
            findNavController().navigate(action)
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
                    WebViewActivity.start(requireActivity(), list?.firstOrNull()?.value, null, "Hỗ trợ đăng nhập")
                }
            })
//            WebViewActivity.start(requireActivity(), "http://quotes.icheck.com.vn/van-de-khi-dang-nhap/")
        }
        ickLoginViewModel.stateRegister.observe(viewLifecycleOwner, {
            if (it == 1) {
                binding.edtPhone.setText("")
                binding.edtPassword.setText("")
            }
        })

        setupListener()
    }

    private fun setupListener() {
        binding.edtPassword.setOnFocusChangeListener { v, hasFocus ->
            binding.btnKeyboard.visibleOrGone(hasFocus)
        }

        binding.btnKeyboard.setOnClickListener {
            binding.edtPassword.apply {
                inputType = if (inputType != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
//                    binding.btnKeyboard.setText(R.string.ban_phim_so)
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                } else {
//                    binding.btnKeyboard.setText(R.string.ban_phim_chu)
                    InputType.TYPE_CLASS_NUMBER
                }
                transformationMethod = PasswordTransformationMethod()
                setSelection(length())
            }
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

                            PageRepository().getRelationshipCurrentUser(object : ICNewApiListener<ICResponse<ICRelationshipsInformation>> {
                                override fun onSuccess(obj: ICResponse<ICRelationshipsInformation>) {
                                    if (obj.data != null) {
                                        RelationshipHelper.saveData(obj.data!!)
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {

                                }
                            })

                            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
                            requireActivity().finish()
                        }
                    })
                } else {
                    showFocus()
                    showError(loginRes?.msg)
                }
            })
        } catch (e: Exception) {
            showFocus()
            showError(e.localizedMessage)
            dismissLoadingScreen()
        }
    }

    private fun showFocus() {
        binding.edtPhone.requestFocus()
        binding.edtPhone.setSelection(binding.edtPhone.text?.toString()?.length ?: 0)
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