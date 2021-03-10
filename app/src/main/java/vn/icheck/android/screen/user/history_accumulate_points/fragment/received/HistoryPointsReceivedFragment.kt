package vn.icheck.android.screen.user.history_accumulate_points.fragment.received

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_history_point.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.screen.user.history_accumulate_points.fragment.HistoryPointAdapter
import vn.icheck.android.screen.user.history_accumulate_points.fragment.HistoryPointViewModel
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class HistoryPointsReceivedFragment : BaseFragmentMVVM(), IRecyclerViewCallback {
    private val viewModel by viewModels<HistoryPointViewModel>()
    val adapter = HistoryPointAdapter(this, "received")

    override val getLayoutID: Int
        get() = R.layout.fragment_history_point

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.beVisible()
        layoutNoData.beGone()

        initSwipeLayout()
        initRecyclerView()
        initListener()
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

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getListPointReceived(false)
    }

    private fun initListener() {
        viewModel.onSetData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.setData(it)
        })

        viewModel.onAddData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.addData(it)
        })

        viewModel.onError.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                showLongError(it.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })

        viewModel.onErrorEmpty.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.setError(it)
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListPointReceived(true)
    }
}