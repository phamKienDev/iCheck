package vn.icheck.android.screen.account.icklogin.fragment

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.FragmentOtpBinding
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.helper.ShareSessionToModule
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.network.model.icklogin.ConfirmOtpResponse
import vn.icheck.android.network.model.icklogin.IckUserInfoData
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICSessionData
import vn.icheck.android.screen.account.icklogin.FORGOT_PW
import vn.icheck.android.screen.account.icklogin.LOGIN_OTP
import vn.icheck.android.screen.account.icklogin.REGISTER
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.*


class IckOtpFragment : Fragment() {
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()
    var timer: CountDownTimer? = null
    val args: IckOtpFragmentArgs by navArgs()
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initTimer()
        initOtp()
        lifecycleScope.launch {
            delay(200)
            binding.otpEditText.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    private fun initViews() {
        val arr = arrayListOf<Char>()
        val number = when (args.phone.length) {
            10 -> args.phone.toList()
            11 -> "0${args.phone.substring(2)}".toList()
            else -> "0${args.phone}".toList()
        }
        arr.addAll(number)
        arr.add(7, ' ')
        arr.add(4, ' ')
        val span = SpannableString("Mã xác nhận OTP đã được gửi đến số điện thoại ${arr.joinToString(separator = "")}")
        span.setSpan(ForegroundColorSpan(Color.parseColor("#057DDA")), 45, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val text = String.format("<p>Mã xác nhận OTP đã được gửi đến số điện thoại <font color=#057DDA>%s</font></p>", arr.joinToString(separator = ""))

        val spannableString = SpannableString(span)
        val onclickPhone = object : ClickableSpan() {
            override fun onClick(widget: View) {
                requireActivity().forceHideKeyboard()
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", args.phone, null))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.setUnderlineText(false)
            }
        }
        spannableString.setSpan(onclickPhone, 45, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textView25.text = spannableString
        binding.textView25.movementMethod = LinkMovementMethod.getInstance()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvTimer.setOnClickListener {
            if (binding.tvTimer.text == "Gửi lại mã") {
                initTimer()
            }
        }
    }

    fun EditText.showKeyboard() {
        if (requestFocus()) {
            (getActivity()?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(this, SHOW_IMPLICIT)
            setSelection(text.length)
        }
    }

    private fun View.getActivity(): AppCompatActivity? {
        var context = this.context
        while (context is ContextWrapper) {
            if (context is AppCompatActivity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    private fun initTimer() {
        cancelTimer()
        timer = object : CountDownTimer(61000, 1000) {
            override fun onFinish() {
                try {
                    binding.tvTimer.setTextColor(Color.parseColor("#3C5A99"))
                    binding.tvTimer.text = "Gửi lại mã"
                    binding.tvTimer.setOnClickListener {
                        if (!ickLoginViewModel.waitResponse) {
                            ickLoginViewModel.waitResponse = true
                            binding.otpEditText.setText("")
                            when (args.loginType) {
                                LOGIN_OTP -> {
                                    ickLoginViewModel.requestLoginOtp().observe(viewLifecycleOwner) {
                                        if (it?.statusCode == "200") {
                                            binding.tvTimer.setOnClickListener(null)
                                            binding.tvTimer.setTextColor(Color.parseColor("#757575"))
                                            ickLoginViewModel.waitResponse = false
                                            start()
                                        } else {
                                            requireContext().showShortErrorToast(it?.message)
                                        }
                                    }
                                }
                                FORGOT_PW -> {
                                    ickLoginViewModel.resendRegisterOtp().observe(viewLifecycleOwner) {
                                        if (it?.statusCode == "200") {
                                            binding.tvTimer.setOnClickListener(null)
                                            binding.tvTimer.setTextColor(Color.parseColor("#757575"))
                                            ickLoginViewModel.waitResponse = false
                                            start()
                                        } else {
                                            requireContext().showShortErrorToast(it?.message)
                                        }
                                    }
                                }
                                REGISTER -> {
                                    if (ickLoginViewModel.facebookToken.isNullOrEmpty()) {
                                        ickLoginViewModel.resendRegisterOtp().observe(viewLifecycleOwner) {
                                            if (it?.statusCode == "200") {
                                                binding.tvTimer.setOnClickListener(null)
                                                binding.tvTimer.setTextColor(Color.parseColor("#757575"))
                                                ickLoginViewModel.waitResponse = false
                                                start()
                                            } else {
                                                requireContext().showShortErrorToast(it?.message)
                                            }
                                        }
                                    } else {
                                        ickLoginViewModel.requestRegisterFacebook(ickLoginViewModel.facebookPhone.toString(), ickLoginViewModel.facebookToken.toString()).observe(viewLifecycleOwner) {
                                            if (it?.statusCode == "200") {
                                                binding.tvTimer.setOnClickListener(null)
                                                binding.tvTimer.setTextColor(Color.parseColor("#757575"))
                                                ickLoginViewModel.waitResponse = false
                                                start()
                                            } else {
                                                requireContext().showShortErrorToast(it?.message)
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                    this.cancel()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                try {
                    binding.tvTimer.text = String.format("Gửi lại mã (%ds)", millisUntilFinished / 1000)
                } catch (e: Exception) {
                    this.cancel()
                }
            }
        }.start()
    }

    private fun initOtp() {
        binding.otpEditText.addTextChangedListener {
            if (it.toString().trim().length == 6) {
                when (args.loginType) {
                    LOGIN_OTP -> {
                        loginOtp(it.toString().trim())
                    }
                    FORGOT_PW -> {
                        forgotPw(it.toString().trim())
                    }
                    REGISTER -> {
                        registerOtp(it.toString().trim())
                    }
                }
            }
        }
    }

    private fun registerOtp(str: String) {
        job?.cancel()
        job = lifecycleScope.launch {
            try {
                showLoadingTimeOut(30000)
                ickLoginViewModel.registerOtp(str).observe(viewLifecycleOwner) {
                    dismissLoadingScreen()
                    if (it?.statusCode != "200" && it?.statusCode != "U3045") {
                        toastError(it)
                    } else {
                        SessionManager.session.firebaseToken = it.data?.firebaseToken
                        RelationshipManager.refreshToken(true)
                        TrackingAllHelper.trackSignupSuccess()
                        hideKeyboard()
                        ickLoginViewModel.hideLoginRegister()

                        it.data?.token?.let { token ->
                            ickLoginViewModel.getUserInfo().observe(viewLifecycleOwner, { res ->
                                res?.data?.let { userInfoRes ->
                                    setData(userInfoRes, token, it.data?.firebaseToken.toString())

                                    if (it.data?.firstLogin == true) {
                                        val action = IckOtpFragmentDirections.actionIckOtpFragmentToIckFacebookUserInfoFragment(args.phone, args.userName, args.userAvatar)
                                        findNavController().navigate(action)
                                    } else {
                                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
                                        requireActivity().finish()
                                    }

                                }
                            })
                        }

                    }
                }
            } catch (e: Exception) {
                dismissLoadingScreen()
                requireActivity().forceHideKeyboard()
            }
        }
    }

    private fun toastError(it: ConfirmOtpResponse?) {
        it?.message?.let { msg ->
            requireContext().showShortErrorToast(msg)
        }
    }

    private fun forgotPw(str: String) {
        job?.cancel()
        job = lifecycleScope.launch {
            try {
                showLoadingTimeOut(30000)
                ickLoginViewModel.forgotPwOtp(str).observe(viewLifecycleOwner) {
                    dismissLoadingScreen()
                    if (it?.statusCode != "200") {
                        toastError(it)
                    } else {
                        hideKeyboard()
                        ickLoginViewModel.hideLoginRegister()
                        val action = IckOtpFragmentDirections.actionIckOtpFragmentToIckFillPwFragment(it.data?.token)
                        findNavController().navigate(action)
                        //                        it.data?.token?.let { token ->
                        //                            ickLoginViewModel.getUserInfo().observe(viewLifecycleOwner, { res ->
                        //                                res?.data?.let { userInfoRes ->
                        //                                    setData(userInfoRes, token)
                        //                                    ickLoginViewModel.hideLoginRegister()
                        //                                    val action = IckOtpFragmentDirections.actionIckOtpFragmentToIckFillPwFragment(args.token)
                        //                                    findNavController().navigate(action)
                        //                                }
                        //                            })
                        //                        }

                    }
                }
            } catch (e: Exception) {
                dismissLoadingScreen()
            }
        }
    }

    private fun loginOtp(str: String) {
        job?.cancel()
        job = lifecycleScope.launch {
            try {
                showLoadingTimeOut(30000)
                ickLoginViewModel.loginOtp(str).observe(viewLifecycleOwner) {
                    dismissLoadingScreen()
                    if (it?.statusCode != "200") {
                        toastError(it)
                    } else {
                        SessionManager.session.firebaseToken = it.data?.firebaseToken
                        RelationshipManager.refreshToken(true)
                        hideKeyboard()
                        it.data?.token?.let { token ->
                            ickLoginViewModel.getUserInfo().observe(viewLifecycleOwner) { res ->
                                res?.data?.let { userInfoRes ->
                                    setData(userInfoRes, token, it.data?.firebaseToken.toString())
                                    if (it.data?.responseCode == "U3046") {
                                        val action = IckOtpFragmentDirections.actionIckOtpFragmentToIckFacebookUserInfoFragment(args.phone, args.userName, args.userAvatar)
                                        findNavController().navigate(action)
                                    } else {
                                        InsiderHelper.onLogin()
                                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
                                        requireActivity().finish()
                                    }

                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                dismissLoadingScreen()
            }
        }
    }

    private fun hideKeyboard() {
        cancelTimer()
//        binding.otpEditText.clearFocus()
//        binding.otpEditText.clearComposingText()
//        requireActivity().forceHideKeyboard(binding.otpEditText)

        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.otpEditText.windowToken, 0)
    }

    fun setData(userInfoRes: IckUserInfoData, token: String, firebaseToken: String) {
        SessionManager.session = ICSessionData().apply {
            this.token = token
            type = userInfoRes.type
            user = userInfoRes.createICUser()
            userType = 2
            if (!args.userName.isNullOrEmpty()) {
                user?.facebookType = true
            }
            this.firebaseToken = firebaseToken
        }
        ShareSessionToModule.setSession(SessionManager.session)
        CartHelper().getCartSocial()

    }

    private fun cancelTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
        job?.cancel()
        _binding = null
    }
}