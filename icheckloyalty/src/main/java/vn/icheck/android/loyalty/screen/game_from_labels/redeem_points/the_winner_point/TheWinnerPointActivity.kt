package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.the_winner_point

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_the_winner_point.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.helper.StatusBarHelper

class TheWinnerPointActivity : BaseActivityGame(), IRecyclerViewCallback {

    private val viewModel by viewModels<TheWinnerPointViewModel>()

    private val adapter = TheWinnerPointAdapter(this)

    override val getLayoutID: Int
        get() = R.layout.activity_the_winner_point

    override fun onInitView() {
        StatusBarHelper.setOverStatusBarDark(this@TheWinnerPointActivity)

        viewModel.collectionID = intent.getLongExtra(ConstantsLoyalty.DATA_1, -1)

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar(){
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.text = "Top người trúng thưởng"

        btnNhapMa.setOnClickListener {
            DialogHelperGame.scanOrEnterAccumulatePoint(this@TheWinnerPointActivity, viewModel.collectionID)
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
            adapter.disableLoadMore()
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getListHeaderWinner()
    }

    private fun initListener() {
        viewModel.onError.observe(this, {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.onTopWinner.observe(this, {
            if (!it.listData.isNullOrEmpty()) {
                linearLayout.setGone()
                recyclerView.setVisible()

                adapter.addTopWinner(it)
            } else {
                linearLayout.setVisible()
                recyclerView.setGone()
            }
        })

        viewModel.onSetData.observe(this, {
            swipeLayout.isRefreshing = false
            adapter.setTheWinner(it)
        })

        viewModel.onAddData.observe(this, {
            adapter.addTheWinner(it)
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListWinner(true)
    }
}