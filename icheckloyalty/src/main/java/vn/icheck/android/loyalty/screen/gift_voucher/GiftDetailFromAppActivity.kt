package vn.icheck.android.loyalty.screen.gift_voucher

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_gift_detail_from_app.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.dialog.ConfirmLoyaltyDialog
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.model.ICKGift
import vn.icheck.android.loyalty.screen.loyalty_customers.accept_ship_gift.AcceptShipGiftActivity

/**
 * Happy new year
 * 00:00 ngày 01/01/2021
 * Phạm Hoàng Phi Hùng
 */
class GiftDetailFromAppActivity : BaseActivityGame() {

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
        viewModel.onErrorString.observe(this@GiftDetailFromAppActivity, Observer {
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

        viewModel.onError.observe(this@GiftDetailFromAppActivity, Observer {
            swipeLayout.isRefreshing = false
            showLongError(it.title)
        })

        viewModel.onActionSuccess.observe(this@GiftDetailFromAppActivity, Observer {
            getData()
        })

        viewModel.onSuccess.observe(this@GiftDetailFromAppActivity, { obj ->
            swipeLayout.isRefreshing = false
            txtTitle.text = obj.name ?: "Chi tiết quà"

            setUpButton(obj)

            adapter.setData(obj)
        })
    }

    private fun setUpButton(obj: ICKGift) {
        when (obj.rewardType) {
            "spirit" -> {
                layoutButton.setGone()
            }
            "product" -> {
                if (obj.state == 1) {
                    layoutButton.setVisible()
                } else {
                    layoutButton.setGone()
                }
            }
            "PRODUCT_IN_SHOP" -> {
                layoutButton.setGone()
            }
            "CARD" -> {
                layoutButton.setGone()
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
            startActivity(Intent(this@GiftDetailFromAppActivity, AcceptShipGiftActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_2, obj.id)
                putExtra(ConstantsLoyalty.TYPE, 2)
            })
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getDataIntent(intent)
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.BACK) {
            getData()
        } else {
            super.onMessageEvent(event)
        }
    }
}