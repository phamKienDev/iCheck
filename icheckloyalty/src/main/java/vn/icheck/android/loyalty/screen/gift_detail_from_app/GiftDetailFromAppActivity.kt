package vn.icheck.android.loyalty.screen.gift_detail_from_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_gift_detail_from_app.*
import kotlinx.android.synthetic.main.item_gift_detail_from_app.view.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.dialog.ConfirmLoyaltyDialog
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.model.ICKGift
import vn.icheck.android.loyalty.screen.loyalty_customers.accept_ship_gift.AcceptShipGiftActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard.ChangePhoneCardsActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard.ExchangePhonecardSuccessDialog
import vn.icheck.android.loyalty.screen.voucher.VoucherLoyaltyActivity

/**
 * Happy new year
 * 00:00 ngày 01/01/2021
 * Phạm Hoàng Phi Hùng
 */
class GiftDetailFromAppActivity : BaseActivityGame() {

    private val requestCard = 111

    private val viewModel by viewModels<GiftDetailFromAppViewModel>()

    private val adapter = GiftDetailFromAppAdapter()

    override val getLayoutID: Int
        get() = R.layout.activity_gift_detail_from_app

    override fun onInitView() {

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.BACK_UPDATE))
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            getData()
        }

        swipeLayout.post {
            getData()
        }
    }

    private fun initListener() {
        viewModel.onErrorString.observe(this@GiftDetailFromAppActivity, {
            swipeLayout.isRefreshing = false

            if (it.isNotEmpty()) {
                showLongError(it)
            } else {
                object : DialogNotification(this@GiftDetailFromAppActivity, null, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), "Ok", false) {
                    override fun onDone() {
                        this@GiftDetailFromAppActivity.onBackPressed()
                    }
                }.show()
            }
        })

        viewModel.onError.observe(this@GiftDetailFromAppActivity, {
            swipeLayout.isRefreshing = false
            showLongError(it.title)
        })

        viewModel.onActionSuccess.observe(this@GiftDetailFromAppActivity, {
            getData()
        })

        viewModel.onSuccess.observe(this@GiftDetailFromAppActivity, { obj ->
            swipeLayout.isRefreshing = false
            txtTitle.text = obj.name ?: "Chi tiết quà"

            setUpButton(obj)

            adapter.setData(obj)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setUpButton(obj: ICKGift) {

        when (obj.rewardType) {
            "spirit" -> {
                layoutButton.setGone()
            }
            "product" -> {
                if (obj.state == 1) {
                    layoutButton.setVisible()
                    layoutButtonNotVoucher.setVisible()
                } else {
                    layoutButton.setGone()
                }
            }
            "PRODUCT_IN_SHOP" -> {
                layoutButton.setGone()
            }
            "CARD" -> {
                if (obj.state == 4 || obj.state == 2 || obj.state == 3) {
                    layoutButton.setGone()
                } else {
                    layoutButton.setVisible()
                    layoutButtonNotVoucher.setVisible()
                }
            }
            "VOUCHER" -> {
                if (obj.voucher != null) {
                    layoutButton.setVisible()
                    layoutButtonNotVoucher.setGone()
                    btnUsed.setVisible()

                    when {
                        obj.voucher.can_use == true -> {
                            btnUsed.apply {
                                text = "Dùng ngay"

                                setOnClickListener {
                                    startActivity(Intent(this@GiftDetailFromAppActivity, VoucherLoyaltyActivity::class.java).apply {
                                        putExtra(ConstantsLoyalty.DATA_1, obj.voucher.code)
                                        putExtra(ConstantsLoyalty.DATA_2, recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.tvTimeGift?.text.toString().trim())
                                        putExtra(ConstantsLoyalty.DATA_3, obj.owner?.logo?.thumbnail)
                                    })
                                }
                            }
                        }
                        obj.voucher.can_mark_use == true -> {
                            btnUsed.apply {
                                text = "Đánh dấu sử dụng"

                                setOnClickListener {
                                    showCustomErrorToast(this@GiftDetailFromAppActivity, "Chưa có sự kiện")
                                }
                            }
                        }
                        else -> {
                            layoutButton.setGone()
                        }
                    }
                } else {
                    if (obj.state == 1) {
                        layoutButton.setVisible()
                        layoutButtonNotVoucher.setVisible()
                    } else {
                        layoutButton.setGone()
                    }
                }
            }
            else -> {
                layoutButton.setGone()
            }
        }


        btnCancel.setOnClickListener {
            object : ConfirmLoyaltyDialog(this@GiftDetailFromAppActivity, "Từ chối nhận quà", "Bạn sẽ không thể nhận quà này nếu\nxác nhận từ chối", "Hủy", "Xác nhận", false) {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    viewModel.postCancelShipGift(obj)
                }

                override fun onDismiss() {

                }
            }.show()
        }

        btnAccept.setOnClickListener {
            when (obj.rewardType) {
                "CARD" -> {
                    if (obj.id != null)
                        ChangePhoneCardsActivity.start(this, obj.id, ConstantsLoyalty.VQMM, requestCard)
                }
                "VOUCHER" -> {
                    startActivity(Intent(this@GiftDetailFromAppActivity, AcceptShipGiftActivity::class.java).apply {
                        putExtra(ConstantsLoyalty.DATA_2, obj.id)
                        putExtra(ConstantsLoyalty.TYPE, 3)
                        putExtra(ConstantsLoyalty.BACK_TO_DETAIL, "BACK_TO_DETAIL")
                    })
                }
                else -> {
                    startActivity(Intent(this@GiftDetailFromAppActivity, AcceptShipGiftActivity::class.java).apply {
                        putExtra(ConstantsLoyalty.DATA_2, obj.id)
                        putExtra(ConstantsLoyalty.TYPE, 2)
                    })
                }
            }
        }
    }


    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getDataIntent(intent)
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.BACK -> {
                getData()
            }
            ICMessageEvent.Type.BACK_UPDATE -> {
                getData()
            }
            else -> super.onMessageEvent(event)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == requestCard) {
                val phone = data?.getStringExtra("phone")
                val provider = data?.getStringExtra("provider")

                val dialog = ExchangePhonecardSuccessDialog(phone, provider)
                dialog.show(supportFragmentManager, null)

                //set lại giao diện
                layoutButton.setGone()
                adapter.listData.firstOrNull()?.let {
                    it.state = 2
                    adapter.setData(it)
                }
            }
        }

    }
}