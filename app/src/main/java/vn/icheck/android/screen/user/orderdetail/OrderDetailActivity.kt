package vn.icheck.android.screen.user.orderdetail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICOrderDetail
import vn.icheck.android.screen.user.cart.CartActivity
import vn.icheck.android.screen.user.orderdetail.adapter.OrderDetailAdapter
import vn.icheck.android.screen.user.orderdetail.dialog.OrderDetailCancelDetailDialog
import vn.icheck.android.screen.user.orderdetail.dialog.OrderDetailCancelOrderDialog
import vn.icheck.android.screen.user.orderdetail.dialog.OrderDetailStatusHistoryDialog
import vn.icheck.android.screen.user.orderdetail.presenter.OrderDetailPresenter
import vn.icheck.android.screen.user.orderdetail.view.IOrderDetailView
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity
import vn.icheck.android.screen.user.shopreview.OrderReviewActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 1/31/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class OrderDetailActivity : BaseActivity<OrderDetailPresenter>(), IOrderDetailView, View.OnClickListener {
    private val adapter = OrderDetailAdapter(this)

    override val getLayoutID: Int
        get() = R.layout.activity_order_detail

    override val getPresenter: OrderDetailPresenter
        get() = OrderDetailPresenter(this)

    override fun onInitView() {
        setupToolbar()
        setupRecyclerView()
        WidgetUtils.setClickListener(this, btnActionOne, btnActionTwo)
        presenter.getID(intent)

        btnActionTwo.background=ViewHelper.bgWhitePressStroke1Radius36(this)
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.don_hang)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
    }

    override fun onGetIDError() {
        DialogHelper.showNotification(this@OrderDetailActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onGetDetailError(error: String) {
        DialogHelper.showConfirm(this@OrderDetailActivity, error, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                presenter.getOrderDetail(true)
            }
        })
    }

    override fun onDetailSuccess(obj: ICOrderDetail, list: MutableList<Int>) {
        layoutBottom.visibility = View.VISIBLE
        btnActionTwo.visibility = View.VISIBLE

        when (obj.status) {
            OrderHistoryActivity.waitForPay -> {
                btnActionTwo.setText(R.string.thanh_toan)
            }
            OrderHistoryActivity.waitForConfirmation -> {
                btnActionTwo.setText(R.string.huy_don)
            }
            OrderHistoryActivity.delivery -> {
                btnActionTwo.setText(R.string.nhan_tin)
            }
            OrderHistoryActivity.delivered -> {
                btnActionTwo.setText(R.string.danh_gia)
            }
            OrderHistoryActivity.canceled -> {
                val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                layoutParams.setMargins(SizeHelper.size12, SizeHelper.size4, SizeHelper.size12, SizeHelper.size4)
                layoutStart.layoutParams = layoutParams

                layoutEnd.visibility = View.GONE
//                btnActionTwo.setText(R.string.dat_lai_don_nay)
            }
            OrderHistoryActivity.error -> {
                btnActionTwo.setText(R.string.huy_don)
            }
            OrderHistoryActivity.refund -> {
                val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                layoutParams.setMargins(SizeHelper.size12, SizeHelper.size4, SizeHelper.size12, SizeHelper.size4)
                layoutStart.layoutParams = layoutParams

                layoutEnd.visibility = View.GONE
            }
        }

        txtTitle.text = getString(R.string.don_hang_xxx, obj.number)
        adapter.setMessage(0, "", null)
        adapter.setData(obj, list)
    }

    private fun openStatusHistory() {
        adapter.getOrderDetail?.let { orderDetail ->
            object : OrderDetailStatusHistoryDialog(this@OrderDetailActivity, orderDetail.status, orderDetail.status_history) {
                override fun onDoneOrder() {
                    adapter.getOrderDetail?.let {
                        presenter.completeOrder(it.id)
                    }
                }
            }.show()
        }
    }

    private fun viewShop() {
        adapter.getOrderDetail?.shop_id?.let {
        }
    }

    private fun cancelOrder() {
        adapter.getOrderDetail?.let {
            object : OrderDetailCancelOrderDialog(this@OrderDetailActivity, it) {
                override fun onDone(orderID: Long, reason: String) {
                    presenter.cancelOrder(orderID, reason)
                }
            }.show()
        }
    }

    private fun ratingShop() {
        adapter.getOrderDetail?.shop_id?.let {
            startActivity<OrderReviewActivity, Long>(Constant.DATA_1, it)
        }
    }

    override fun onTryAgainClicked() {
        presenter.getOrderDetail(true)
    }

    override fun onActionClicked(status: Int) {
        when (status) {
            OrderHistoryActivity.waitForConfirmation -> {
                openStatusHistory()
            }
            OrderHistoryActivity.waitForPay -> {
                openStatusHistory()
            }
            OrderHistoryActivity.delivery -> {
                openStatusHistory()
            }
            OrderHistoryActivity.delivered -> {
                openStatusHistory()
            }
            OrderHistoryActivity.canceled -> {
                adapter.getOrderDetail?.let { orderDetail ->
                    object : OrderDetailCancelDetailDialog(this@OrderDetailActivity, orderDetail) {
                        override fun onOrderAgaign(obj: ICOrderDetail) {
                            presenter.orderAgain(obj)
                        }
                    }.show()
                }
            }
            OrderHistoryActivity.error -> {
                openStatusHistory()
            }
            OrderHistoryActivity.refund -> {
                openStatusHistory()
            }
        }
    }

    override fun onTrackingClicked(link: String) {
        startActivity<WebViewActivity>(Constant.DATA_1, link)
    }

    override fun onGoToShop(shopID: Long) {
    }

    override fun onPaySuccess(url: String?) {
        if (!url.isNullOrEmpty()) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }

        setResult(Activity.RESULT_OK)
        onBackPressed()
    }

    override fun onCancelSuccess(orderID: Long) {
        val intent = Intent()
        intent.putExtra(Constant.DATA_1, orderID)
        setResult(Activity.RESULT_OK, intent)
    }

    override fun onCompleteSuccess(orderID: Long) {
        val intent = Intent()
        intent.putExtra(Constant.DATA_1, orderID)
        setResult(Activity.RESULT_OK, intent)
    }

    override fun onShowNotification(title: String?, message: String) {
        DialogHelper.showNotification(this@OrderDetailActivity, title, message, null)
    }

    override fun onOrderAgainError() {
        DialogHelper.showConfirm(this@OrderDetailActivity, R.string.order_again_error, true, object : ConfirmDialogListener {
            override fun onDisagree() {
                presenter.stopOrderAgain()
            }

            override fun onAgree() {
                onShowLoading()
                presenter.startOrderAgain()
            }
        })
    }

    override fun onOrderAgainSuccess() {
        startActivity<CartActivity>()
    }

    override fun onClick(view: View?) {
        when (adapter.getOrderDetail?.status) {
            OrderHistoryActivity.waitForConfirmation -> {
                when (view?.id) {
                    R.id.btnActionOne -> {
                        viewShop()
                    }
                    R.id.btnActionTwo -> {
                        cancelOrder()
                    }
                }
            }
            OrderHistoryActivity.waitForPay -> {
                when (view?.id) {
                    R.id.btnActionOne -> {
                        viewShop()
                    }
                    R.id.btnActionTwo -> {
                        adapter.getOrderDetail?.let { obj ->
                            presenter.payAgain(obj.id)
                        }
                    }
                }
            }
            OrderHistoryActivity.delivery -> {
                when (view?.id) {
                    R.id.btnActionOne -> {
                        viewShop()
                    }
                    R.id.btnActionTwo -> {
                        adapter.getOrderDetail?.shop?.let { shop ->
//                            ChatV2Activity.createChatUser(shop.id, this)
                        }
                    }
                }
            }
            OrderHistoryActivity.delivered -> {
                when (view?.id) {
                    R.id.btnActionOne -> {
                        viewShop()
                    }
                    R.id.btnActionTwo -> {
                        ratingShop()
                    }
                }
            }
            OrderHistoryActivity.canceled -> {
                when (view?.id) {
                    R.id.btnActionOne -> {
                        viewShop()
                    }
                    R.id.btnActionTwo -> {
                        adapter.getOrderDetail?.let { obj ->
                            presenter.orderAgain(obj)
                        }
                    }
                }
            }
            OrderHistoryActivity.error -> {
                when (view?.id) {
                    R.id.btnActionOne -> {
                        viewShop()
                    }
                    R.id.btnActionTwo -> {
                        adapter.getOrderDetail?.let { obj ->
                            cancelOrder()
                        }
                    }
                }
            }
            OrderHistoryActivity.refund -> {
                when (view?.id) {
                    R.id.btnActionOne -> {
                        viewShop()
                    }
                }
            }
        }
    }
}