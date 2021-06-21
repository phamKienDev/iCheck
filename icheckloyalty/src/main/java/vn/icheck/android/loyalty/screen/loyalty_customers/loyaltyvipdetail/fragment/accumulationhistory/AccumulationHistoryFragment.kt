package vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail.fragment.accumulationhistory

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_accumulation_history.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.fragment.BaseFragmentGame
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.dialog.ComingSoonOrOutOfGiftDialog
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame

class AccumulationHistoryFragment(private val id: Long, private val status: String) : BaseFragmentGame(), IRecyclerViewCallback {

    private val viewModel by viewModels<AccumulationHistoryViewModel>()

    private val adapter = AccumulationHistoryAdapter(this)

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_accumulation_history

    override fun onInitView() {
        viewModel.collectionID = id

        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getAccumulationHistory()
    }

    private fun initListener() {
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
            adapter.setError(it)
        })

        viewModel.onSetData.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(this, Observer {
            adapter.addListData(it)
        })
    }

    override fun onMessageClicked() {
        if (status != "COMING_SOON") {
            DialogHelperGame.scanOrEnterAccumulatePointLongTime(requireContext(), id)
        } else {
            ComingSoonOrOutOfGiftDialog(requireContext(), R.drawable.ic_coming_soon, getString(R.string.chuong_trinh_chua_dien_ra), getString(R.string.moi_ban_quay_lai_sau_de_tham_gia_chuong_trinh_nhe)).show()
        }
    }

    override fun onLoadMore() {
        viewModel.getAccumulationHistory(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.ON_UPDATE_POINT -> viewModel.getAccumulationHistory()
        }
    }
}