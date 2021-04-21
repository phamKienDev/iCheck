package vn.icheck.android.screen.user.store_sell_history

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_store_sell_history.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.models.history.ICStoreNear
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.map_scan_history.MapScanHistoryActivity

class StoreSellHistoryActivity : BaseActivityMVVM(), StoreSellHistoryView {

    private val viewModel: StoreSellHistoryViewModel by viewModels()
    private val adapter = StoreSellHistoryAdapter(this)

    private val gpsPermission = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_sell_history)
        initSwipeLayout()
        initRecyclerview()
        initViewModel()
        listener()
        getData()
    }

    private fun initSwipeLayout() {
        val swipeColor = if (vn.icheck.android.ichecklibs.Constant.primaryColor.isNotEmpty()) {
            Color.parseColor(vn.icheck.android.ichecklibs.Constant.primaryColor)
        } else {
            ContextCompat.getColor(this, vn.icheck.android.ichecklibs.R.color.colorPrimary)
        }
        swipe_layout.setColorSchemeColors(swipeColor, swipeColor, swipeColor))

        swipe_layout.setOnRefreshListener {
            getData()
        }
    }

    private fun initRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initViewModel() {
        viewModel.dataProduct.observe(this, {
            txtTitle.text = it.product?.name
        })

        adapter.disableLoadMore()

        viewModel.isLoadMoreData.observe(this, {
            adapter.removeDataWithoutUpdate()
        })

        viewModel.listData.observe(this, {
            swipe_layout.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.statusCode.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
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

        viewModel.onError.observe(this, {
            swipe_layout.isRefreshing = false
            when (it) {
                Constant.ERROR_UNKNOW -> {
                    DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
                }
            }
        })
    }

    private fun getData(handle: Boolean = false) {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            swipe_layout.isRefreshing = true
            if (handle) {
                Handler().postDelayed({
                    viewModel.getDataIntent(intent)
                }, 3000)
            } else {
                viewModel.getDataIntent(intent)
            }
        } else {
            DialogHelper.showConfirm(this, R.string.de_hien_thi_du_lieu_vui_long_bat_gps, R.string.huy_bo, R.string.bat_gps, object : ConfirmDialogListener {
                override fun onDisagree() {
                    onBackPressed()
                }

                override fun onAgree() {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, gpsPermission)
                }
            })
        }
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onClickItem(listData: MutableList<ICStoreNear>, id: Long?) {
        val data = JsonHelper.toJson(listData)
        val intent = Intent(this, MapScanHistoryActivity::class.java)
        intent.putExtra(Constant.DATA_1, data)
        intent.putExtra(Constant.DATA_2, id)
        startActivity(intent)
    }

    override fun onLoadMore() {
        viewModel.getStoreSell(viewModel.idProduct!!, true)
    }

    override fun onRefresh() {
        getData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == gpsPermission) {
            getData(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == gpsPermission) {
            if (PermissionHelper.checkResult(grantResults)) {
                getData()
            }
        }
    }
}