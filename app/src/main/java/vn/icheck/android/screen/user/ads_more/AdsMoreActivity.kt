package vn.icheck.android.screen.user.ads_more

import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ads_more.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper.setScrollSpeed
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.loyalty.base.activity.BaseActivityGame

class AdsMoreActivity : BaseActivityGame() {

    private val viewModel by viewModels<AdsMoreViewModel>()
    private val adapter = AdsMoreAdapter()

    override val getLayoutID: Int
        get() = R.layout.activity_ads_more

    override fun onInitView() {
        getData()
        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.clearData()
            getData()
        }
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.setScrollSpeed()
        recyclerView.layoutManager = if (viewModel.adsModel?.type == Constant.HORIZONTAL) {
            LinearLayoutManager(this@AdsMoreActivity, LinearLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(this@AdsMoreActivity, 2, GridLayoutManager.VERTICAL, false)
        }
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView)
                }
            }
        })
    }

    private fun initListener() {
        viewModel.setError.observe(this@AdsMoreActivity, {
            swipeLayout.isRefreshing = false
            showLongError(it)
        })

        viewModel.setData.observe(this@AdsMoreActivity, {
            swipeLayout.isRefreshing = false
            txtTitle.text = it.name
            adapter.setData(it.data, it.objectType, it.targetType, it.targetId)
        })
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getDataIntent(intent)
    }
}