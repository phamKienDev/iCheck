package vn.icheck.android.screen.user.buy_mobile_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.btnAgainError
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.btnPayment
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.constraintLayout10
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.imgActionHistoryTopup
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.imgBack
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.imgError
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.layoutError
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.nestedScrollView
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.rcvCardValue
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.rcvNetwork
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.tvMessageError
import kotlinx.android.synthetic.main.activity_buy_mobile_card_v2.tvPrice
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.OnItemClickListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.recharge_phone.ICMenhGia
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.history_loading_card.home.HistoryCardActivity
import vn.icheck.android.screen.user.payment_topup.PaymentTopupActivity
import vn.icheck.android.screen.user.recharge_phone.adapter.MenhGiaTheAdapter
import vn.icheck.android.screen.user.recharge_phone.adapter.NetworkAdapter
import vn.icheck.android.screen.user.recharge_phone.viewmodel.RechargePhoneVIewModel
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.rText

class BuyMobileCardV2Activity : BaseActivityMVVM() {

    companion object {
        val listActivities = mutableListOf<AppCompatActivity>()
    }

    private var listBuyTopup = mutableListOf<ICRechargePhone>()
    private var listMenhGia = ArrayList<ICMenhGia>()
    private var value: String = ""
    private var serviceId: Long = -1L
    private var card: ICRechargePhone? = null
    private var mIcheckPoint: Long? = null

    private lateinit var viewModel: RechargePhoneVIewModel

    private lateinit var nhaMangAdapter: NetworkAdapter
    private val menhGiaAdapter = MenhGiaTheAdapter(listMenhGia)

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_mobile_card_v2)
        viewModel = ViewModelProvider(this).get(RechargePhoneVIewModel::class.java)
        listenerGetData()
        initListNetworkOperator()
        initListMenhGia()
        displayData()
        listener()
        TrackingAllHelper.trackIcheckPhoneTopupStart()
    }

    private fun listenerGetData() {
        viewModel.getData(2)

        viewModel.data.observe(this, Observer { obj ->
            if (obj.isNullOrEmpty()) {
                nestedScrollView.visibility = View.GONE
                constraintLayout10.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
            } else {
                layoutError.visibility = View.GONE
                nestedScrollView.visibility = View.VISIBLE
                constraintLayout10.visibility = View.VISIBLE
                listBuyTopup.clear()
                listBuyTopup.addAll(obj)
                nhaMangAdapter.notifyDataSetChanged()
                selectNetWork(0)
            }
        })

        viewModel.errorType.observe(this, Observer { typeMessage ->
            layoutError.visibility = View.VISIBLE
            nestedScrollView.visibility = View.GONE
            constraintLayout10.visibility = View.GONE
            when (typeMessage) {
                Constant.ERROR_UNKNOW -> {
                    imgError.setImageResource(R.drawable.ic_error_request)
                    tvMessageError rText R.string.khong_the_truy_cap_vui_long_thu_lai_sau
                }
                Constant.ERROR_EMPTY -> {
                    imgError.setImageResource(R.drawable.ic_error_request)
                    tvMessageError rText R.string.khong_the_truy_cap_vui_long_thu_lai_sau
                }
                Constant.ERROR_INTERNET -> {
                    imgError.setImageResource(R.drawable.ic_error_network)
                    tvMessageError rText R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai
                }
            }
        })

        viewModel.statusCode.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            viewModel.getData(2)
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
            showShortError(it)
        })
    }

    private fun initListNetworkOperator() {
        nhaMangAdapter = NetworkAdapter(rcvNetwork, listBuyTopup)

        rcvNetwork.layoutManager = object : GridLayoutManager(this, 3, VERTICAL, false) {
            override fun canScrollVertically(): Boolean = false
        }
        rcvNetwork.adapter = nhaMangAdapter

        nhaMangAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(itemView: View, position: Int) {
                selectNetWork(position)
                for (temp in listBuyTopup) {
                    temp.select = false
                }
                listBuyTopup[position].select = true
                nhaMangAdapter.notifyDataSetChanged()
                tvPrice.text = TextHelper.formatMoneyPhay(listMenhGia[0].menhGia.toLong()) + "đ"
            }
        })
    }

    private fun initListMenhGia() {
        rcvCardValue.layoutManager = object : GridLayoutManager(this, 3, VERTICAL, false) {
            override fun canScrollVertically(): Boolean = false
        }
        rcvCardValue.adapter = menhGiaAdapter

        menhGiaAdapter.setOnItemClickListener(object : OnItemClickListener {
            @SuppressLint("SetTextI18n")
            override fun onItemClick(itemView: View, position: Int) {
                for (temp in listMenhGia) {
                    temp.select = false
                }
                value = listMenhGia[position].menhGia
                listMenhGia[position].select = true
                menhGiaAdapter.notifyDataSetChanged()
                tvPrice.text = TextHelper.formatMoneyPhay(listMenhGia[position].menhGia.toLong()) + "đ"
            }
        })
    }

    private fun listener() {
        imgActionHistoryTopup.setOnClickListener {
            startActivity<HistoryCardActivity, Int>(Constant.DATA_1, 0)
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnPayment.setOnClickListener {
            if (card != null) {
                if (value.isNotEmpty()) {
                    if (!SessionManager.session.user?.phone.isNullOrEmpty()) {
                        val intent = Intent(this, PaymentTopupActivity::class.java)
                        intent.putExtra("type", 2)
                        intent.putExtra("value", value)
//                        intent.putExtra("serviceId",serviceId)
                        card?.let { card ->
                            intent.putExtra("card", JsonHelper.toJson(card))
                        }
//                        intent.putExtra("point", mIcheckPoint)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, PaymentTopupActivity::class.java)
                        intent.putExtra("type", 2)
                        intent.putExtra("value", value)
//                        intent.putExtra("serviceId",serviceId)
                        card?.let { card ->
                            intent.putExtra("card", JsonHelper.toJson(card))
                        }
//                        intent.putExtra("point", mIcheckPoint)
                        startActivity(intent)
                    }
                }
            }
        }

        btnAgainError.setOnClickListener {
            viewModel.getData(2)
        }
    }

    private fun displayData() {
        if (listBuyTopup.isNotEmpty()) {
            listBuyTopup[0].select = true
            card = listBuyTopup[0]

            listMenhGia.clear()

            for (i in 0 until (listBuyTopup[0].denomination as MutableList<String>).size) {
                val temp = ICMenhGia((listBuyTopup[0].denomination as MutableList<String>)[i])
                listMenhGia.add(temp)
            }
        }

        nhaMangAdapter.notifyDataSetChanged()
        menhGiaAdapter.notifyDataSetChanged()
    }

    private fun selectNetWork(position: Int) {
        for (temp in listBuyTopup) {
            temp.select = false
        }

        listBuyTopup[position].select = true

        try {
            value = ""
            card = listBuyTopup[position]
            try {
                listMenhGia.clear()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            listMenhGia.clear()

            (listBuyTopup[position].denomination as MutableList<String>).let {
                for (i in 0 until it.size) {
                    val temp = ICMenhGia(it[i])
                    listMenhGia.add(temp)
                }
                listMenhGia[0].select = true
                value = listMenhGia[0].menhGia
                tvPrice.text = TextHelper.formatMoneyPhay(listMenhGia[0].menhGia.toLong()) + "đ"
            }
            menhGiaAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.REFRESH_DATA -> {
                tvPrice.text = "0đ"
                selectNetWork(0)
            }
        }
    }
}