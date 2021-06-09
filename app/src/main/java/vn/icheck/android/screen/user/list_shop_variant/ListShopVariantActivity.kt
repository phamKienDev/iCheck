package vn.icheck.android.screen.user.list_shop_variant

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_shop_variant.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICShopVariantV2
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.list_shop_variant.adapter.ListShopVariantAdapter
import vn.icheck.android.screen.user.list_shop_variant.view.IListShopVariantView
import vn.icheck.android.screen.user.list_shop_variant.viewmodel.ListShopVariantViewModel
import vn.icheck.android.screen.user.map_scan_history.MapScanHistoryActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class ListShopVariantActivity : BaseActivityMVVM(), IListShopVariantView {

    private lateinit var viewModel: ListShopVariantViewModel

    private val adapter = ListShopVariantAdapter(this)

    private var mId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_shop_variant)

        setupToolbar()
        setupRecyclerview()
        setupViewModel()
    }

    private fun setupToolbar() {
        txtTitle.text = "Điểm bán gần đây"
        imgAction.visibility = View.VISIBLE
        imgAction.setImageResource(R.drawable.ic_home_blue_v2_24px)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgAction.setOnClickListener {
            onBackPressed()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME))
        }
    }

    private fun setupRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ListShopVariantViewModel::class.java)

        viewModel.listData.observe(this, Observer {
            adapter.setListData(it)
        })

        viewModel.successData.observe(this, Observer {
            showShortSuccess(it)
        })

        viewModel.errorData.observe(this, Observer {
            showShortError(it)
        })

        viewModel.statusCode.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {

                        }

                        override fun onAgree() {
                            viewModel.addCart(mId)
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

        viewModel.getDataIntent(intent)
    }

    override fun onClickAddToCart(id: Long) {
        mId = id
        if (SessionManager.isUserLogged) {
            viewModel.addCart(id)
        } else {
            ActivityUtils.startActivity<IckLoginActivity>(this@ListShopVariantActivity)
        }
    }

    override fun onClickShowMap(item: ICShopVariantV2) {
        if (item.location != null) {
            if (item.location?.lat != null && item.location?.lon != null) {
                val intent = Intent(this@ListShopVariantActivity, MapScanHistoryActivity::class.java)
                intent.putExtra(Constant.DATA_2, item.id)
                item.location?.lat?.let {
                    intent.putExtra(Constant.DATA_3, it)
                }
                item.location?.lon?.let {
                    intent.putExtra(Constant.DATA_4, it)
                }
                intent.putExtra("avatarShop", item.avatar)
                startActivity(intent)
            } else {
                showShortError("Vị trí của shop đang được cập nhật")
            }
        } else {
            showShortError("Vị trí của shop đang được cập nhật")
        }
    }
}
