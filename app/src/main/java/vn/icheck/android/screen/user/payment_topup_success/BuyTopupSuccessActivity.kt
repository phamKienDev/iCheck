package vn.icheck.android.screen.user.payment_topup_success

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_buy_topup_success.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.screen.user.history_loading_card.home.HistoryCardActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.payment_topup.viewmodel.PaymentViewModel
import vn.icheck.android.screen.user.recharge_phone.RechargePhoneActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast

class BuyTopupSuccessActivity : BaseActivityMVVM() {
    lateinit var viewModel: PaymentViewModel
    private var vnPayType = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_topup_success)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        RechargePhoneActivity.listActivities.add(this)
        initView()
        listener()
        TrackingAllHelper.trackPhoneTopupSuccess()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val data = intent.getSerializableExtra(Constant.DATA_1)

        if (data != null) {
            setDataView(data as ICRechargePhone)
        } else {
            getDataUriVnPay()
        }
    }

    private fun getDataUriVnPay() {
        val data = intent.getLongExtra(Constant.DATA_2, -1)

        if (data != -1L){
            tvTypePayment.text = "VNPAY"
            viewModel.getDetailCard(data)
        }else{
            onBackPressed()
        }
    }

    private fun listener() {
        viewModel.detailCard.observe(this, {
            setDataView(it)
        })

        viewModel.errorMessage.observe(this, {
            this.showShortErrorToast(it)
            onBackPressed()
        })

        imgBack.setOnClickListener {
            for (act in RechargePhoneActivity.listActivities) {
                act.finish()
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            }
        }

        btnTopupAgain.setOnClickListener {
            for (act in RechargePhoneActivity.listActivities) {
                act.finish()
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            }
        }

        imgHome.setOnClickListener {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        imgHistoryTopup.setOnClickListener {
            val intent = Intent(this@BuyTopupSuccessActivity, HistoryCardActivity::class.java)
            intent.putExtra(Constant.DATA_1, 1)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        for (act in RechargePhoneActivity.listActivities)
//            act.finish()
    }

    fun setDataView(data: ICRechargePhone) {
        tvMessageSuccess.text = "Quý khách đã nạp thành công \n 1 mã thẻ điện thoại " + data?.provider

        tvName.text = data?.provider

        if (data.denomination is String) {
            if (vnPayType) {
                tvTotal.text = TextHelper.formatMoneyPhay(data.denomination.toString().toLong()) + " đ"
                tvTypePayment.text = "VNPAY"
            } else {
                tvTotal.text = TextHelper.formatMoneyPhay(data.denomination.toString().toLong()) + " Xu"
                tvTypePayment.text = "Ví iCheck"
            }
        }

        tvPhone.text = data.phone?.let {
            if (it.startsWith("84")) {
                StringBuilder(it).apply {
                    replace(0, 2, "0")
                }.toString()
            } else if (it.startsWith("+84")) {
                StringBuilder(it).apply {
                    replace(0, 3, "0")
                }.toString()
            } else {
                it
            }
        }
    }
}
