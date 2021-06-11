package vn.icheck.android.screen.user.cart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.confirm.ConfirmDialog
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.room.entity.ICCart
import vn.icheck.android.screen.user.cart.adapter.CartParentAdapter
import vn.icheck.android.screen.user.cart.presenter.CartPresenter
import vn.icheck.android.screen.user.cart.view.ICartView
import vn.icheck.android.screen.user.checkoutcart.CheckoutCartActivity

/**
 * Created by VuLCL on 12/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CartActivity : BaseActivityMVVM(), ICartView {
    private val adapter = CartParentAdapter(this)
private val presenter = CartPresenter(this@CartActivity)
    private val requestCheckout = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        onInitView()
    }

    fun onInitView() {
        setupToolbar()
        setupRecyclerView()
        setupSwipeLayout()
        updateTotalMoney()
        initListener()

        presenter.getCartOffline()
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.gio_hang)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgAction.visibility = View.VISIBLE
        imgAction.setImageResource(R.drawable.ic_home_blue_24px)

        imgAction.setOnClickListener {
            setResult(RESULT_OK)
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
    }

    private fun setupSwipeLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorPrimary))

        swipeLayout.setOnRefreshListener {
            presenter.getCartOnline()
        }

        swipeLayout.post {
            getData()
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        presenter.getCartOnline()
    }

    private fun initListener() {
        tvOrder.setOnClickListener {
            if (!swipeLayout.isRefreshing) {
                if (adapter.isExistCart) {
                    startActivityForResult<CheckoutCartActivity>(requestCheckout)
                } else {
                    DialogHelper.showNotification(this@CartActivity, null, R.string.thong_bao_chua_chon_san_pham_de_dat_hang, R.string.dong_y)
                }
            }
        }
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {

    }

    override fun onSetError(icon: Int, message: String) {
        swipeLayout.isRefreshing = false

        if (adapter.isEmpty) {
            adapter.setError(R.drawable.ic_error_network, message, R.string.thu_lai)
        } else {
            showShortError(message)
        }
    }

    override fun onSetListCart(list: MutableList<ICCart>) {
        swipeLayout.isRefreshing = false

        adapter.setMessage(R.drawable.ic_list_empty_130_115dp, getString(R.string.gio_hang_trong), null)
        adapter.setListData(list)

        updateTotalMoney()
    }

    override fun onChangeQuantity(obj: ICItemCart, count: Int, parentPosition: Int, childPosition: Int) {
        if (swipeLayout.isRefreshing) {
            onRefreshCart(obj, parentPosition, childPosition)
            return
        }

        if (count > 0) {
            presenter.updateItemQuantity(obj, count, parentPosition, childPosition)
        } else {
            object : ConfirmDialog(this@CartActivity, null, getString(R.string.ban_muon_xoa_xxx_khoi_gio_hang, obj.name), null, null, true) {
                override fun onDisagree() {
                    onRefreshCart(obj, parentPosition, childPosition)
                }

                override fun onAgree() {
                    presenter.updateItemQuantity(obj, count, parentPosition, childPosition)
                }

                override fun onDismiss() {
                    onRefreshCart(obj, parentPosition, childPosition)
                }
            }.show()
        }
    }

    override fun onNotEnoughInStock() {
        DialogHelper.showNotification(this@CartActivity, R.string.so_luong_san_pham_trong_kho_khong_du)
    }

    override fun onUpdateCart(obj: ICItemCart, parentPosition: Int, childPosition: Int) {
        recyclerView.findViewHolderForAdapterPosition(parentPosition)?.let {
            if (it is CartParentAdapter.ViewHolder) {
                it.updateQuantity(childPosition)
                updateTotalMoney()
            }
        }
    }

    override fun onRefreshCart(obj: ICItemCart, parentPosition: Int, childPosition: Int) {
        recyclerView.findViewHolderForAdapterPosition(parentPosition)?.let {
            if (it is CartParentAdapter.ViewHolder) {
                CartParentAdapter.quantityCart.remove(obj.item_id)
                it.refreshQuantity(childPosition)
                updateTotalMoney()
            }
        }
    }

    override fun onRemoveCart(parentPosition: Int, childPosition: Int) {
        recyclerView.findViewHolderForAdapterPosition(parentPosition)?.let {
            if (it is CartParentAdapter.ViewHolder) {
                it.removeCart(childPosition)
                updateTotalMoney()
            }
        }
    }

    override fun updateTotalMoney() {
        lifecycleScope.launch {
            val money = adapter.getTotalPrice

            tvTotal.text = getString(R.string.tong_thanh_toan_xxx_d, TextHelper.formatMoney(money))

            tvOrder.isEnabled = money > 0L

            layoutBottom.visibility = if (adapter.getListData.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }
    }

    override fun onSkipCart(cartID: Long, position: Int) {
        DialogHelper.showConfirm(this@CartActivity, R.string.thong_bao_bo_qua_san_pham_trong_gio_hang, R.string.khong, null, object : ConfirmDialogListener {
            override fun onDisagree() {

            }

            override fun onAgree() {
                adapter.skipCart(cartID, position)
                updateTotalMoney()
            }
        })
    }

    override fun onClickShopDetail(shopId: Long) {
    }

    override fun onClickShopProductDetail(productId: Long) {
//        ShopProductDetailActivity.startShop = false
//        startActivity<ShopProductDetailActivity, Long>(Constant.DATA_1, productId)
    }

    override fun showError(errorMessage: String) {

        swipeLayout.isRefreshing = false
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@CartActivity

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@CartActivity, isShow)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestCheckout) {
                finishActivity(null, null)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.disposeApi()
        CartParentAdapter.skipCart.clear()
        CartParentAdapter.quantityCart.clear()
    }
}