package vn.icheck.android.screen.user.buy_mobile_card_success

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_buy_card_success.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.screen.user.buy_mobile_card.BuyMobileCardV2Activity
import vn.icheck.android.screen.user.history_loading_card.home.HistoryCardActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.payment_topup.viewmodel.PaymentViewModel
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.ToastUtils

class BuyCardSuccessActivity : BaseActivityMVVM() {

    lateinit var viewModel: PaymentViewModel

    private var code: String? = ""
    private var typePayment = rText(R.string.vi_icheck)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_card_success)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        BuyMobileCardV2Activity.listActivities.add(this)
        initView()
        listener()
        TrackingAllHelper.trackIcheckPhoneTopupSuccess()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val data = intent.getSerializableExtra(Constant.DATA_1)

        if (data != null) {
            setData(data as ICRechargePhone)
        } else {
            getDataUriVnPay()
        }
    }

    private fun getDataUriVnPay() {
        val data = intent.getLongExtra(Constant.DATA_2, -1)

        if (data != -1L) {
            typePayment = "VNPAY"
            DialogHelper.showLoading(this)
            viewModel.getDetailCard(data)
        } else {
            onBackPressed()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listener() {
        viewModel.detailCard.observe(this, Observer {
            DialogHelper.closeLoading(this)
            setData(it)
        })

        viewModel.errorMessage.observe(this, Observer {
            DialogHelper.closeLoading(this)
            this.showShortErrorToast(it)
            onBackPressed()
        })

        imgBack.setOnClickListener {
            for (act in BuyMobileCardV2Activity.listActivities) {
                act.finish()
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            }
        }

        imgHistoryTopup.setOnClickListener {
            val intent = Intent(this@BuyCardSuccessActivity, HistoryCardActivity::class.java)
            intent.putExtra(Constant.DATA_1, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        imgHome.setOnClickListener {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        btn_again.setOnClickListener {
            for (act in BuyMobileCardV2Activity.listActivities) {
                act.finish()
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            }
        }

        btn_charge.setOnClickListener {
            val chargePhone = Intent(Intent.ACTION_DIAL)
            chargePhone.data = Uri.parse("tel:" + Uri.encode("*100*$code#"))
            startActivity(chargePhone)
        }

        tv_serial.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val textLocation = IntArray(2)
                tv_serial.getLocationOnScreen(textLocation)
                if (event.rawX >= textLocation[0] + tv_serial.width - tv_serial.totalPaddingRight) {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(rText(R.string.so_serie), tv_serial.text)
                    clipboard.setPrimaryClip(clip)
                    ToastUtils.showShortSuccess(this, rText(R.string.copy_so_serie_s, tv_serial.text))
                    return@OnTouchListener true
                }
            }
            true
        })

        tv_code.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val textLocation = IntArray(2)
                tv_code.getLocationOnScreen(textLocation)
                if (event.rawX >= textLocation[0] + tv_code.width - tv_code.totalPaddingRight) {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(rText(R.string.ma_the), tv_code.text)
                    clipboard.setPrimaryClip(clip)
                    ToastUtils.showShortSuccess(this, rText(R.string.copy_ma_the_s, tv_code.text))
                    return@OnTouchListener true
                }
            }
            true
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData(data: ICRechargePhone) {
        tvMessageSuccess.rText(R.string.quy_khach_da_mua_thanh_cong_ma_the_dien_thoai_s, data.provider)
        tvName.text = data.provider

        if (data.denomination is String) {
            tvTotal.text = if (typePayment == rText(R.string.vi_icheck)) {
                rText(R.string.s_xu, TextHelper.formatMoneyPhay(data.denomination.toString().toLong()))
            } else {
                rText(R.string.s_space_d, TextHelper.formatMoneyPhay(data.denomination.toString().toLong()))
            }
        }
        tvTypePayment.text = typePayment

        tv_serial.text = data.card?.serial
        tv_code.text = data.card?.pin
        code = data.card?.pin
    }
}