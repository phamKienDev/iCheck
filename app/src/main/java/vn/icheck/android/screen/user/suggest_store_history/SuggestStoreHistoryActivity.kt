package vn.icheck.android.screen.user.suggest_store_history

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_suggest_store_history.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.history.ICSuggestStoreHistory
import vn.icheck.android.screen.user.map_scan_history.MapScanHistoryActivity
import vn.icheck.android.screen.user.product_of_shop_history.ProductOfShopHistoryActivity
import java.io.Serializable

class SuggestStoreHistoryActivity : BaseActivityMVVM(), SuggestStoreHistoryView {

    val viewModel: SuggestStoreHistoryViewModel by viewModels()

    private val adapter = SuggestStoreHistoryAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_store_history)
        initView()
        initSwipeLayout()
        initRecycleView()
        initViewModel()
        listener()
        getData()
    }

    private fun initView() {
        txtTitle.setText(R.string.goi_y_cua_hang_danh_cho_ban)
    }

    private fun initViewModel() {
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
        viewModel.getSuggestStore()
    }

    private fun initSwipeLayout() {
        val swipeColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipe_layout.setColorSchemeColors(swipeColor, swipeColor, swipeColor)

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

    override fun onClickGotoMap(item: ICSuggestStoreHistory) {
        MapScanHistoryActivity.start(this,
                shopID = item.id,
                shopLat = item.location?.lat,
                shopLng = item.location?.lon,
                shopAvatar = item.avatar)

//        val intent = Intent(this, MapScanHistoryActivity::class.java)
//        intent.putExtra(Constant.DATA_2,    item.id)
//        intent.putExtra(Constant.DATA_3,    item.location?.lat)
//        intent.putExtra(Constant.DATA_4,    item.location?.lon)
//        intent.putExtra("avatarShop", item.avatar)
//        startActivity(intent)
    }

    override fun onClickShowProduct(item: ICSuggestStoreHistory) {
        startActivity<ProductOfShopHistoryActivity, Serializable>(Constant.DATA_1, item)
    }

    override fun onLoadMore() {
        viewModel.getSuggestStore(true)
    }

    override fun onRefresh() {
        getData()
    }
}