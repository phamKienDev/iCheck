package vn.icheck.android.screen.user.winner_campaign

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.winner_campaign_activity.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.my_gift_warehouse.list_mission.list.ListMissionActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.StatusBarUtils

class WinnerCampaignActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    lateinit var viewModel: WinnerCampaignViewModel
    val adapter = WinnerCampaignAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.winner_campaign_activity)

        viewModel = ViewModelProvider(this).get(WinnerCampaignViewModel::class.java)

        DialogHelper.showLoading(this)
        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        StatusBarUtils.setOverStatusBarDark(this@WinnerCampaignActivity)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnMission.setOnClickListener {
            viewModel.campaignId?.let { id -> ListMissionActivity.show(this, id) }
        }
    }

    private fun initRecyclerView() {
        var backgroundHeight = 0

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (backgroundHeight <= 0) {
                    backgroundHeight = toolbarAlpha.height + (toolbarAlpha.height / 2)
                }

                val visibility = viewModel.getHeaderAlpha(recyclerView.computeVerticalScrollOffset(), backgroundHeight)
                toolbarAlpha.alpha = visibility
                viewShadow.alpha = visibility
            }
        })
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.resetData()
            getData()
        }

        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)

        swipeLayout.post {
            getData()
        }
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getData(intent)
    }

    private fun initListener() {
        viewModel.onError.observe(this) {
            DialogHelper.closeLoading(this)
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                showLongError(it.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        }
        viewModel.topWinnerData.observe(this) {
            DialogHelper.closeLoading(this)
            swipeLayout.isRefreshing = false

            if (it.listData.isNullOrEmpty()) {
                layoutEmpity.beVisible()
                recyclerView.beGone()
            } else {
                layoutEmpity.beGone()
                recyclerView.beVisible()

                adapter.setHeaderTheWinner(it)
            }

            viewModel.getListWinner(false)
        }

        viewModel.setListWinnerData.observe(this) { model ->
            DialogHelper.closeLoading(this)
            swipeLayout.isRefreshing = false
            if (!model.isLoadMore){
                adapter.setData(model.listData ?: mutableListOf())
            }else{
                adapter.addData(model.listData ?: mutableListOf())
            }
        }
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListWinner(true)
    }
}