package vn.icheck.android.screen.user.history_accumulate_points.fragment.used

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_history_point.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.screen.user.history_accumulate_points.fragment.HistoryPointAdapter
import vn.icheck.android.screen.user.history_accumulate_points.fragment.HistoryPointViewModel
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class HistoryPointUsedFragment : BaseFragmentMVVM(), IRecyclerViewCallback {
    private val viewModel by viewModels<HistoryPointViewModel>()
    val adapter = HistoryPointAdapter(this, "used")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_point, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        viewModel.getListPointUsed(false)
    }

    private fun initListener() {
        viewModel.onSetData.observe(viewLifecycleOwner, Observer {
            recyclerView.beVisible()
            layoutNoData.beGone()
            swipeLayout.isRefreshing = false
            adapter.setData(it)
        })

        viewModel.onAddData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.addData(it)
        })

        viewModel.onError.observe(viewLifecycleOwner, Observer {
            recyclerView.beVisible()
            layoutNoData.beGone()
            swipeLayout.isRefreshing = false
            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                showLongError(it.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })

        viewModel.onErrorEmpty.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            recyclerView.beGone()
            layoutNoData.beVisible()
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListPointUsed(true)
    }
}