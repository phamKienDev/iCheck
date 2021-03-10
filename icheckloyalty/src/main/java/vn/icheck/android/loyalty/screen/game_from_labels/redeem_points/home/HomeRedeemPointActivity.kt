package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_home_redeem_point.*
import kotlinx.android.synthetic.main.item_header_home_redeem_point.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.helper.StatusBarHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKBoxGifts
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.point_history.PointHistoryLoyaltyActivity
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.the_winner_point.TheWinnerPointActivity
import vn.icheck.android.loyalty.screen.redemption_history.RedemptionHistoryActivity
import vn.icheck.android.loyalty.screen.web.WebViewActivity

class HomeRedeemPointActivity : BaseActivityGame(), IRecyclerViewCallback, View.OnClickListener {

    companion object {
        fun startActivity(activity: Activity, campaignID: Long, banner: String?, description: String?) {
            val intent = Intent(activity, HomeRedeemPointActivity::class.java)
            intent.putExtra(ConstantsLoyalty.DATA_1, campaignID)
            intent.putExtra(ConstantsLoyalty.DATA_2, banner)
            intent.putExtra(ConstantsLoyalty.DATA_3, description)
            activity.startActivity(intent)
        }

        var banner: String? = null

        var description: String? = null

        var campaignID = -1L
    }

    private val viewModel by viewModels<HomeRedeemPointViewModel>()

    private val adapter = HomeRedeemPointAdapter(this)

    private val layoutManager = GridLayoutManager(this, 2)

    override val getLayoutID: Int
        get() = R.layout.activity_home_redeem_point

    override fun onInitView() {
        StatusBarHelper.setOverStatusBarLight(this@HomeRedeemPointActivity)

        campaignID = intent.getLongExtra(ConstantsLoyalty.DATA_1, -1)
        banner = intent.getStringExtra(ConstantsLoyalty.DATA_2)
        description = intent.getStringExtra(ConstantsLoyalty.DATA_3)

        if (campaignID != -1L) {
            viewModel.collectionID = campaignID
        } else {
            object : DialogNotification(this, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, "Ok", false) {
                override fun onDone() {
                    onBackPressed()
                }
            }.show()
        }

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        WidgetHelper.setClickListener(this@HomeRedeemPointActivity, imgInfor, imgReward, imgRank, imgHistory, imgPointMore, layoutNoLogin, btnInfor, btnRanking, btnReward, btnHistory)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position < adapter.listData.size && (adapter.listData[position] is ICKBoxGifts)) {
                    if (adapter.setLoadMore(position)) {
                        2
                    } else {
                        1
                    }
                } else {
                    2
                }
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val scrollPosition = recyclerView.findViewHolderForAdapterPosition(0)?.itemView

                if (scrollPosition != null) {
                    val visibility = (1f / (-scrollPosition.height + toolbar.height)) * scrollPosition.y
                    toolbarAlpha.alpha = visibility
                }
            }
        })
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getOverView()
    }

    private fun initListener() {
        if (SessionManager.isLogged) {
            recyclerView.setVisible()
            imgPointMore.setVisible()
            layoutDefault.setGone()
        } else {
            layoutNoLogin.setVisible()
            recyclerView.setGone()
            layoutDefault.setVisible()
            imgPointMore.setGone()
        }

        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.onErrorEmpty.observe(this, Observer {
            swipeLayout.isRefreshing = false

            layoutNoLogin.setVisible()
            recyclerView.setGone()
            layoutDefault.setVisible()
            imgPointMore.setGone()
        })

        viewModel.onOverView.observe(this, Observer {
            adapter.addOverView(it)
            viewModel.getProductList()
        })

        viewModel.onSetData.observe(this, {
            swipeLayout.isRefreshing = false
            adapter.setData(it)
        })

        viewModel.onAddData.observe(this, {
            adapter.addData(it)
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getProductList(true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgPointMore -> {
                DialogHelperGame.scanOrEnterAccumulatePoint(this@HomeRedeemPointActivity, campaignID)
            }
            R.id.layoutNoLogin -> {
                showLongWarning("showDialogLogin")
            }

            R.id.imgInfor -> {
                startActivity(Intent(this@HomeRedeemPointActivity, WebViewActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.DATA_1, description)
                    putExtra(ConstantsLoyalty.DATA_3, "Thông tin chương trình")
                })
            }
            R.id.imgRank -> startActivity<TheWinnerPointActivity, Long>(ConstantsLoyalty.DATA_1, campaignID)
            R.id.imgReward -> startActivity<RedemptionHistoryActivity, Long>(ConstantsLoyalty.DATA_1, campaignID)
            R.id.imgHistory -> startActivity<PointHistoryLoyaltyActivity, Long>(ConstantsLoyalty.DATA_2, campaignID)
            R.id.btnInfor -> {
                startActivity(Intent(this@HomeRedeemPointActivity, WebViewActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.DATA_1, description)
                    putExtra(ConstantsLoyalty.DATA_3, "Thông tin chương trình")
                })
            }
            R.id.btnRanking -> startActivity<TheWinnerPointActivity, Long>(ConstantsLoyalty.DATA_1, campaignID)
            R.id.btnReward -> startActivity<RedemptionHistoryActivity, Long>(ConstantsLoyalty.DATA_1, campaignID)
            R.id.btnHistory -> startActivity<PointHistoryLoyaltyActivity, Long>(ConstantsLoyalty.DATA_2, campaignID)
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.ON_UPDATE_POINT -> viewModel.getOverView()
            ICMessageEvent.Type.ON_COUNT_GIFT -> viewModel.getProductList()
            else -> super.onMessageEvent(event)
        }
    }
}