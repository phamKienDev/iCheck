package vn.icheck.android.screen.account.icklogin.fragment

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.fragment.CoroutineFragment
import vn.icheck.android.databinding.FragmentFillPwBinding
import vn.icheck.android.ichecklibs.visibleOrGone
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEvent
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEventListener
import vn.icheck.android.lib.keyboard.Unregistrar
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel

class IckConfirmPasswordFragment : CoroutineFragment() {
    private lateinit var binding: FragmentFillPwBinding
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()
    private val args: IckConfirmPasswordFragmentArgs by navArgs()

    private var unregistrar: Unregistrar? = null

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

        binding.edtPw.addTextChangedListener {
            validate()
        }
        binding.edtRepw.addTextChangedListener {
            validate()
        }
        binding.btnLogin.setOnClickListener {
            when {

                binding.edtPw.text?.trim().toString().length < 6 -> {
                    binding.edtPw.setError("Mật khẩu phải lớn hơn hoặc bằng 6 kí tự")
                    binding.edtPw.requestFocus()
                }
                binding.edtRepw.text?.trim().isNullOrEmpty() -> {
                    binding.edtRepw.setError("Vui lòng nhập dữ liệu")
                    binding.edtRepw.requestFocus()
                }
                binding.edtRepw.text?.trim().toString() != binding.edtPw.text?.trim().toString() -> {
                    binding.edtRepw.setError("Xác nhận mật khẩu không trùng khớp")
                    binding.edtRepw.requestFocus()
                }
                else -> {
                    ickLoginViewModel.updatePassword(args.token, binding.edtPw.text.toString()).observe(viewLifecycleOwner, Observer {
                        if (it?.statusCode == "200") {
                            IckChangePasswordSuccessDialog {
                                ickLoginViewModel.showLoginRegister()
                                val act = IckConfirmPasswordFragmentDirections.actionIckFillPwFragmentToIckLoginFragment()
                                findNavController().navigate(act)
                            }.show(requireActivity().supportFragmentManager, null)
                        } else {
                            it?.message?.let { msg ->
                                showError(msg)
                            }
                        }
                    })
                }
            }
        }
        lifecycleScope.launch {
            delay(200)
            binding.edtPw.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        setupListener()
    }

    private fun setupListener() {
        binding.btnKeyboard.setOnClickListener {
            changeKeyboard(binding.edtPw)
            changeKeyboard(binding.edtRepw)
        }

        binding.edtPw.setOnFocusChangeListener { v, hasFocus ->
            checkButtonChangeKeyboard()
            checkKeyboard(binding.edtPw)
        }

        binding.edtRepw.setOnFocusChangeListener { v, hasFocus ->
            checkButtonChangeKeyboard()
            checkKeyboard(binding.edtRepw)
        }
    }

    private fun checkButtonChangeKeyboard() {
        binding.btnKeyboard.visibleOrGone(binding.edtPw.isFocused || binding.edtRepw.isFocused)
    }

    private fun changeKeyboard(view: AppCompatEditText) {
        view.apply {
            if (isFocused) {
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

    private fun checkKeyboard(view: AppCompatEditText) {
//        view.apply {
//            if (isFocused) {
//                if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
//                    binding.btnKeyboard.setText(R.string.ban_phim_so)
//                } else {
//                    binding.btnKeyboard.setText(R.string.ban_phim_chu)
//                }
//            }
//        }
    }

    fun validate() {
        lifecycleScope.launch {
            delay(300)
            if (binding.edtPw.text.toString().isNotEmpty() || binding.edtRepw.text.toString().isNotEmpty()) {
                binding.btnLogin.enable()
            } else {
                binding.btnLogin.disable()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        unregistrar = KeyboardVisibilityEvent.registerEventListener(requireActivity(), object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                binding.btnKeyboard.visibleOrGone(isOpen && (binding.edtPw.isFocused || binding.edtRepw.isFocused))
            }
        })
    }

    override fun onPause() {
        super.onPause()
        unregistrar?.unregister()
        unregistrar = null
    }
}