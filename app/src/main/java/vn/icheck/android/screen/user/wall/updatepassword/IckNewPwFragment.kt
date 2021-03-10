package vn.icheck.android.screen.user.wall.updatepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import vn.icheck.android.base.fragment.CoroutineFragment
import vn.icheck.android.databinding.FragmentNewPwBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.model.ApiErrorResponse
import vn.icheck.android.model.ApiSuccessResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.util.ick.showShortError
import vn.icheck.android.util.ick.showSimpleErrorToast
import vn.icheck.android.util.ick.showSimpleSuccessToast
import vn.icheck.android.util.ick.simpleText

class IckNewPwFragment : CoroutineFragment() {
    private var _binding: FragmentNewPwBinding? = null
    private val binding get() = _binding!!
    private val args: IckNewPwFragmentArgs by navArgs()
    private val ickUserWallViewModel: IckUserWallViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNewPwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (SessionManager.session.user?.hasPassword == false) {
            binding.textView26 simpleText "Cập nhật mật khẩu"
            binding.tvDesc simpleText "Vui lòng nhập mật khẩu"
            binding.groupOldPw.visibility = View.GONE
            binding.groupPw.addTextChangedListener {
                validate()
            }
            binding.groupRePw.addTextChangedListener {
                validate()
            }
        } else {
            binding.groupOldPw.addTextChangedListener {
                validate()
            }
            binding.groupPw.addTextChangedListener {
                validate()
            }
            binding.groupRePw.addTextChangedListener {
                validate()
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            if (SessionManager.session.user?.hasPassword == true) {
                when {
                    binding.groupOldPw.text?.toString().isNullOrEmpty() -> {
                        binding.groupOldPw.setError("Bạn chưa nhập mật khẩu cũ")
                    }
                    binding.groupRePw.text?.toString().isNullOrEmpty() -> {
                        binding.groupRePw.setError("Xin vui lòng xác nhận mật khẩu")
                    }
                    binding.groupOldPw.text?.length ?: 0 < 6 -> {
                        binding.groupOldPw.setError("Mật khẩu phải lớn hơn hoặc bằng 6 ký tự")
                    }
                    binding.groupRePw.text?.length ?: 0 < 6 -> {
                        binding.groupRePw.setError("Mật khẩu phải lớn hơn hoặc bằng 6 ký tự")
                    }
                    binding.groupPw.text.toString() !=  binding.groupRePw.text.toString() -> {
                        binding.groupRePw.setError("Xác nhận mật khẩu không trùng khớp")
                    }
                    else -> {

                        DialogHelper.showLoading(this)
                        ickUserWallViewModel.updatePassword(binding.groupOldPw.text.toString(), binding.groupPw.text.toString())
                                .observe(viewLifecycleOwner, Observer {
                                    DialogHelper.closeLoading(this)
                                    if (it is ApiSuccessResponse) {
                                        if (it.body.statusCode == "200") {
                                            requireContext().showSimpleSuccessToast("Bạn đã cập nhật mật khẩu thành công")
                                            ickUserWallViewModel.getUserInfo().observe(requireActivity(), {user ->
                                                SessionManager.updateUser(user?.data?.createICUser())
                                            })
                                            delayAction({
                                                findNavController().popBackStack()
                                            }, 3000)
                                        } else {
                                            it.body.message?.let { msg ->
                                                requireContext().showSimpleErrorToast(msg)
                                            }
                                        }
                                    } else if (it is ApiErrorResponse) {
                                        requireContext().showShortError(it.error.message)
                                    }
                                })
                    }
                }
            } else {
                when {
                    binding.groupPw.text?.toString().isNullOrEmpty() -> {
                        binding.groupPw.setError("Bạn chưa nhập mật khẩu mới")
                    }
                    binding.groupRePw.text?.toString().isNullOrEmpty() -> {
                        binding.groupRePw.setError("Xin vui lòng xác nhận mật khẩu")
                    }
                    binding.groupPw.text?.length ?: 0 < 6 -> {
                        binding.groupPw.setError("Mật khẩu phải lớn hơn hoặc bằng 6 ký tự")
                    }
                    binding.groupRePw.text?.length ?: 0 < 6 -> {
                        binding.groupRePw.setError("Mật khẩu phải lớn hơn hoặc bằng 6 ký tự")
                    }
                    binding.groupPw.text.toString() !=  binding.groupRePw.text.toString() -> {
                        binding.groupRePw.setError("Xác nhận mật khẩu không trùng khớp")
                    }
                    else -> {

                        DialogHelper.showLoading(this)
                        ickUserWallViewModel.firstPassword(binding.groupPw.text.toString())
                                .observe(viewLifecycleOwner, Observer {
                                    DialogHelper.closeLoading(this)
                                    if (it is ApiSuccessResponse) {
                                        if (it.body.statusCode == "200") {
                                            requireContext().showSimpleSuccessToast("Bạn đã cập nhật mật khẩu thành công")
                                            delayAction({
                                                findNavController().popBackStack()
                                            }, 3000)
                                        } else {
                                            it.body.message?.let { msg ->
                                                requireContext().showSimpleErrorToast(msg)
                                            }
                                        }
                                    } else if (it is ApiErrorResponse) {
                                        requireContext().showShortError(it.error.message)
                                    }
                                })
                    }
                }
            }


        }
    }

    private fun validate() {
        if (!binding.groupOldPw.text?.toString().isNullOrEmpty() || !binding.groupPw.text?.toString().isNullOrEmpty() || !binding.groupRePw.text?.toString().isNullOrEmpty()) {
            enableContinue()
        } else {
            binding.btnContinue.disable()
        }
    }

    private fun enableContinue() {
        binding.btnContinue.enable()
    }
}
