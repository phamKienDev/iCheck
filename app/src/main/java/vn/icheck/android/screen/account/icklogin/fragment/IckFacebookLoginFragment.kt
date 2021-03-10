package vn.icheck.android.screen.account.icklogin.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.databinding.FragmentIckLoginFacebookBinding
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.screen.account.icklogin.REGISTER
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.screen.location.IckNationBottomDialog
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.dismissLoadingScreen
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.ick.showLoadingTimeOut

class IckFacebookLoginFragment : Fragment() {
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()
    private var _binding: FragmentIckLoginFacebookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentIckLoginFacebookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext().applicationContext)
                .load(ickLoginViewModel.facebookAvatar)
                .placeholder(R.drawable.ic_avatar_default_84px)
                .error(R.drawable.ic_avatar_default_84px)
                .into(binding.userAvatar)
        binding.tvUsername.text = ickLoginViewModel.facebookUsername
        binding.edtPhone.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().startsWith("84")) {
//                    binding.edtPhone.setText(s.toString().replace("84", "0", true))
//                    binding.edtPhone.setSelection(1)
//                }
                checkForm()
            }
        })
        binding.btnContinue.setOnClickListener {
            if (!ickLoginViewModel.waitResponse) {
                ickLoginViewModel.waitResponse = true
                ickLoginViewModel.requestRegisterFacebook(binding.edtPhone.text.toString(), ickLoginViewModel.facebookToken.toString())
                        .observe(viewLifecycleOwner, Observer {
                            ickLoginViewModel.waitResponse = false
                            if (it != null) {
                                TrackingAllHelper.trackLoginSuccess()
                                try {
                                    val action = IckFacebookLoginFragmentDirections
                                            .actionIckFacebookLoginFragmentToIckOtpFragment(
                                                    ickLoginViewModel.facebookToken.toString(),
                                                    ickLoginViewModel.facebookPhone.toString(),
                                                    REGISTER,
                                                    ickLoginViewModel.facebookUsername.toString(),
                                                    ickLoginViewModel.facebookAvatar.toString()
                                            )
                                    findNavController().navigate(action)
                                } catch (e: Exception) {
                                    logError(e)
                                }
                            }
                        })
            }
        }
        binding.tvNation.setOnClickListener {
            IckNationBottomDialog().show(requireActivity().supportFragmentManager, null)
        }
    }

    private fun checkForm() {
        when {
            binding.edtPhone.text?.toString()?.length == 11 && binding.edtPhone.text.toString().startsWith("84") -> binding.btnContinue.enable()
            binding.edtPhone.text?.toString()?.length == 10 && binding.edtPhone.text.toString().startsWith("0") -> binding.btnContinue.enable()
            binding.edtPhone.text?.toString()?.length == 9 -> binding.btnContinue.enable()
            else -> binding.btnContinue.disable()
        }
        ickLoginViewModel.facebookPhone = binding.edtPhone.text.toString()
    }
}