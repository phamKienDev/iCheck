package vn.icheck.android.screen.user.coinhistory

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_coin_history.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.Status
import vn.icheck.android.screen.dialog.CointSettingDialog
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class CoinHistoryActivity : BaseActivityMVVM(), ICoinHistoryView {
    private val adapter = CoinHistoryAdapter(this)
    lateinit var viewModel: CoinHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_history)

        viewModel = ViewModelProvider(this).get(CoinHistoryViewModel::class.java)
        TrackingAllHelper.trackIcoinWalletViewed()
        initToolbar()
        setupRecycleView()
        initSwipelayout()
        setupViewModel()
        getCoin()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener { onBackPressed() }

        imgAction.setOnClickListener {
            object : CointSettingDialog(this@CoinHistoryActivity, viewModel.getTypeXu, viewModel.getBeginAt, viewModel.getEndAt) {
                override fun onDone(type: Int, begin: String, end: String) {
                    viewModel.setTypeXu(type)
                    viewModel.setBeginAt(begin)
                    viewModel.setEndAt(end)

                    if (viewModel.getTypeXu != 0 || viewModel.getBeginAt.isNotEmpty() || viewModel.getEndAt.isNotEmpty()) {
                        imgDot.beVisible()
                    } else {
                        imgDot.beGone()
                    }

                    getData()
                }
            }.show()
        }
    }

    private fun setupRecycleView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initSwipelayout() {
        val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)

        swipeLayout.isRefreshing = true

        swipeLayout.setOnRefreshListener { getData() }
    }

    private fun setupViewModel() {
//        viewModel.onCoin.observe(this, {
//            if (recyclerView.findViewHolderForAdapterPosition(0) is CoinHistoryAdapter.HeaderCoinHolder) {
//                adapter.notifyItemChanged(0)
//            }
//        })

        viewModel.onSetData.observe(this, {
            swipeLayout.isRefreshing = false
            if (!it.isNullOrEmpty()) {
                if (viewModel.getTypeXu != 0 || viewModel.getBeginAt.isNotEmpty() || viewModel.getEndAt.isNotEmpty()) {
                    adapter.setData(it, true)
                } else {
                    adapter.setData(it, false)
                }
            } else {
                adapter.setEmpityData()
            }
        })
        viewModel.onAddData.observe(this, {
            swipeLayout.isRefreshing = false
            adapter.addData(it)
        })
        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false
            if (adapter.isNotEmpity) {
                showShortError(ICheckApplication.getError(it.message))
            } else {
                adapter.setError(it.icon, null, it.message)
            }
        })
    }

    private fun getCoin() {
        if (NetworkHelper.isConnected(this@CoinHistoryActivity)) {
            viewModel.getCoin().observe(this, Observer {
                if (it.status == Status.SUCCESS) {
                    SessionManager.setCoin(it.data?.data?.availableBalance ?: 0)
                    if (recyclerView.findViewHolderForAdapterPosition(0) is CoinHistoryAdapter.HeaderCoinHolder) {
                        adapter.notifyItemChanged(0)
                    }
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COIN_AND_RANK))
                }
            })
        }

        viewModel.getCointHistory()
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getCointHistory()
    }

    override fun onLoadmore() {
        viewModel.getCointHistory(true)
    }

    override fun onTryAgain() {
        getData()
    }
}
