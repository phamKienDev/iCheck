package vn.icheck.android.screen.user.wall.updatephone

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import vn.icheck.android.R
import vn.icheck.android.databinding.FragmentConfirmChangePhoneBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

class IckConfirmChangePhoneFragment:Fragment() {
    private var _binding:FragmentConfirmChangePhoneBinding? = null
    private val binding get() = _binding!!
    val args:IckConfirmChangePhoneFragmentArgs by navArgs()
    var timer: CountDownTimer? = null
    private val ickUserWallViewModel:IckUserWallViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentConfirmChangePhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        cancelTimer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initTimer()
        initOtp()
    }

    private fun initViews() {
        binding.constraintLayout.background=ViewHelper.bgWhiteCornersTop20(requireContext())

        val text = String.format("<p>${getString(R.string.ma_xac_nhan_otp_da_duoc_gui_den_n_sdt)} <span style='color:${vn.icheck.android.ichecklibs.ColorManager.getSecondaryColorCode(requireContext())}'>%s</span></p>", args.phone)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.textView25.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            binding.textView25.text = Html.fromHtml(text)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvTimer.setOnClickListener {
            if (binding.tvTimer.text == getString(R.string.gui_lai_ma)) {
                initTimer()
            }
        }
    }

    private fun initTimer() {
        cancelTimer()
        timer = object : CountDownTimer(181000, 1000) {

            override fun onFinish() {
                try {
                    binding.tvTimer.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getSecondaryColor(requireContext()))
                    binding.tvTimer.setText(R.string.gui_lai_ma)
                } catch (e: Exception) {
                    this.cancel()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                try {
                    binding.tvTimer.setText(R.string.gui_lai_ma_d_s, millisUntilFinished / 1000)
                } catch (e: Exception) {
                    this.cancel()
                }
            }
        }.start()
    }

    private fun initOtp() {
        binding.edtOtp.addTextChangedListener {
            if (it.toString().trim().length == 6) {
                ickUserWallViewModel.changedPhone = true
                findNavController().popBackStack()
            }
        }
    }
    private fun cancelTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }
}