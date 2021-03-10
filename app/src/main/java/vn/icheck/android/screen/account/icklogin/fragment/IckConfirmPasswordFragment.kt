package vn.icheck.android.screen.account.icklogin.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
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
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICSessionData
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.forceShowKeyboard
import vn.icheck.android.util.kotlin.ToastUtils

class IckConfirmPasswordFragment : CoroutineFragment() {

    private var _binding: FragmentFillPwBinding? = null
    private val binding get() = _binding!!
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()
    private val args: IckConfirmPasswordFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFillPwBinding.inflate(inflater, container, false)
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
                        }  else {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}