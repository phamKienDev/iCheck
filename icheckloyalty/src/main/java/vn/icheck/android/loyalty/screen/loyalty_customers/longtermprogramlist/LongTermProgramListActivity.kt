package vn.icheck.android.loyalty.screen.loyalty_customers.longtermprogramlist

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_long_term_program_list.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback

class LongTermProgramListActivity : BaseActivityGame(), IRecyclerViewCallback {
    private val viewModel by viewModels<LongTermProgramListViewModel>()

    private val adapter = LongTermProgramListAdapter(this)

    override val getLayoutID: Int
        get() = R.layout.activity_long_term_program_list

    override fun onInitView() {
        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle rText R.string.khach_hang_than_thiet
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this@LongTermProgramListActivity, LinearLayoutManager.VERTICAL, false)
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

    fun initListener() {
        viewModel.onSetData.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(this, Observer {
            adapter.addListData(it)
        })

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
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getLongTermProgramList()
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        viewModel.getLongTermProgramList(true)
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when(event.type){
            ICMessageEvent.Type.ON_UPDATE_POINT -> viewModel.getLongTermProgramList()
        }
    }
}