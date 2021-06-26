package vn.icheck.android.screen.user.icheckstore.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_list_product_store.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICStoreiCheck
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.screen.user.icheckstore.view.IGiftStoreView
import vn.icheck.android.screen.user.icheckstore.list.adapter.ListProductStoreAdapter
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.ui.layout.ItemDecorationAlbumColumns
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.ToastUtils


class ProductStoreiCheckActivity : BaseActivityMVVM(), IGiftStoreView, IRecyclerViewCallback {

    private val adapter = ListProductStoreAdapter(this, this)

    private val viewModel by viewModels<ListProductStoreViewModel>()

    private var pointRemaining: Long? = null
    private lateinit var cartLauncher: ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        cartLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == RESULT_OK) {
//                it.data?.getLongExtra("id", 0L)?.let { id ->
//                    SuccessConfirmShipDialog(id).apply {
//                        isCancelable = false
//                    }.show(supportFragmentManager, null)
//                }
//            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product_store)

        initToolbar()
        initRecyclerView()
        initSwipe()
        initListener()
        initLayoutAddToCart()

        pointRemaining = SettingManager.getUserCoin
        TrackingAllHelper.trackIcheckStoreClick()
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.setText(R.string.gian_hang_icheck)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.setLoadMore(position) || adapter.listData.isNullOrEmpty()) {
                        2
                    } else {
                        1
                    }
                }
            }
        }
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(ItemDecorationAlbumColumns(
                SizeHelper.size1,
                resources.getInteger(R.integer.list_preview_columns)))
    }

    private fun initSwipe() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            getData()
        }

        swipeLayout.post {
            getData()
        }
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getListStore(false)
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListStore(true)
    }

    private fun initListener() {
        viewModel.onError.observe(this, {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                ToastUtils.showLongError(this@ProductStoreiCheckActivity, it.message)
            }
        })

        viewModel.onSetData.observe(this@ProductStoreiCheckActivity, {
            swipeLayout.isRefreshing = false
            adapter.setData(it)
        })

        viewModel.onAddData.observe(this@ProductStoreiCheckActivity, {
            adapter.addData(it)
        })

        viewModel.onErrorEmpty.observe(this@ProductStoreiCheckActivity, {
            swipeLayout.isRefreshing = false
            adapter.setError("", R.drawable.ic_error_emty_history_topup)
        })

        viewModel.onErrorString.observe(this, {
            showLongError(it)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initLayoutAddToCart() {
        if (!AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().getAll().isNullOrEmpty()) {
            layoutAddToCart.beVisible()
        } else {
            layoutAddToCart.beGone()
        }

        layoutAddToCart.setOnClickListener {
            cartLauncher.launch(Intent(this, ShipActivity::class.java).apply {
                putExtra(Constant.CART, true)
            })
        }

        tvPrice.setText(R.string.s_xu, TextHelper.formatMoneyPhay(viewModel.getTotalPrice()))

        tvCountProduct.setText(R.string.d_san_pham, viewModel.getCount())
    }

    override fun onClickItem(item: ICStoreiCheck) {
        TrackingAllHelper.tagIcheckItemView(item)
        IckProductDetailActivity.start(this@ProductStoreiCheckActivity, item.originId ?: -1)
    }

    override fun onExchangeGift(item: ICStoreiCheck) {
        viewModel.addToCart(item)
    }

    override fun onLogin() {
        onRequireLogin()
    }

    @SuppressLint("SetTextI18n")
    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (event.type == ICMessageEvent.Type.UPDATE_PRICE) {
            if (!AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().getAll().isNullOrEmpty()) {
                layoutAddToCart.beVisible()
            } else {
                layoutAddToCart.beGone()
            }

            tvPrice.setText(R.string.s_xu,TextHelper.formatMoneyPhay(viewModel.getTotalPrice()))

            tvCountProduct.setText(R.string.d_san_pham, viewModel.getCount())

            getData()
        }
    }
}