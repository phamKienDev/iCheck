package vn.icheck.android.screen.account.icklogin.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.databinding.FragmentFillPwBinding
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.util.kotlin.WidgetUtils

class IckConfirmPasswordFragment : BaseFragmentMVVM() {
    private lateinit var binding: FragmentFillPwBinding
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()
    private val args: IckConfirmPasswordFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFillPwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.disable()
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.edtPassword.addTextChangedListener {
            validate()
        }
        binding.edtRePassword.addTextChangedListener {
            validate()
        }
        binding.btnLogin.setOnClickListener {
            when {

                binding.edtPassword.text?.trim().toString().length < 6 -> {
                    binding.edtPassword.setError("Mật khẩu phải lớn hơn hoặc bằng 6 kí tự")
                    binding.edtPassword.requestFocus()
                }
                binding.edtRePassword.text?.trim().isNullOrEmpty() -> {
                    binding.edtRePassword.setError("Vui lòng nhập dữ liệu")
                    binding.edtRePassword.requestFocus()
                }
                binding.edtRePassword.text?.trim().toString() != binding.edtPassword.text?.trim().toString() -> {
                    binding.edtRePassword.setError("Xác nhận mật khẩu không trùng khớp")
                    binding.edtRePassword.requestFocus()
                }
                else -> {
                    ickLoginViewModel.updatePassword(args.token, binding.edtPassword.text.toString()).observe(viewLifecycleOwner, Observer {
                        if (it?.statusCode == "200") {
                            IckChangePasswordSuccessDialog {
                                ickLoginViewModel.showLoginRegister()
                                val act = IckConfirmPasswordFragmentDirections.actionIckFillPwFragmentToIckLoginFragment()
                                findNavController().navigate(act)
                            }.show(requireActivity().supportFragmentManager, null)
                        } else {
                            it?.message?.let { msg ->
                                requireContext().showShortErrorToast(msg)
                            }
                        }
                    })
                }
            }
        }
        lifecycleScope.launch {
            delay(200)
            binding.edtPassword.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
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

    fun validate() {
        lifecycleScope.launch {
            delay(300)
            if (binding.edtPassword.text.toString().isNotEmpty() || binding.edtRePassword.text.toString().isNotEmpty()) {
                binding.btnLogin.enable()
            } else {
                binding.btnLogin.disable()
            }
        }
    }
}