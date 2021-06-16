package vn.icheck.android.screen.user.checkoutcart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_checkout_cart.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.user.checkoutcart.adapter.CheckoutCartAdapter
import vn.icheck.android.screen.user.checkoutcart.dialog.SelectShippingDialog
import vn.icheck.android.screen.user.checkoutcart.entity.Checkout
import vn.icheck.android.screen.user.checkoutcart.presenter.CheckoutCartPresenter
import vn.icheck.android.screen.user.checkoutcart.view.ICheckoutCartView
import vn.icheck.android.screen.user.createuseraddress.CreateUserAddressActivity
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity
import vn.icheck.android.screen.user.selectuseraddress.SelectUserAddressActivity
import vn.icheck.android.util.kotlin.ActivityUtils

/**
 * Created by VuLCL on 12/24/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CheckoutCartActivity : BaseActivityMVVM(), ICheckoutCartView {
    private val adapter = CheckoutCartAdapter(this)
    private val presenter = CheckoutCartPresenter(this@CheckoutCartActivity)
    private val requestCreateUserAddress = 1
    private val requestSelectUserAddress = 2
    private val requestViewShop = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_checkout_cart)
        onInitView()
    }

    fun onInitView() {
        setupToolbar()
        setupView()
        setupRecyclerView()
        setupListener()

    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.thanh_toan)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        btnDone.background = ViewHelper.btnSecondaryCorners26(this)
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter

        recyclerView?.post {
            presenter.checkAction()
        }
    }

    private fun setupListener() {
        btnDone.setOnClickListener {
            presenter.completeCheckouts(adapter.getListData)
        }
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@CheckoutCartActivity, isShow)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onCheckoutError(error: String, body: ICReqCheckout?, isFirstCreate: Boolean) {
        DialogHelper.showConfirm(this@CheckoutCartActivity, error, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                if (body != null) {
                    presenter.createCheckouts(body, isFirstCreate)
                } else {
                    presenter.checkAction()
                }
            }
        })
    }

    override fun onSetCheckout(list: MutableList<Checkout>, grandTotal: Long) {
        adapter.setListData(list)
        tvMoney?.text = getString(R.string.xxx_d, TextHelper.formatMoney(grandTotal))
    }

    override fun onMessageClicked() {
        presenter.checkAction()
    }

    override fun onAddUserAddress() {
        startActivityForResult<CreateUserAddressActivity>(requestCreateUserAddress)
    }

    override fun onSelectUserAddress(addressID: Long) {
        val intent = Intent(this, SelectUserAddressActivity::class.java)
        intent.putExtra(Constant.DATA_1, presenter.getListAddressJson)
        intent.putExtra(Constant.DATA_2, addressID)
        ActivityUtils.startActivityForResult(this, intent, requestSelectUserAddress)
    }

    override fun onChangeUserAddress(addressID: Long) {
        presenter.createCheckouts(addressID, null, null, null, adapter.getListData, true)
    }

    override fun onChangeShippingUnit(shopID: Long, shippingID: Int, list: MutableList<ICShipping>) {
        object : SelectShippingDialog(this@CheckoutCartActivity, shippingID, list) {
            override fun onSelected(obj: ICShipping) {
                presenter.createCheckouts(null, null, shopID, obj.id, this@CheckoutCartActivity.adapter.getListData, false)
            }
        }.show()
    }

    override fun onChangePayment(obj: ICPayment) {
        presenter.createCheckouts(null, obj.id, null, null, adapter.getListData, false)
    }

    override fun onCompleteCheckoutError(error: String) {
        DialogHelper.showNotification(this@CheckoutCartActivity, error, true)
    }

    override fun onCompleteCheckoutSuccess(url: String?) {
        if (url.isNullOrEmpty()) {
            showLongSuccess(R.string.dat_hang_thanh_cong)
            startActivity<OrderHistoryActivity>()
        } else {
            startActivity<OrderHistoryActivity>()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
        finishActivity(Activity.RESULT_OK, null)
    }

    override fun onShopClicked(shopID: Long) {
    }

    override fun onProductClicked(obj: ICItemCart) {
//        ShopProductDetailActivity.startShop = false
//        startActivity<ShopProductDetailActivity, Long>(Constant.DATA_1, obj.product_id)
    }

    override fun showError(errorMessage: String) {

        if (adapter.isEmpty) {
            showShortError(errorMessage)
            adapter.notifyDataSetChanged()
        } else {
            adapter.setError(0, errorMessage, R.string.thu_lai)
        }
    }

    override val mContext: Context
        get() = this@CheckoutCartActivity

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestSelectUserAddress -> {
                    presenter.selectAddress(data)
                }
                requestCreateUserAddress -> {
                    presenter.createAddress(data)
                }
            }
        }

        if (requestCode == requestViewShop) {
            presenter.refreshData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.disposeApi()
    }
}