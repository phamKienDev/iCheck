package vn.icheck.android.loyalty.screen.redemption_history

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_redemption_history.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop.GiftShopActivity

class RedemptionHistoryActivity : BaseActivityGame(), IRecyclerViewCallback {

    private val viewModel by viewModels<RedemptionHistoryViewModel>()

    private val adapter = RedemptionHistoryAdapter(this)

    /**
     * @param type = 0 Tích điểm đổi quà
     * @param type = 1 Tích điểm dài hạn
     */
    private var type = 0

    override val getLayoutID: Int
        get() = R.layout.activity_redemption_history

    override fun onInitView() {
        viewModel.collectionID = intent.getLongExtra(ConstantsLoyalty.DATA_1, -1)
        type = intent.getIntExtra(ConstantsLoyalty.DATA_2, 0)

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle rText R.string.lich_su_doi_qua
    }

    fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    fun initSwipeLayout() {
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
        if (type == 0) {
            viewModel.getRedemptionHistory()
        } else {
            viewModel.getRedemptionHistoryLongTime()
        }
    }

    fun initListener() {
        viewModel.onError.observe(this, {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.onSetData.observe(this, {
            swipeLayout.isRefreshing = false
            adapter.setDataICKRewardGameLoyalty(it)
        })

        viewModel.onAddData.observe(this, {
            adapter.addDataICKRewardGameLoyalty(it)
        })

        viewModel.setData.observe(this, {
            swipeLayout.isRefreshing = false
            adapter.setDataICKRedemptionHistory(it)
        })

        viewModel.addData.observe(this, {
            adapter.addDataICKRedemptionHistory(it)
        })

        viewModel.onErrorEmpty.observe(this, {
            swipeLayout.isRefreshing = false
            adapter.setError(it)
        })
    }

    override fun onMessageClicked() {
        if (type == 0) {
            DialogHelperGame.scanOrEnterAccumulatePoint(this@RedemptionHistoryActivity, intent.getLongExtra(ConstantsLoyalty.DATA_1, -1))
        } else {
            startActivity<GiftShopActivity, Long>(ConstantsLoyalty.ID, viewModel.collectionID)
        }
    }

    override fun onLoadMore() {
        if (type == 0) {
            viewModel.getRedemptionHistory(true)
        } else {
            viewModel.getRedemptionHistoryLongTime(true)
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        when(event.type){
            ICMessageEvent.Type.BACK_UPDATE -> {
                getData()
            }
            else -> super.onMessageEvent(event)
        }
    }
}