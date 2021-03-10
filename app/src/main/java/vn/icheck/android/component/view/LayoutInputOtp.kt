package vn.icheck.android.component.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICLayoutOtp

class LayoutInputOtp : LinearLayout {
    private var receiveOtpType = ""
    private var timeToSendOtp = 60000L
    private var timeToChangeType = 60000L
    private var phoneNumber = ""

    val SEND_SMS_TYPE = "sms"
    val CALL_PHONE_TYPE = "call"

    private var timerSendOtp: CountDownTimer? = null
    private var timerChangeType: CountDownTimer? = null

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupView()
    }

    private fun setupView() {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL

        addView(ViewHelper.createText(context,
                ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, SizeHelper.size60, SizeHelper.size8, SizeHelper.size60, 0),
                null,
                ViewHelper.createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.fast_survey_gray),
                14f).also {
            it.gravity = Gravity.CENTER_HORIZONTAL
        })

        // Layout center
        addView(LinearLayout(context).also { layoutCenter ->
            layoutCenter.id = R.id.layoutCenter
            layoutCenter.layoutParams = ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
            layoutCenter.orientation = VERTICAL
            layoutCenter.gravity = Gravity.CENTER

            // Layout otp
            layoutCenter.addView(LinearLayout(context).also { layoutOtp ->
                layoutOtp.layoutParams = ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, SizeHelper.size44)
                layoutOtp.orientation = HORIZONTAL

                for (i in 0 until 6) {
                    layoutOtp.addView(AppCompatEditText(context).also { edtInpuut ->
                        edtInpuut.layoutParams = ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, SizeHelper.size8, 0, SizeHelper.size8, 0)
                        edtInpuut.minWidth = SizeHelper.size20
                        edtInpuut.typeface = ViewHelper.createTypeface(context, R.font.barlow_medium)
                        edtInpuut.background = ContextCompat.getDrawable(context, R.drawable.under_line_dark_gray2_2dp)
                        edtInpuut.setTextColor(Color.BLACK)
                        edtInpuut.includeFontPadding = false
                        edtInpuut.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                        edtInpuut.isSingleLine = true
                        edtInpuut.gravity = Gravity.CENTER
                        edtInpuut.inputType = InputType.TYPE_CLASS_NUMBER
                        onTextChange(i, edtInpuut)
                    })
                }
            })

            // Text send
            layoutCenter.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, SizeHelper.size24, 0, 0),
                    ViewHelper.outValue.resourceId,
                    ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                    ContextCompat.getColorStateList(context, R.color.text_send_otp_login),
                    14f).also {
                it.setPadding(SizeHelper.size24, SizeHelper.size8, SizeHelper.size24, SizeHelper.size8)
            })
        })

        addView(ViewHelper.createText(context,
                ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, SizeHelper.size24, 0, 0),
                ViewHelper.outValue.resourceId,
                ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                ContextCompat.getColorStateList(context, R.color.text_send_otp_login),
                14f).also {
            it.setPadding(SizeHelper.size24, SizeHelper.size8, SizeHelper.size24, SizeHelper.size8)
        })
    }

    private fun onTextChange(position: Int, edtInput: AppCompatEditText) {
        var oldChar = ' '

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.isNotEmpty()) {
                    oldChar = p0[0]
                } else {
                    oldChar = ' '
                }
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!charSequence.isNullOrEmpty()) {
                    if (oldChar != ' ') {
                        edtInput.removeTextChangedListener(this)

                        if (charSequence.first() == oldChar) {
                            edtInput.setText(charSequence.removeRange(0, 1))
                        } else if (charSequence.last() == oldChar) {
                            edtInput.setText(charSequence.removeRange(1, 2))
                        }
                        edtInput.setSelection(edtInput.length())

                        edtInput.addTextChangedListener(this)
                    }

                    if (position < 5) {
                        (((getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(position + 1) as AppCompatEditText).run {
                            requestFocus()

                            if (!text.isNullOrEmpty()) {
                                setSelection(length())
                            }
                        }
                    }
                } else {
                    if (position > 0) {
                        (((getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(position - 1) as AppCompatEditText).run {
                            requestFocus()

                            if (!text.isNullOrEmpty()) {
                                setSelection(length())
                            }
                        }
                    }
                }

                edtInput.background = if (edtInput.text.isNullOrEmpty()) {
                    ContextCompat.getDrawable(context, R.drawable.under_line_dark_gray2_2dp)
                } else {
                    ContextCompat.getDrawable(context, R.drawable.under_line_light_blue_2dp)
                }
            }
        }

        edtInput.addTextChangedListener(textWatcher)
    }

    fun setData(phone: String, setting: ICLayoutOtp?) {
        phoneNumber = phone

        setting?.let {
            it.timeout?.let {
                timeToSendOtp = it
            }

            it.timechange?.let {
                timeToChangeType = it
            }

            it.type?.let {
                setReceiveOtpType(it)
            }
        }

        refreshTimeToSendOtp()
    }

    /**
     * Set giá trị kiểu nhận OTP và hiển thị các thông tin:
     * - Gửi mã otp đến số điện thoại xxx
     * - Đổi phương thức nhận otp
     *
     * @param type kiểu nhận otp SEND_SMS_METHOD hoặc CALL_PHONE_METHOD
     */
    fun setReceiveOtpType(type: String) {
        receiveOtpType = type

        val sendToPhoneMessage: String
        val receiverMessageType: String

        when (receiveOtpType) {
            CALL_PHONE_TYPE -> {
                sendToPhoneMessage = context.getString(R.string.login_ma_xac_thuc_otp_da_duoc_goi_toi, phoneNumber)
                receiverMessageType = context.getString(R.string.doi_phuong_thuc_nhan_sms)
            }
            SEND_SMS_TYPE -> {
                sendToPhoneMessage = context.getString(R.string.login_ma_xac_thuc_otp_da_duoc_gui_toi, phoneNumber)
                receiverMessageType = context.getString(R.string.doi_phuong_thuc_nhan_cuoc_goi)
            }
            else -> {
                sendToPhoneMessage = ""
                receiverMessageType = ""
            }
        }

        (getChildAt(0) as AppCompatTextView).text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(sendToPhoneMessage, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(sendToPhoneMessage);
        }
        (getChildAt(2) as AppCompatTextView).text = receiverMessageType
    }

    /**
     * Lấy giá trị kiểu nhận OTP
     */
    val getReceiveOtpType: String
        get() {
            return receiveOtpType
        }

    /**
     * Hiển thị thời gian đếm ngược để enable nút "gửi lại mã OTP và đổi phương thức nhận
     *
     * @param millisecond thời gian (không truyền sẽ lấy thời gian mặc định là 60s)
     */
    fun refreshTimeToSendOtp() {
        refreshOtpTime()
        refreshTypeTime()
    }

    private fun refreshOtpTime() {
        timerSendOtp?.cancel()
        timerSendOtp = null

        ((getChildAt(1) as ViewGroup).getChildAt(1) as AppCompatCheckedTextView).run {
            isEnabled = false

            text = when (receiveOtpType) {
                CALL_PHONE_TYPE -> {
                    context.getString(R.string.goi_lai_ma_xxx_s, getTimeSecond(timeToSendOtp))
                }
                SEND_SMS_TYPE -> {
                    context.getString(R.string.gui_lai_ma_xxx_s, getTimeSecond(timeToSendOtp))
                }
                else -> {
                    getTimeSecond(timeToSendOtp)
                }
            }
        }

        timerSendOtp = object : CountDownTimer(timeToSendOtp, 1000) {
            override fun onFinish() {
                ((getChildAt(1) as ViewGroup?)?.getChildAt(1) as AppCompatCheckedTextView?)?.run {
                    isEnabled = true
                    setText(R.string.gui_lai_ma)
                }
            }

            override fun onTick(millisecond: Long) {
                ((getChildAt(1) as ViewGroup?)?.getChildAt(1) as AppCompatCheckedTextView?)?.run {
                    text = when (receiveOtpType) {
                        CALL_PHONE_TYPE -> {
                            context.getString(R.string.goi_lai_ma_xxx_s, getTimeSecond(millisecond))
                        }
                        SEND_SMS_TYPE -> {
                            context.getString(R.string.gui_lai_ma_xxx_s, getTimeSecond(millisecond))
                        }
                        else -> {
                            getTimeSecond(millisecond)
                        }
                    }
                }
            }
        }

        timerSendOtp?.start()
    }

    private fun refreshTypeTime() {
        timerChangeType?.cancel()
        timerChangeType = null

        (getChildAt(2) as AppCompatTextView).isEnabled = false

        timerChangeType = object : CountDownTimer(timeToChangeType, 1000) {
            override fun onFinish() {
                if (this != null) {
                    val receiverMessageType = when (receiveOtpType) {
                        CALL_PHONE_TYPE -> {
                            context.getString(R.string.doi_phuong_thuc_nhan_sms)
                        }
                        SEND_SMS_TYPE -> {
                            context.getString(R.string.doi_phuong_thuc_nhan_cuoc_goi)
                        }
                        else -> {
                            ""
                        }
                    }

                    (getChildAt(2) as AppCompatTextView).run {
                        text = receiverMessageType
                        isEnabled = true
                    }
                }
            }

            override fun onTick(millisecond: Long) {
            }
        }

        timerChangeType?.start()
    }

    /**
     * Chuyển thời gian kiểu millisecond sang second
     *
     * @param millisecond thời gian
     * @return thời gian dạng second kiểu String
     */
    private fun getTimeSecond(millisecond: Long): String {
        return (millisecond / 1000).toString()
    }

    /**
     * Lấy dãy số OTP người dùng đã nhập
     */
    val getOtp: String
        get() {
            val builder = StringBuilder()

            val layoutOtp = (getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup
            for (i in 0 until layoutOtp.childCount) {
                builder.append((layoutOtp.getChildAt(i) as AppCompatEditText).text)
            }

            return builder.toString()
        }

    /**
     * Callback khi click vào nút Gửi lại mã OTP
     *
     * @param listener callback của view
     */
    fun setOnSendOtpClicked(listener: OnClickListener) {
        (getChildAt(1) as ViewGroup).getChildAt(1).setOnClickListener(listener)
    }

    /**
     * Callback khi click vào nút Đổi phương thức gửi OTP
     *
     * @param listener callback của view
     */
    fun setOnChangeMethodReceiver(listener: OnClickListener) {
        getChildAt(2).setOnClickListener(listener)
    }
}