package vn.icheck.android.screen.user.payment_topup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_payment_topup.*
import kotlinx.android.synthetic.main.activity_payment_topup.imgBack
import kotlinx.android.synthetic.main.activity_payment_topup.txtTitle
import kotlinx.android.synthetic.main.activity_recharge_phone.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.screen.user.buy_mobile_card.BuyMobileCardV2Activity
import vn.icheck.android.screen.user.buy_mobile_card_success.BuyCardSuccessActivity
import vn.icheck.android.screen.user.home_page.campaign.list_campaign.ListCampaignActivity
import vn.icheck.android.screen.user.payment_topup.adapter.PaymentTypeAdapter
import vn.icheck.android.screen.user.payment_topup.adapter.ValuePaymentAdapter
import vn.icheck.android.screen.user.payment_topup.viewmodel.PaymentViewModel
import vn.icheck.android.screen.user.payment_topup_success.BuyTopupSuccessActivity
import vn.icheck.android.screen.user.recharge_phone.RechargePhoneActivity
import vn.icheck.android.util.ick.showSimpleErrorToast

class PaymentTopupActivity : BaseActivityMVVM(), ItemClickListener<ICRechargePhone> {

    private lateinit var viewModel: PaymentViewModel
    private lateinit var adapterPaymentType: PaymentTypeAdapter
    private lateinit var adapterValue: ValuePaymentAdapter

    private var sodutaikhoan: Long? = null
    private var mValue: Long? = null
    private var serviceId: Long? = null
    private var mIdNetWork: Long? = null
    private var mType: Int? = null
    private var mPhone: String? = null
    private var mNameNetwork: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_topup)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        initRecycleView()
        listenerGetData()
        getCoin()
        txtTitle.text = "Thanh toán"
        listener()
    }

    private fun initRecycleView() {
        adapterValue = ValuePaymentAdapter(this)
        recyclerView.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }
        recyclerView.adapter = adapterValue

        adapterPaymentType = PaymentTypeAdapter(this, this)
        recyclerPaymentType.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }
        recyclerPaymentType.adapter = adapterPaymentType
    }

    @SuppressLint("SetTextI18n")
    private fun listenerGetData() {
        viewModel.getDataIntent(intent)
        viewModel.getListPaymentType()

        viewModel.dataIntent.observe(this, {
            mType = it.type
            mValue = it.value?.toLong()
//            serviceId = it.serviceId
            serviceId = it.card?.serviceId
            mNameNetwork = it.card?.provider
            mIdNetWork = it.card?.id
            mPhone = if (it.phoneNumber.isNullOrEmpty()) SessionManager.session.user?.phone else it.phoneNumber

            if (mType == 1) {
                RechargePhoneActivity.listActivities.add(this)
            } else {
                BuyMobileCardV2Activity.listActivities.add(this)
            }

            it.listValue?.let { it1 -> adapterValue.setListData(it1) }
        })

        viewModel.dataListPaymentType.observe(this, {
            adapterPaymentType.setListData(it)
        })

        viewModel.dataBuyCard.observe(this, {
            when (adapterPaymentType.selectedItem?.agent?.code) {
                "ICHECK-XU" -> {
                    if (it.type == 1) {
                        val intent = Intent(this, BuyTopupSuccessActivity::class.java)
                        intent.putExtra(Constant.DATA_1, it.card)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, BuyCardSuccessActivity::class.java)
                        intent.putExtra(Constant.DATA_1, it.card)
                        startActivity(intent)
                    }
                }
                else -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.card?.vnpUrl))
                    startActivity(browserIntent)
                }
            }
        })

        viewModel.errorType.observe(this, { typeMessage ->
            when (typeMessage) {
                Constant.ERROR_UNKNOW -> {
                    imgError.setImageResource(R.drawable.ic_error_request)
                    tvMessageError.text = "Không thể truy cập. Vui lòng thử lại sau"
                }
                Constant.ERROR_EMPTY -> {
                    DialogHelper.showNotification(this, null, R.string.khong_the_truy_cap_vui_long_thu_lai_sau, null, false, object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
                }
                Constant.ERROR_INTERNET -> {
                    imgError.setImageResource(R.drawable.ic_error_network)
                    tvMessageError.text = "Kết nối mạng của bạn có vấn đề. Vui lòng thử lại"
                }
            }
        })

        viewModel.statusCode.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            viewModel.getListPaymentType()
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
            this@PaymentTopupActivity.showSimpleErrorToast(it)
            Handler().postDelayed({
                this@PaymentTopupActivity.onBackPressed()
            }, 2000)
        })
    }

    private fun listener() {
        btnAcceptPayment.setOnClickListener {
            if (!adapterPaymentType.selectedItem?.agent?.code.isNullOrEmpty()) {
                when (adapterPaymentType.selectedItem?.agent?.code) {
                    "ICHECK-XU" -> {
                        if (sodutaikhoan != null && mValue != null) {
                            if (sodutaikhoan!! >= mValue!!) {

                                if (mType == 1) {
                                    mPhone?.let { phone -> viewModel.rechargeCard(mType, serviceId!!, mValue!!, adapterPaymentType.selectedItem?.agent?.code!!, phone) }
                                } else {
                                    viewModel.buyTopup(mType, serviceId!!, mValue!!, adapterPaymentType.selectedItem?.agent?.code!!)
                                }
                            } else {
                                onItemClick(1, null)
                            }
                        }
                    }
                    else -> {
                        if (mType == 1) {
                            viewModel.vnpayCard(adapterPaymentType.selectedItem?.agent?.code!!, mType, mValue!!, "icheck://recharge_card_success", mPhone, serviceId!!, "rechargecard")
                        } else {
                            viewModel.vnpayCard(adapterPaymentType.selectedItem?.agent?.code!!, mType, mValue!!, "icheck://buy_card_success", null, serviceId!!, "buycard")
                        }
                    }
                }
            }
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getCoin() {
        viewModel.getCoin().observe(this, Observer {
            if (it.status == Status.SUCCESS) {
                SessionManager.setCoin(it.data?.data?.availableBalance ?: 0)
                sodutaikhoan = it.data?.data?.availableBalance ?: 0
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        RechargePhoneActivity.listActivities.remove(this)
    }

    override fun onItemClick(position: Int, item: ICRechargePhone?) {
        when (item?.code) {
            "ICHECK-XU" -> {
                if (sodutaikhoan != null && mValue != null) {
                    if (sodutaikhoan!! >= mValue!!) {
                        imgErrorCoin.visibility = View.INVISIBLE

                        btnAcceptPayment.background = ContextCompat.getDrawable(this, R.drawable.bg_corners_4_light_blue_solid)
                        btnAcceptPayment.isEnabled = true
                    } else {
                        imgErrorCoin.visibility = View.VISIBLE
                        Handler().postDelayed({
                            imgErrorCoin.visibility = View.INVISIBLE
                        }, 3000)

                        imgErrorCoin.setOnClickListener {
                            startActivity<ListCampaignActivity>()
                        }

                        btnAcceptPayment.background = ContextCompat.getDrawable(this, R.drawable.bg_gray_b4_corners_4)
                        btnAcceptPayment.isEnabled = false
                    }
                }
                tvTotalMoney.text = TextHelper.formatMoneyPhay(mValue) + " Xu"
            }
            else -> {
                imgErrorCoin.visibility = View.INVISIBLE
                btnAcceptPayment.background = ContextCompat.getDrawable(this, R.drawable.bg_corners_4_light_blue_solid)
                btnAcceptPayment.isEnabled = true
                tvTotalMoney.text = TextHelper.formatMoneyPhay(mValue) + " đ"
            }
        }
    }
}
