package vn.icheck.android.screen.user.wall.updatepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.databinding.FragmentNewPwBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.model.ApiErrorResponse
import vn.icheck.android.network.model.ApiSuccessResponse
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.ick.simpleText
import vn.icheck.android.util.kotlin.WidgetUtils

class IckNewPwFragment : BaseFragmentMVVM() {
    private lateinit var binding: FragmentNewPwBinding
    private val ickUserWallViewModel: IckUserWallViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewPwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        validate()
        if (SessionManager.session.user?.hasPassword == false) {
            binding.textView26 rText R.string.cap_nhat_mat_khau
            binding.tvDesc rText R.string.vui_long_nhap_mat_khau
            binding.edtOldPassword.visibility = View.GONE
            binding.edtPassword.addTextChangedListener {
                validate()
            }
            binding.edtRePassword.addTextChangedListener {
                validate()
            }
        } else {
            binding.edtOldPassword.addTextChangedListener {
                validate()
            }
            binding.edtPassword.addTextChangedListener {
                validate()
            }
            binding.edtRePassword.addTextChangedListener {
                validate()
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            if (SessionManager.session.user?.hasPassword == true) {
                when {
                    binding.edtOldPassword.text?.toString().isNullOrEmpty() -> {
                        binding.edtOldPassword.apply {
                            error = context.rText(R.string.ban_chua_nhap_mat_khau_cu)
                        }
                    }
                    binding.edtRePassword.text?.toString().isNullOrEmpty() -> {
                        binding.edtRePassword.apply {
                            error = context.rText(R.string.xin_vui_long_xac_nhan_mat_khau)
                        }
                    }
                    binding.edtOldPassword.text?.length ?: 0 < 6 -> {
                        binding.edtOldPassword.apply {
                            error = context.rText(R.string.mat_khau_phai_lon_hon_hoac_bang_6_ki_tu)
                        }
                    }
                    binding.edtRePassword.text?.length ?: 0 < 6 -> {
                        binding.edtRePassword.apply {
                            error = context.rText(R.string.mat_khau_phai_lon_hon_hoac_bang_6_ki_tu)
                        }
                    }
                    binding.edtPassword.text.toString() != binding.edtRePassword.text.toString() -> {
                        binding.edtRePassword.apply {
                            error = context.rText(R.string.xac_nhan_mat_khau_khong_trung_khop)
                        }
                    }
                    else -> {

                        DialogHelper.showLoading(this)
                        ickUserWallViewModel.updatePassword(binding.edtOldPassword.text.toString(), binding.edtPassword.text.toString())
                                .observe(viewLifecycleOwner, Observer {
                                    DialogHelper.closeLoading(this)
                                    if (it is ApiSuccessResponse) {
                                        if (it.body.statusCode == "200") {
                                            requireContext().showShortSuccessToast(rText(R.string.ban_da_cap_nhat_mat_khau_thanh_cong))
                                            ickUserWallViewModel.getUserInfo().observe(requireActivity(), { user ->
                                                SessionManager.updateUser(user?.data?.createICUser())
                                            })
                                            delayAction({
                                                findNavController().popBackStack()
                                            }, 3000)
                                        } else {
                                            it.body.message?.let { msg ->
                                                requireContext().showShortErrorToast(msg)
                                            }
                                        }
                                    } else if (it is ApiErrorResponse) {
                                        requireContext().showShortErrorToast(it.error.message)
                                    }
                                })
                    }
                }
            } else {
                when {
                    binding.edtPassword.text?.toString().isNullOrEmpty() -> {
                        binding.edtPassword.apply {
                            error = context.rText(R.string.ban_chua_nhap_mat_khau_moi)
                        }
                    }
                    binding.edtRePassword.text?.toString().isNullOrEmpty() -> {
                        binding.edtRePassword.apply {
                            error = context.rText(R.string.xin_vui_long_xac_nhan_mat_khau)
                        }
                    }
                    binding.edtPassword.text?.length ?: 0 < 6 -> {
                        binding.edtPassword.apply {
                            error = context.rText(R.string.mat_khau_phai_lon_hon_hoac_bang_6_ki_tu)
                        }
                    }
                    binding.edtRePassword.text?.length ?: 0 < 6 -> {
                        binding.edtRePassword.apply {
                            error = context.rText(R.string.mat_khau_phai_lon_hon_hoac_bang_6_ki_tu)
                        }
                    }
                    binding.edtPassword.text.toString() != binding.edtRePassword.text.toString() -> {
                        binding.edtRePassword.apply {
                            error = context.rText(R.string.xac_nhan_mat_khau_khong_trung_khop)
                        }
                    }
                    else -> {

                        DialogHelper.showLoading(this)
                        ickUserWallViewModel.firstPassword(binding.edtPassword.text.toString())
                                .observe(viewLifecycleOwner, Observer {
                                    DialogHelper.closeLoading(this)
                                    if (it is ApiSuccessResponse) {
                                        if (it.body.statusCode == "200") {
                                            requireContext().showShortSuccessToast(rText(R.string.ban_da_cap_nhat_mat_khau_thanh_cong))
                                            delayAction({
                                                findNavController().popBackStack()
                                            }, 3000)
                                        } else {
                                            it.body.message?.let { msg ->
                                                requireContext().showShortErrorToast(msg)
                                            }
                                        }
                                    } else if (it is ApiErrorResponse) {
                                        requireContext().showShortErrorToast(it.error.message)
                                    }
                                })
                    }
                }
            }


        }

        setupListener()
    }

    private fun setupListener() {
        binding.edtOldPassword.setOnFocusChangeListener { _, _ ->
            WidgetUtils.setButtonKeyboardMargin(binding.btnKeyboardOld, binding.edtOldPassword)
        }
        binding.edtOldPassword.setCenterView(binding.btnKeyboardOld)

        binding.btnKeyboardOld.setOnClickListener {
            WidgetUtils.changePasswordInput(binding.edtOldPassword)
        }

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
        if (!binding.edtOldPassword.text?.toString().isNullOrEmpty() || !binding.edtPassword.text?.toString().isNullOrEmpty() || !binding.edtRePassword.text?.toString().isNullOrEmpty()) {
            enableContinue()
        } else {
            binding.btnContinue.disable()
        }
    }

    private fun enableContinue() {
        binding.btnContinue.enable()
    }
}
