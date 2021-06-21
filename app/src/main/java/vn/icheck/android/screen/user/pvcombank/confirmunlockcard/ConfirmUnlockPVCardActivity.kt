package vn.icheck.android.screen.user.pvcombank.confirmunlockcard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_confirm_unlock_pvcard.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
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
        val span = SpannableString(getString(R.string.ma_xac_nhan_otp_da_duoc_gui_den_sdt_s, arr.joinToString(separator = "")))
        span.setSpan(ForegroundColorSpan(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)), 45, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
                btnConfirm.background = ViewHelper.bgPrimaryCorners4(this@ConfirmUnlockPVCardActivity)
                btnConfirm.isEnabled = true
            } else {
                btnConfirm.setBackgroundResource(R.drawable.bg_corner_gray_topup_4)
                btnConfirm.isEnabled = false
            }
        }
        initTimer()
    }

    private fun initViewModel() {
        viewModel.dataUnLockCard.observe(this, Observer {
            if (!it.verification?.requestId.isNullOrEmpty()) {
                viewModel.requestId = it.verification?.requestId!!
            }

            if (!it.verification?.otpTransId.isNullOrEmpty()) {
                viewModel.otptranid = it.verification?.otpTransId!!
            }
        })

        viewModel.unlockCardSuccess.observe(this, Observer {
            val intent = Intent()
            intent.putExtra(Constant.DATA_1, it)
            intent.putExtra(Constant.DATA_2, viewModel.objCard?.cardId)
            setResult(Activity.RESULT_OK, intent)
            onBackPressed()
        })

        viewModel.statusCode.observe(this, Observer {
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

        viewModel.errorData.observe(this, Observer {
            showLongError(it)
            onBackPressed()
        })

        viewModel.verifyError.observe(this, Observer {
            showLongError(it)
        })
    }

    private fun initTimer() {
        cancelTimer()
        timer = object : CountDownTimer(61000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    btnResend.text = getString(R.string.gui_lai_ma_d_s, millisUntilFinished / 1000)
                } catch (e: Exception) {
                    this.cancel()
                }
            }

            override fun onFinish() {
                btnResend.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(this@ConfirmUnlockPVCardActivity))
                btnResend.text = getString(R.string.gui_lai_ma)
                btnResend.setOnClickListener {
                    btnResend.setOnClickListener(null)
                    btnResend.setTextColor(Color.parseColor(vn.icheck.android.ichecklibs.Constant.getSecondTextCode))
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