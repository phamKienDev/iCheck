package vn.icheck.android.screen.account.icklogin.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import vn.icheck.android.base.fragment.CoroutineFragment
import vn.icheck.android.databinding.FragmentIckOtpLoginBinding
import vn.icheck.android.ichecklibs.util.visibleOrGone
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEvent
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEventListener
import vn.icheck.android.lib.keyboard.Unregistrar
import vn.icheck.android.screen.account.icklogin.FORGOT_PW
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.account.icklogin.LOGIN_OTP
import vn.icheck.android.screen.account.icklogin.REGISTER
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.WidgetUtils

class IckLoginOtpFragment : CoroutineFragment() {
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()
    private val args: IckLoginOtpFragmentArgs by navArgs()
    lateinit var binding: FragmentIckOtpLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIckOtpLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableContinue()
        ickLoginViewModel.registerType = args.loginType
        when (args.loginType) {
            LOGIN_OTP -> {
                binding.textView22.text = "Đăng nhập bằng số điện thoại"
                binding.textView25.text = "Số điện thoại của bạn được sử dụng để đăng nhập tài khoản iCheck"
                binding.btnBack.visibility = View.VISIBLE
            }
            FORGOT_PW -> {
                binding.textView22.text = "Số điện thoại đặt lại mật khẩu"
                binding.textView25.text = "Vui lòng nhập số điện thoại của bạn để đặt lại mật khẩu tài khoản iCheck"
                binding.btnBack.visibility = View.VISIBLE
            }
            REGISTER -> {
                TrackingAllHelper.trackSignupStart()
                binding.textView22.text = "Đăng ký tài khoản"
//                binding.textView25.text = "Vui lòng nhập số điện thoại của bạn để đăng ký tài khoản iCheck"
                binding.btnBack.visibility = View.INVISIBLE
                binding.edtPassword.visibility = View.VISIBLE
                binding.edtRePassword.visibility = View.VISIBLE
            }
        }
        binding.edtPhone.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickLoginViewModel.cPhone = s?.trim().toString()
                validate()
            }
        })
        binding.edtPassword.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickLoginViewModel.cPw = s?.trim().toString()
                validate()
            }
        })
        binding.edtRePassword.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickLoginViewModel.cRPw = s?.trim().toString()
                validate()
            }
        })
        binding.btnContinue.setOnClickListener {
            val phone = if (binding.edtPhone.text?.length == 9) "0${binding.edtPhone.text}" else binding.edtPhone.text
            when {
                phone.toString().trim().isEmpty() -> {
                    binding.edtPhone.setError("Vui lòng nhập dữ liệu!")
                    binding.edtPhone.requestFocus()
                }
                !phone.toString().isPhoneNumber() -> {
                    binding.edtPhone.setError("Số điện thoại không đúng định dạng")
                    binding.edtPhone.requestFocus()
                }

                phone.toString().isPhoneNumber() -> {

                    when (args.loginType) {
                        LOGIN_OTP -> {
                            showLoadingTimeOut(5000)
                            ickLoginViewModel.requestLoginOtp().observe(viewLifecycleOwner) {
                                dismissLoadingScreen()
                                if (it?.statusCode == "200") {
                                    TrackingAllHelper.trackLoginSuccess()
                                    val action = IckLoginOtpFragmentDirections
                                            .actionIckLoginOtpFragmentToIckOtpFragment(
                                                    it.data?.token, binding.edtPhone.text.toString(), args.loginType, null, null
                                            )
                                    findNavController().navigate(action)
                                } else {
                                    it?.message?.let { msg ->
                                        requireContext().showSimpleErrorToast(msg)
                                    }
                                }
                            }
                        }
                        FORGOT_PW -> {
                            showLoadingTimeOut(5000)
                            ickLoginViewModel.requestForgotPw().observe(viewLifecycleOwner) {
                                dismissLoadingScreen()
                                if (it?.statusCode == "200") {
                                    val action = IckLoginOtpFragmentDirections
                                            .actionIckLoginOtpFragmentToIckOtpFragment(
                                                    it.data?.token, binding.edtPhone.text.toString(), args.loginType, null, null
                                            )
                                    findNavController().navigate(action)
                                } else if (it?.statusCode == "U3018") {
                                    showError("Đã tồn tại yêu cầu thay đổi mật khẩu")
                                } else {
                                    it?.message?.let { msg ->
                                        requireContext().showSimpleErrorToast(msg)
                                    }
                                }
                            }
                        }
                        REGISTER -> {
                            when {
                                binding.edtPassword.text?.trim().isNullOrEmpty() -> {
                                    binding.edtPassword.setError("Bạn chưa nhập mật khẩu")
                                    binding.edtPassword.requestFocus()
                                }
                                binding.edtPassword.text?.length ?: 0 < 6 -> {
                                    binding.edtPassword.setError("Mật khẩu phải lớn hơn hoặc bằng 6 kí tự")
                                    binding.edtPassword.requestFocus()
                                }
                                binding.edtRePassword.text?.trim().isNullOrEmpty() -> {
                                    binding.edtRePassword.setError("Vui lòng nhập dữ liệu")
                                    binding.edtRePassword.requestFocus()
                                }
                                binding.edtRePassword.text?.length ?: 0 < 6 -> {
                                    binding.edtRePassword.setError("Mật khẩu phải lớn hơn hoặc bằng 6 kí tự")
                                    binding.edtRePassword.requestFocus()
                                }
                                else -> {
                                    if (binding.edtPassword.text.toString() == binding.edtRePassword.text.toString()) {
                                        showLoadingTimeOut(5000)
                                        ickLoginViewModel.password = binding.edtPassword.text.toString()
                                        ickLoginViewModel.requestRegisterOtp(binding.edtPassword.text.toString(), binding.edtRePassword.text.toString()).observe(viewLifecycleOwner) {
                                            dismissLoadingScreen()
                                            if (it?.statusCode == "200") {
                                                val action = IckLoginOtpFragmentDirections
                                                        .actionIckLoginOtpFragmentToIckOtpFragment(
                                                                it.data?.token, binding.edtPhone.text.toString(), args.loginType, null, null
                                                        )
                                                findNavController().navigate(action)
                                            } else {
                                                it?.message?.let { msg ->
                                                    requireContext().showSimpleErrorToast(msg)
                                                }
                                            }
                                        }
                                    } else {
//                        showError("Xác nhận mật khẩu không trùng khớp")
                                        dismissLoadingScreen()
                                        binding.edtRePassword.requestFocus()
                                        binding.edtRePassword.setSelection(binding.edtRePassword.text?.length
                                                ?: 0)
                                        binding.edtRePassword.setError("Xác nhận mật khẩu không trùng khớp")
                                        requireActivity().forceShowKeyboard()
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        binding.btnBack.setOnClickListener {
            if (args.loginType == FORGOT_PW) {
                ickLoginViewModel.showLoginRegister()
            }
            findNavController().popBackStack()
        }
        ickLoginViewModel.nationLiveData.observe(viewLifecycleOwner) {
            binding.tvPhoneHead simpleText it.dialCode
            binding.tvNation simpleText it.name
        }

        setupListener()
    }

    private fun setupListener() {
        binding.edtPassword.setOnFocusChangeListener { _, _ ->
            WidgetUtils.setButtonKeyboardMargin(binding.btnKeyboard, binding.edtPassword)
        }

        binding.btnKeyboard.setOnClickListener {
            WidgetUtils.changePasswordInput(binding.edtPassword)
        }

        binding.edtRePassword.setOnFocusChangeListener { _, _ ->
            WidgetUtils.setButtonKeyboardMargin(binding.btnKeyboardNew, binding.edtRePassword)
        }

        binding.btnKeyboardNew.setOnClickListener {
            WidgetUtils.changePasswordInput(binding.edtRePassword)
        }
    }

    private fun validate() {
        if (args.loginType != REGISTER) {
            when {
//                binding.edtPhone.text?.length == 10 && binding.edtPhone.text.toString().startsWith("0") -> {
//                    enableContinue()
//                }
                binding.edtPhone.text?.length ?: 0 > 0 -> {
                    enableContinue()
                }
                else -> disableContinue()
            }
        } else {
            when {
//                binding.edtPhone.text?.length == 10 && binding.edtPhone.text.toString().startsWith("0") -> {
//                    if (binding.edtRePassword.text?.length!! >= 6 && binding.edtPassword.text?.length!! >= 6) {
//                        enableContinue()
//                    } else {
//                        disableContinue()
//                    }
//                }
                binding.edtPhone.text?.length ?: 0 > 0 -> {
                    enableContinue()
                }
                binding.edtPassword.text?.length ?: 0 > 0 -> {
                    enableContinue()
                }
                binding.edtRePassword.text?.length ?: 0 > 0 -> {
                    enableContinue()
                }
                else -> disableContinue()
            }
        }
    }

    private fun disableContinue() {
        binding.btnContinue.disable()
    }

    private fun enableContinue() {
        binding.btnContinue.enable()
        (activity as IckLoginActivity).ickLoginViewModel.setPhone(binding.edtPhone.text.toString())
    }
}