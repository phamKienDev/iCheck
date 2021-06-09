package vn.icheck.android.screen.user.mygift.fragment.reward_item_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_my_gifts.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ToastUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class MyGiftsFragment : BaseFragmentMVVM(), IRecyclerViewCallback {
    lateinit var viewModel: MyGiftsViewModel
    val adapter = RewardItemV2Adapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_gifts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyGiftsViewModel::class.java)

        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initRecyclerView() {
        rcv_gifts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rcv_gifts.adapter = adapter
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
        viewModel.onError.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                ToastUtils.showLongError(context, it.message)
            }
        })
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false

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