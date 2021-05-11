package vn.icheck.android.screen.account.icklogin.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import vn.icheck.android.R
import vn.icheck.android.base.fragment.CoroutineFragment
import vn.icheck.android.databinding.FragmentIckOtpLoginBinding
import vn.icheck.android.ichecklibs.visibleOrGone
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEvent
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEventListener
import vn.icheck.android.lib.keyboard.Unregistrar
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.screen.account.icklogin.FORGOT_PW
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.account.icklogin.LOGIN_OTP
import vn.icheck.android.screen.account.icklogin.REGISTER
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.*

class IckLoginOtpFragment : CoroutineFragment() {
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()
    private val args: IckLoginOtpFragmentArgs by navArgs()
    lateinit var binding: FragmentIckOtpLoginBinding

    private var unregistrar: Unregistrar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIckOtpLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        unregistrar = KeyboardVisibilityEvent.registerEventListener(requireActivity(), object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                binding.btnKeyboard.visibleOrGone(isOpen && (binding.groupPw.isFocused || binding.groupRePw.isFocused))
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
                binding.groupPw.visibility = View.VISIBLE
                binding.groupRePw.visibility = View.VISIBLE
            }
        }
//        binding.edtPhone.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                binding.imgClear.visibility = View.VISIBLE
//                binding.divider20.background = ColorDrawable(Color.parseColor("#057DDA"))
//            } else {
//                binding.imgClear.visibility = View.INVISIBLE
//                binding.divider20.background = ColorDrawable(Color.parseColor("#F0F0F0"))
//            }
//        }
        binding.edtPhone.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().startsWith("84")) {
//                    binding.edtPhone.setText(s.toString().replace("84","0", true))
//                    binding.edtPhone.setSelection(1)
//                }
                ickLoginViewModel.cPhone = s?.trim().toString()
                validate()
            }
        })
        binding.groupPw.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickLoginViewModel.cPw = s?.trim().toString()
                validate()
            }
        })
        binding.groupRePw.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickLoginViewModel.cRPw = s?.trim().toString()
                validate()
            }
        })
//        binding.imgClear.setOnClickListener {
//            binding.edtPhone.setText("")
//        }
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
                                binding.groupPw.text?.trim().isNullOrEmpty() -> {
                                    binding.groupPw.setError("Bạn chưa nhập mật khẩu")
                                    binding.groupPw.requestFocus()
                                }
                                binding.groupPw.text?.length ?: 0 < 6 -> {
                                    binding.groupPw.setError("Mật khẩu phải lớn hơn hoặc bằng 6 kí tự")
                                    binding.groupPw.requestFocus()
                                }
                                binding.groupRePw.text?.trim().isNullOrEmpty() -> {
                                    binding.groupRePw.setError("Vui lòng nhập dữ liệu")
                                    binding.groupRePw.requestFocus()
                                }
                                binding.groupRePw.text?.length ?: 0 < 6 -> {
                                    binding.groupRePw.setError("Mật khẩu phải lớn hơn hoặc bằng 6 kí tự")
                                    binding.groupRePw.requestFocus()
                                }
                                else -> {
                                    if (binding.groupPw.text.toString() == binding.groupRePw.text.toString()) {
                                        showLoadingTimeOut(5000)
                                        ickLoginViewModel.password = binding.groupPw.text.toString()
                                        ickLoginViewModel.requestRegisterOtp(binding.groupPw.text.toString(), binding.groupRePw.text.toString()).observe(viewLifecycleOwner) {
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
                                        binding.groupRePw.requestFocus()
                                        binding.groupRePw.setSelection(binding.groupRePw.text?.length
                                                ?: 0)
                                        binding.groupRePw.setError("Xác nhận mật khẩu không trùng khớp")
                                        requireActivity().forceShowKeyboard()
                                    }
                                }
                            }

                        }
                    }
                }

            }
//            if (phone.toString().isPhoneNumber()) {
//                showLoadingTimeOut(5000)
//                when (args.loginType) {
//                    LOGIN_OTP -> {
//                        ickLoginViewModel.requestLoginOtp().observe(viewLifecycleOwner) {
//                            dismissLoadingScreen()
//                            if (it?.statusCode == "200") {
//                                InsiderHelper.loginSuccess()
//                                TekoHelper.loginSuccess()
//                                val action = IckLoginOtpFragmentDirections
//                                        .actionIckLoginOtpFragmentToIckOtpFragment(
//                                                it.data?.token, binding.edtPhone.text.toString(), args.loginType, null, null
//                                        )
//                                findNavController().navigate(action)
//                            } else {
//                                it?.message?.let { msg ->
//                                    requireContext().showSimpleErrorToast(msg)
//                                }
//                            }
//                        }
//                    }
//                    FORGOT_PW -> {
//                        ickLoginViewModel.requestForgotPw().observe(viewLifecycleOwner) {
//                            dismissLoadingScreen()
//                            if (it?.statusCode == "200") {
//                                val action = IckLoginOtpFragmentDirections
//                                        .actionIckLoginOtpFragmentToIckOtpFragment(
//                                                it.data?.token, binding.edtPhone.text.toString(), args.loginType, null, null
//                                        )
//                                findNavController().navigate(action)
//                            } else {
//                                it?.message?.let { msg ->
//                                    requireContext().showSimpleErrorToast(msg)
//                                }
//                            }
//                        }
//                    }
//                    REGISTER -> {
//                        if (binding.groupPw.text.toString() == binding.groupRePw.text.toString()) {
//                            ickLoginViewModel.requestRegisterOtp(binding.groupPw.text.toString(), binding.groupRePw.text.toString()).observe(viewLifecycleOwner) {
//                                dismissLoadingScreen()
//                                if (it?.statusCode == "200") {
//                                    val action = IckLoginOtpFragmentDirections
//                                            .actionIckLoginOtpFragmentToIckOtpFragment(
//                                                    it.data?.token, binding.edtPhone.text.toString(), args.loginType, null, null
//                                            )
//                                    findNavController().navigate(action)
//                                } else {
//                                    it?.message?.let { msg ->
//                                        requireContext().showSimpleErrorToast(msg)
//                                    }
//                                }
//                            }
//                        } else {
////                        showError("Xác nhận mật khẩu không trùng khớp")
//                            dismissLoadingScreen()
//                            binding.groupRePw.requestFocus()
//                            binding.groupRePw.setSelection(binding.groupRePw.text?.length ?: 0)
//                            binding.groupRePw.setError("Xác nhận mật khẩu không trùng khớp")
//                            requireActivity().forceShowKeyboard()
//                        }
//                    }
//                }
//            } else {
//                binding.edtPhone.setError("Số điện thoại không đúng định dạng")
//            }


        }
        binding.btnBack.setOnClickListener {
            if (args.loginType == FORGOT_PW) {
                ickLoginViewModel.showLoginRegister()
            }
            findNavController().popBackStack()
        }
//        binding.tvNation.setOnClickListener {
//            IckNationBottomDialog().show(requireActivity().supportFragmentManager, null)
//            val action = IckLoginOtpFragmentDirections.actionIckLoginOtpFragmentToIckNationBottomDialog()
//            findNavController().navigate(action)
//        }
//        binding.tvPhoneHead.setOnClickListener {
//            IckNationBottomDialog().show(requireActivity().supportFragmentManager, null)
//        }
        ickLoginViewModel.nationLiveData.observe(viewLifecycleOwner) {
            binding.tvPhoneHead simpleText it.dialCode
            binding.tvNation simpleText it.name
        }

        setupListener()
    }

    private fun setupListener() {
        binding.btnKeyboard.setOnClickListener {
            changeKeyboard(binding.groupPw)
            changeKeyboard(binding.groupRePw)
        }

        binding.groupPw.setOnFocusChangeListener { v, hasFocus ->
            checkButtonChangeKeyboard()
            checkKeyboard(binding.groupPw)
        }

        binding.groupRePw.setOnFocusChangeListener { v, hasFocus ->
            checkButtonChangeKeyboard()
            checkKeyboard(binding.groupRePw)
        }
    }

    private fun checkButtonChangeKeyboard() {
        binding.btnKeyboard.visibleOrGone(binding.groupPw.isFocused || binding.groupRePw.isFocused)
    }

    private fun changeKeyboard(view: AppCompatEditText) {
        view.apply {
            if (isFocused) {
                inputType = if (inputType != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    binding.btnKeyboard.setText(R.string.ban_phim_so)
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                } else {
                    binding.btnKeyboard.setText(R.string.ban_phim_chu)
                    InputType.TYPE_CLASS_NUMBER
                }
                transformationMethod = PasswordTransformationMethod()
                setSelection(length())
            }
        }
    }

    private fun checkKeyboard(view: AppCompatEditText) {
        view.apply {
            if (isFocused) {
                if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    binding.btnKeyboard.setText(R.string.ban_phim_so)
                } else {
                    binding.btnKeyboard.setText(R.string.ban_phim_chu)
                }
            }
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
//                    if (binding.groupRePw.text?.length!! >= 6 && binding.groupPw.text?.length!! >= 6) {
//                        enableContinue()
//                    } else {
//                        disableContinue()
//                    }
//                }
                binding.edtPhone.text?.length ?: 0 > 0 -> {
                    enableContinue()
                }
                binding.groupPw.text?.length ?: 0 > 0 -> {
                    enableContinue()
                }
                binding.groupRePw.text?.length ?: 0 > 0 -> {
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