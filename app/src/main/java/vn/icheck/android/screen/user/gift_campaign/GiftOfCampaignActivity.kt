package vn.icheck.android.screen.user.gift_campaign

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_gift_of_campaign.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.LOGO
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.screen.user.gift_campaign.adapter.GiftOfCampaignAdapter
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.StatusBarUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class GiftOfCampaignActivity : BaseActivityGame(), IRecyclerViewCallback {

    private val viewModel by viewModels<GiftOfCampaignViewModel>()

    private var adapter: GiftOfCampaignAdapter? = null

    override val getLayoutID: Int
        get() = R.layout.activity_gift_of_campaign

    override fun onInitView() {
        StatusBarUtils.setOverStatusBarDark(this@GiftOfCampaignActivity)

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()

        layoutNoData.background=ViewHelper.bgWhiteCornersTop20(this)
        recyclerView.background=ViewHelper.bgWhiteCornersTop20(this)
    }

    private fun initToolbar(){
        txtTitle.setText(R.string.qua_tang_dang_cho_ban)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        var backgroundHeight = 0

        adapter = GiftOfCampaignAdapter(this, intent.getStringExtra(LOGO))
        recyclerView.layoutManager = LinearLayoutManager(this@GiftOfCampaignActivity, LinearLayoutManager.VERTICAL, false)
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
        WidgetUtils.loadImageUrl(imgHeader, viewModel.banner)
    }

    private fun initListener() {
        viewModel.onNotEmpty.observe(this, Observer {
            recyclerView.beVisible()
            imgHeader.beGone()
            layoutNoData.beGone()
            swipeLayout.isRefreshing = false
        })

        viewModel.onSuccess.observe(this, Observer {
            adapter?.addGiftProduct(it)
        })

        viewModel.showError.observe(this, Observer {
            swipeLayout.isRefreshing = false
            DialogHelper.showNotification(this@GiftOfCampaignActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        })

        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false
            if (adapter?.isEmpty == true) {
                adapter?.setError(it)
            } else {
                showLongError(it.message ?: "")
            }
        })

        viewModel.onErrorEmpty.observe(this, Observer {
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

    }
}