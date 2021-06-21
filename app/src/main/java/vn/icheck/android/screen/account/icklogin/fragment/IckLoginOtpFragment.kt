package vn.icheck.android.screen.account.icklogin.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.databinding.FragmentIckOtpLoginBinding
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.screen.account.icklogin.FORGOT_PW
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.account.icklogin.LOGIN_OTP
import vn.icheck.android.screen.account.icklogin.REGISTER
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.WidgetUtils

class IckLoginOtpFragment : BaseFragmentMVVM() {
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
                binding.textView22.setText(R.string.dang_nhap_bang_so_dien_thoai)
                binding.textView25.setText(R.string.so_dien_thoai_cua_ban_duoc_su_dung_de_dang_nhap_tai_khoan_icheck)
                binding.btnBack.visibility = View.VISIBLE
            }
            FORGOT_PW -> {
                binding.textView22.setText(R.string.so_dien_thoai_dat_lai_mat_khau)
                binding.textView25.setText(R.string.vui_long_nhap_so_dien_thoai_cua_ban_de_dat_lai_mat_khau_tai_khoan_icheck)
                binding.btnBack.visibility = View.VISIBLE
            }
            REGISTER -> {
                TrackingAllHelper.trackSignupStart()
                binding.textView22.setText(R.string.dang_ky_tai_khoan)
//                binding.textView25.text = "Vui lòng nhập số điện thoại của bạn để đăng ký tài khoản iCheck"
                binding.btnBack.visibility = View.INVISIBLE
                binding.edtPassword.visibility = View.VISIBLE
                binding.edtRePassword.visibility = View.VISIBLE
            }
        }
        binding.edtPhone.apply {
            setHintTextColor(Constant.getDisableTextColor(requireContext()))
            addTextChangedListener(object : AfterTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    ickLoginViewModel.cPhone = s?.trim().toString()
                    validate()
                }
            })

        }
        binding.edtPassword.apply {
            setHintTextColor(Constant.getDisableTextColor(requireContext()))
            addTextChangedListener(object : AfterTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    ickLoginViewModel.cPw = s?.trim().toString()
                    validate()
                }
            })
        }
        binding.edtRePassword.apply {
            setHintTextColor(Constant.getDisableTextColor(requireContext()))
            addTextChangedListener(object : AfterTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    ickLoginViewModel.cRPw = s?.trim().toString()
                    validate()
                }
            })
        }
        binding.btnContinue.setOnClickListener {
            val phone = if (binding.edtPhone.text?.length == 9) "0${binding.edtPhone.text}" else binding.edtPhone.text
            when {
                phone.toString().trim().isEmpty() -> {
                    binding.edtPhone.apply {
                        error = context.getString(R.string.vui_long_nhap_du_lieu)
                        requestFocus()
                    }
                }
                !phone.toString().isPhoneNumber() -> {
                    binding.edtPhone.apply {
                        error = context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
                        requestFocus()
                    }
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
                                        requireContext().showShortErrorToast(msg)
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
                                    requireContext().showShortErrorToast(getString(R.string.da_ton_tai_yeu_cau_thay_doi_mat_khau))
                                } else {
                                    it?.message?.let { msg ->
                                        requireContext().showShortErrorToast(msg)
                                    }
                                }
                            }
                        }
                        REGISTER -> {
                            when {
                                binding.edtPassword.text?.trim().isNullOrEmpty() -> {
                                    binding.edtPassword.apply {
                                        error = context.getString(R.string.ban_chua_nhap_mat_khau)
                                        requestFocus()
                                    }
                                }
                                binding.edtPassword.text?.length ?: 0 < 6 -> {
                                    binding.edtPassword.apply {
                                        error = context.getString(R.string.mat_khau_phai_lon_hon_hoac_bang_6_ki_tu)
                                        requestFocus()
                                    }
                                }
                                binding.edtRePassword.text?.trim().isNullOrEmpty() -> {
                                    binding.edtRePassword.apply {
                                        error = context.getString(R.string.vui_long_nhap_du_lieu)
                                        requestFocus()
                                    }
                                }
                                binding.edtRePassword.text?.length ?: 0 < 6 -> {
                                    binding.edtRePassword.apply {
                                        error = context.getString(R.string.mat_khau_phai_lon_hon_hoac_bang_6_ki_tu)
                                        requestFocus()
                                    }
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
                                                    requireContext().showShortErrorToast(msg)
                                                }
                                            }
                                        }
                                    } else {
//                        showError("Xác nhận mật khẩu không trùng khớp")
                                        dismissLoadingScreen()
                                        binding.edtRePassword.apply {
                                            requestFocus()
                                            setSelection(binding.edtRePassword.text?.length ?: 0)
                                            error = context.getString(R.string.xac_nhan_mat_khau_khong_trung_khop)
                                        }
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
        binding.edtPassword.setCenterView(binding.btnKeyboard)

        binding.btnKeyboard.setOnClickListener {
            WidgetUtils.changePasswordInput(binding.edtPassword)
        }

        binding.edtRePassword.setOnFocusChangeListener { _, _ ->
            WidgetUtils.setButtonKeyboardMargin(binding.btnKeyboardNew, binding.edtRePassword)
        }
        binding.edtRePassword.setCenterView(binding.btnKeyboardNew)

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