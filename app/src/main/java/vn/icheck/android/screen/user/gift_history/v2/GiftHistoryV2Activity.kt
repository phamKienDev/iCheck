package vn.icheck.android.screen.user.gift_history.v2

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_gift_history_v2.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.screen.user.my_gift_warehouse.list_mission.list.ListMissionActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.StatusBarUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class GiftHistoryV2Activity : BaseActivityGame(), IRecyclerViewCallback {

    private var adapter = GiftHistoryAdapterV2(this)

    private val viewModel by viewModels<GiftHistoryV2ViewModel>()

    override val getLayoutID: Int
        get() = R.layout.activity_gift_history_v2

    override fun onInitView() {
        StatusBarUtils.setOverStatusBarDark(this@GiftHistoryV2Activity)

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()

        btnAction.apply {
            background = ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                ListMissionActivity.show(this@GiftHistoryV2Activity, viewModel.campaignId)
            }
        }

        layoutNoData.background=ViewHelper.bgWhiteCornersTop20(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        txtTitle.setText(R.string.lich_su_mo_qua)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        var backgroundHeight = 0

        recyclerView.layoutManager = LinearLayoutManager(this@GiftHistoryV2Activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (backgroundHeight <= 0) {
                    backgroundHeight = toolbarAlpha.height + (toolbarAlpha.height / 2)
                }

                val visibility = viewModel.getHeaderAlpha(recyclerView.computeVerticalScrollOffset(), backgroundHeight)
                toolbarAlpha.alpha = visibility
                viewShadowBottom.alpha = visibility
            }
        })
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            getData()
        }

        swipeLayout.post {
            getData()
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getDataIntent(intent)
    }

    private fun initListener() {
        viewModel.headerImage.observe(this, {
            WidgetUtils.loadImageUrl(imgHeader, it, R.drawable.bg_error_campaign)
            adapter.setHeaderImage(it)
        })

        viewModel.onSetData.observe(this, {
            recyclerView.beVisible()
            imgHeader.beGone()
            layoutNoData.beGone()
            swipeLayout.isRefreshing = false
            adapter.setData(it)
        })

        viewModel.onAddData.observe(this, {
            adapter.addData(it)
        })

        viewModel.errorNotId.observe(this, {
            swipeLayout.isRefreshing = false
            DialogHelper.showNotification(this@GiftHistoryV2Activity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        })

        viewModel.onError.observe(this, {
            swipeLayout.isRefreshing = false
            if (adapter.isEmpty) {
                adapter.setError(it.icon, it.title, null)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.errorEmpty.observe(this, {
            recyclerView.beGone()
            layoutNoData.beVisible()
            imgHeader.beVisible()
            swipeLayout.isRefreshing = false
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getGiftHistory(true)
    }
}