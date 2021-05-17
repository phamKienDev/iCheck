package vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_gift_shop_loyalty.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.model.LoyaltyGiftItem
import vn.icheck.android.loyalty.screen.loyalty_customers.giftdetail.GiftDetailActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.history.adapter.FilterAdapter
import vn.icheck.android.loyalty.screen.loyalty_customers.history.viewmodel.GiftShopViewModel

class GiftShopActivity : BaseActivityGame() {
    private val arrCheckBox = arrayListOf<CheckBox>()
    private val giftShopViewModel by viewModels<GiftShopViewModel>()
    private lateinit var giftShopAdapter: GiftShopAdapter
    private lateinit var filterAdapter: FilterAdapter

    override val getLayoutID: Int
        get() = R.layout.activity_gift_shop_loyalty

    @SuppressLint("SetTextI18n")
    override fun onInitView() {

        giftShopViewModel.userId = intent.getLongExtra("id", 0L)
        giftShopAdapter = GiftShopAdapter(giftShopViewModel.arrayGiftShop)
        arrCheckBox.addAll(arrayListOf(ship_gift, not_ship_gift, icoin, mobile_card, voucher))
        for (item in arrCheckBox) {
            item.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isChecked) {
                    buttonView.setTextColor(Color.parseColor("#212121"))
                } else {
                    buttonView.setTextColor(Color.parseColor("#757575"))
                }
                val arr = arrCheckBox
                        .firstOrNull {
                            it.isChecked
                        }
                giftShopViewModel.checked.postValue(arr != null)
            }
        }
        tv_reset.setOnClickListener {
            for (item in arrCheckBox) {
                item.isChecked = false
            }
        }
        textView33.setOnClickListener {
            root_motion.transitionToEnd()
        }
        filterAdapter = FilterAdapter(giftShopViewModel.arrFilter) {
            when (it) {
                giftShopViewModel.arrFilter.lastIndex -> {
                    root_motion.transitionToEnd()
                }
                0 -> {
                    arrCheckBox.firstOrNull { checkBox ->
                        checkBox.text == giftShopViewModel.arrFilter[it]
                    }?.let { checkBox ->
                        checkBox.isChecked = false
                    }
                    giftShopViewModel.arrFilter.removeAt(it)
                    filterAdapter.notifyItemRemoved(it)
                }
                else -> {
                    arrCheckBox.firstOrNull { checkBox ->
                        checkBox.text == giftShopViewModel.arrFilter[it]
                    }?.let { checkBox ->
                        checkBox.isChecked = false
                    }
                    giftShopViewModel.arrFilter.removeAt(it)
                    filterAdapter.notifyItemRemoved(it)
                }
            }
        }
        tv_filter_type.adapter = filterAdapter
        bg_dialog.setOnClickListener {
        }
        img_clear.setOnClickListener {
            if (root_motion.currentState == root_motion.endState) {
                root_motion.transitionToStart()
            }
        }
        tv_done.setOnClickListener {
            if (root_motion.currentState == root_motion.endState) {
                root_motion.transitionToStart()
            }
        }
        btn_back.setOnClickListener {
            finish()
        }
        rcv_transactions.adapter = giftShopAdapter
        giftShopViewModel.checked.observe(this, Observer {
            if (it) {
                val arr = arrCheckBox
                        .filter { checkbox ->
                            checkbox.isChecked
                        }
                        .map { checkbox ->
                            checkbox.text.toString()
                        }
                        .toList()
                giftShopViewModel.addFilter(arr)
                filterAdapter.notifyDataSetChanged()
                tv_reset.setBackgroundResource(R.drawable.bg_blue_corner_18)
                tv_reset.setTextColor(Color.parseColor("#0064B1"))
                val giftType = arr
                        .map { key ->
                            when (key) {
                                "Quà hiện vật" -> {
                                    "PRODUCT"
                                }
                                "Quà nhân tại cửa hàng" -> {
                                    "RECEIVE_STORE"
                                }
                                "Quà xu" -> {
                                    "ICOIN"
                                }
                                "Quà thẻ cào" -> {
                                    "PHONE_CARD"
                                }
                                "Quà voucher" -> {
                                    "VOUCHER"
                                }
                                else -> "PRODUCT,RECEIVE_STORE,ICOIN,PHONE_CARD,VOUCHER"
                            }
                        }
                        .toList()
                        .joinToString(separator = ",")
                getShopProducts(giftType)
            } else {
                giftShopViewModel.resetFilter()
                filterAdapter.notifyDataSetChanged()
                tv_reset.setBackgroundResource(R.drawable.bg_dark_gray_corner_18)
                tv_reset.setTextColor(Color.WHITE)
                getShopProducts()
            }
        })
        giftShopViewModel.countLiveData.observe(this, Observer {
            tv_total_transactions.text = "Cửa hàng quà tặng (${it})"
        })
        getShopProducts()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (event.type == ICMessageEvent.Type.ON_COUNT_GIFT) {
            getShopProducts()
        }
        if (event.type == ICMessageEvent.Type.OPEN_DETAIL_GIFT) {
            GiftDetailActivity.startActivityGiftDetail(this, (event.data as LoyaltyGiftItem).id
                    ?: -1, 1, 1)
        }
    }

    private fun getShopProducts(filter: String? = null) {
        lifecycleScope.launch {
            giftShopViewModel.getGiftsShopFlow(filter).collectLatest {
                giftShopAdapter.submitData(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                getShopProducts()
            }
        }
    }
}