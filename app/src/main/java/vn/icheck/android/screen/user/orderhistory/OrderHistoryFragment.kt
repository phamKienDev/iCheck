package vn.icheck.android.screen.user.orderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_order_history.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICOrderHistoryV2
import vn.icheck.android.screen.user.orderhistory.adapter.OrderHistoryAdapter

/**
 * Created by VuLCL on 1/4/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class OrderHistoryFragment : BaseFragmentMVVM(), IRecyclerViewCallback {
    lateinit var adapter: OrderHistoryAdapter
    private val viewModel: OrderHistoryViewModel by viewModels()

    private var status = 1

    companion object {
        fun newInstance(status: Int): OrderHistoryFragment {
            val fragment = OrderHistoryFragment()

            val bundle = Bundle()
            bundle.putInt(Constant.DATA_1, status)
            fragment.arguments = bundle

            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_history, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        status = arguments?.getInt(Constant.DATA_1, 1) ?: 1
        setupRefreshLayout()
        setupRecyclerView()
        listenerData()
    }

    private fun setupRefreshLayout() {
        context?.let {
            swipeLayout.setColorSchemeColors(ContextCompat.getColor(it, R.color.colorSecondary), ContextCompat.getColor(it, R.color.colorPrimary), ContextCompat.getColor(it, R.color.colorPrimary))
        }

        swipeLayout.setOnRefreshListener {
            getListOrders()
        }

        swipeLayout.post {
            getListOrders()
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderHistoryAdapter(status, this)
        recyclerView.adapter = adapter
    }

    private fun listenerData() {
        viewModel.getData(status)

        viewModel.onSetData.observe(viewLifecycleOwner, {
            swipeLayout?.isRefreshing = false
            if (!it.isNullOrEmpty()) {
                adapter.setListData(it)
            } else {
                adapter.setError(R.drawable.ic_group_120dp, getString(R.string.ban_chua_co_don_hang_nao), -1)
            }
        })

        viewModel.onAddData.observe(viewLifecycleOwner, {
            swipeLayout?.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.onError.observe(viewLifecycleOwner, {
            swipeLayout?.isRefreshing = false
            if (adapter.isEmpty) {
                it.message?.let { it1 -> adapter.setError(it.icon, it1, R.string.thu_lai) }
            } else {
                it.message?.let { it1 -> showShortError(it1) }
            }
        })

    }

    private fun getListOrders() {
        swipeLayout?.isRefreshing = true
        viewModel.getData(status)
    }

    fun addOrderDelivered(obj: ICOrderHistoryV2) {
        adapter.addOrderDelivered(obj)
    }

    override fun onMessageClicked() {
        getListOrders()
    }

    override fun onLoadMore() {
        viewModel.getData(status, true)
    }
}