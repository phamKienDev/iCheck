package vn.icheck.android.screen.user.product_of_shop_history

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_product_of_shop_history.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.history.ICProductOfShopHistory
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

class ProductOfShopHistoryActivity : BaseActivityMVVM(), ProductOfShopHistoryView {

    val viewModel: ProductOfShopHistoryViewModel by viewModels()

    private val adapter = ProductOfShopHistoryAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_of_shop_history)
        initSwipeLayout()
        initRecycleView()
        initViewModel()
        listener()
        getData()
    }

    @SuppressLint("SetTextI18n")
    private fun initViewModel() {
        viewModel.dataIntent.observe(this, {
            txtTitle.setText(R.string.cua_hang_s, it.name)
        })

        adapter.disableLoadMore()

        viewModel.isLoadMoreData.observe(this, {
            adapter.removeDataWithoutUpdate()
        })

        viewModel.listData.observe(this, {
            swipe_layout.isRefreshing = false
            if (!it.isNullOrEmpty()) {
                adapter.addListData(it)
            } else {
                adapter.setErrorCode(Constant.ERROR_EMPTY)
            }
        })

        viewModel.statusCode.observe(this, {
            swipe_layout.isRefreshing = false
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                        }

                        override fun onAgree() {
                            getData()
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
            swipe_layout.isRefreshing = false
            when (it) {
                Constant.ERROR_EMPTY -> {
                    adapter.setErrorCode(Constant.ERROR_EMPTY)
                }

                Constant.ERROR_SERVER -> {
                    adapter.setErrorCode(Constant.ERROR_SERVER)
                }

                Constant.ERROR_INTERNET -> {
                    adapter.setErrorCode(Constant.ERROR_INTERNET)
                }
            }
        })
    }

    private fun getData() {
        swipe_layout.isRefreshing = true
        viewModel.getDataIntent(intent)
    }

    private fun initSwipeLayout() {
        swipe_layout.setColorSchemeColors(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.colorPrimary), ContextCompat.getColor(ICheckApplication.getInstance(), R.color.colorPrimary), ContextCompat.getColor(ICheckApplication.getInstance(), R.color.colorPrimary))

        swipe_layout.setOnRefreshListener {
            getData()
        }
    }

    private fun initRecycleView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onClickItem(item: ICProductOfShopHistory) {
        if (!item.barcode.isNullOrEmpty()) {
            IckProductDetailActivity.start(this, item.barcode!!)
        }
    }

    override fun onLoadMore() {
        viewModel.getProductOfShopHistory(viewModel.idShop, true)
    }

    override fun onRefresh() {
        getData()
    }
}