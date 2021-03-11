package vn.icheck.android.screen.user.pvcombank.confirmunlockcard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_confirm_unlock_pvcard.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.pvcombank.confirmunlockcard.viewModel.ConfirmUnlockPVCardViewModel
import vn.icheck.android.util.ick.forceHideKeyboard

class ConfirmUnlockPVCardActivity : BaseActivityMVVM() {

    private val viewModel: ConfirmUnlockPVCardViewModel by viewModels()
    var timer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_unlock_pvcard)
        listener()
        initView()
        initViewModel()
        viewModel.getData(intent)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnConfirm.setOnClickListener {
            viewModel.verifyOtpCard(edt_otp.text.toString())
        }

        btnResend.setOnClickListener {
            viewModel.getData(intent)
        }
    }

    private fun initView() {
        val phone = SessionManager.session.user?.phone.toString()
        val arr = arrayListOf<Char>()
        val number = when (phone.length) {
            10 -> phone.toList()
            11 -> "0${phone.substring(2)}".toList()
            else -> "0${phone}".toList()
        }
        arr.addAll(number)
        arr.add(7, ' ')
        arr.add(4, ' ')
        val span = SpannableString("Mã xác nhận OTP đã được gửi đến số điện thoại ${arr.joinToString(separator = "")}")
        span.setSpan(ForegroundColorSpan(Color.parseColor("#057DDA")), 45, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val spannableString = SpannableString(span)
        val onclickPhone = object : ClickableSpan() {
            override fun onClick(widget: View) {
                forceHideKeyboard()
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.setUnderlineText(false)
            }
        }
        spannableString.setSpan(onclickPhone, 45, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val text = String.format("<p>Mã xác nhận OTP đã được gửi đến\nsố điện thoại <span style='color:#057dda'>%s</span></p>", "0912651881")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tvOtpToPhone.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
//        } else {
//            tvOtpToPhone.text = Html.fromHtml(text)
//        }
        tvOtpToPhone.text = spannableString

        edt_otp.addTextChangedListener {
            if (!it?.trim().isNullOrEmpty()) {
                btnConfirm.setBackgroundResource(R.drawable.bg_corners_4_light_blue_solid)
                btnConfirm.isEnabled = true
            } else {
                btnConfirm.setBackgroundResource(R.drawable.bg_corner_gray_topup_4)
                btnConfirm.isEnabled = false
            }
        }
        initTimer()
    }

    private fun initViewModel() {
        viewModel.dataUnLockCard.observe(this, {
            if (!it.verification?.requestId.isNullOrEmpty()) {
                viewModel.requestId = it.verification?.requestId!!
            }

            if (!it.verification?.otpTransId.isNullOrEmpty()) {
                viewModel.otptranid = it.verification?.otpTransId!!
            }
        })

        viewModel.unlockCardSuccess.observe(this, {
            val intent = Intent()
            intent.putExtra(Constant.DATA_1, it)
            intent.putExtra(Constant.DATA_2, viewModel.objCard?.cardId)
            setResult(Activity.RESULT_OK, intent)
            onBackPressed()
        })

        viewModel.statusCode.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            viewModel.unlockCard(viewModel.objCard?.cardId)
                        }
                    })
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.errorData.observe(this, {
            when (it) {
                Constant.ERROR_EMPTY -> {
                    showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    onBackPressed()
                }

                Constant.ERROR_SERVER -> {
                    showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    onBackPressed()
                }

                Constant.ERROR_UNKNOW -> {
                    showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    onBackPressed()
                }
            }
        })
    }

    private fun initTimer() {
        cancelTimer()
        timer = object : CountDownTimer(61000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                try {
                    btnResend.text = String.format("Gửi lại mã (%ds)", millisUntilFinished / 1000)
                } catch (e: Exception) {
                    this.cancel()
                }
            }

            override fun onFinish(){
                btnResend.setTextColor(Color.parseColor("#3C5A99"))
                btnResend.text = "Gửi lại mã"
                btnResend.setOnClickListener {
                    btnResend.setOnClickListener(null)
                    btnResend.setTextColor(Color.parseColor("#757575"))
                    viewModel.getData(intent)
                    start()
                }
            }
        }.start()
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
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.FINISH_ALL_PVCOMBANK -> {
                onBackPressed()
            }
            else -> {
            }
        }
    }
}