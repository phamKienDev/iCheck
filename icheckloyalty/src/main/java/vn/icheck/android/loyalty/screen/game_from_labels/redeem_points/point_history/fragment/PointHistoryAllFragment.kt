package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.point_history.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_point_history_all.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.fragment.BaseFragmentGame
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.HomeRedeemPointActivity

class PointHistoryAllFragment(val status: Int, val id: Long, val target: String) : BaseFragmentGame(), IRecyclerViewCallback {

    private val viewModel by viewModels<PointHistoryAllViewModel>()

    private val adapter = PointHistoryAllAdapter(this)

    private var type = 0

    override val getLayoutID: Int
        get() = R.layout.fragment_point_history_all

    override fun onInitView() {
        viewModel.collectionID = id

        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
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
        when (status) {
            1 -> viewModel.getPointHistoryAll(false, target, null)
            2 -> viewModel.getPointHistoryAll(false, target, "accumulate-points")
            3 -> viewModel.getPointHistoryAll(false, target, "exchange-gift")
        }
    }

    private fun initListener() {
        viewModel.onError.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                type = 1
                adapter.setError(it)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.onSetData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.onErrorEmpty.observe(viewLifecycleOwner, Observer {
            type = 2
            swipeLayout.isRefreshing = false
            adapter.setError(it)
        })
    }

    override fun onMessageClicked() {
        when (type) {
            1 -> getData()
            2 -> {
                if (status != 3){
                    DialogHelperGame.scanOrEnterAccumulatePoint(requireContext(), id)
                }else{
                    activity?.onBackPressed()
                }
            }
        }
    }

    override fun onLoadMore() {
        when (status) {
            1 -> viewModel.getPointHistoryAll(true, target, null)
            2 -> viewModel.getPointHistoryAll(true, target, "accumulate-points")
            3 -> viewModel.getPointHistoryAll(true, target, "exchange-gift")
        }
    }
}