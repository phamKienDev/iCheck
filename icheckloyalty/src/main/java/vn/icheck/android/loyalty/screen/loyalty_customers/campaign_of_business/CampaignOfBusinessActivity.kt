package vn.icheck.android.loyalty.screen.loyalty_customers.campaign_of_business

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_campaign_of_business.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback

class CampaignOfBusinessActivity : BaseActivityGame(), IRecyclerViewCallback {

    private val viewModel by viewModels<CampaignOfBusinessViewModel>()

    private val adapter = CampaignOfBusinessAdapter(this)

    override val getLayoutID: Int
        get() = R.layout.activity_campaign_of_business

    override fun onInitView() {
        viewModel.collectionID = intent.getLongExtra(ConstantsLoyalty.DATA_1, -1)

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
        txtTitle.text = "Chương trình"
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this@CampaignOfBusinessActivity)
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

    private fun initListener() {
        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                showLongError(it.message)
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
            swipeLayout.isRefreshing = false
            adapter.addListData(it)
        })
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getCampaignOfBusiness()
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getCampaignOfBusiness(true)
    }
}