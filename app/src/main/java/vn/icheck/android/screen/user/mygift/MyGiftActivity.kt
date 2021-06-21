package vn.icheck.android.screen.user.mygift

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_my_gift.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.screen.user.mygift.fragment.reward_item_v2.MyGiftsViewModel
import vn.icheck.android.screen.user.mygift.fragment.reward_item_v2.RewardItemV2Adapter
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.ToastUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class MyGiftActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    lateinit var viewModel: MyGiftsViewModel
    val adapter = RewardItemV2Adapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_gift)

        TrackingAllHelper.tagMyGiftBoxClick()
        viewModel = ViewModelProvider(this).get(MyGiftsViewModel::class.java)

        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initRecyclerView() {
        rcv_gifts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcv_gifts.adapter = adapter
        imgBack.setOnClickListener {
            finish()
        }
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
        viewModel.getListRewardItem(false)
    }

    private fun initListener() {
        viewModel.onError.observe(this,  {
            swipeLayout.isRefreshing = false
            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                ToastUtils.showLongError(this, it.message)
            }
        })
        viewModel.liveData.observe(this, {
            swipeLayout.isRefreshing = false
            if (viewModel.totalItems != 0) {
                txtTitle.setText(R.string.qua_cua_toi_d, viewModel.totalItems)
                it.listRewardItem.firstOrNull()?.totalGifts = viewModel.totalItems
            }
            if (it.count == 0) {
                rcv_gifts.beGone()
                ic_no_gift.beVisible()
                tv_no_gift.beVisible()
            }
            if (!it.isLoadMore) {
                adapter.setData(it.listRewardItem)
            } else {
                adapter.addData(it.listRewardItem)
            }
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListRewardItem(true)
    }
}