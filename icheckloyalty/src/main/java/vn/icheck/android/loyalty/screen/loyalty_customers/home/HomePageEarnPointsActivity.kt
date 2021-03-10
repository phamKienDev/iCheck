package vn.icheck.android.loyalty.screen.loyalty_customers.home

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_home_page_earn_points.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.helper.StatusBarHelper
import vn.icheck.android.loyalty.model.ICKLongTermProgram

class HomePageEarnPointsActivity : BaseActivityGame() {

    private val viewModel by viewModels<HomePageEarnPointsViewModel>()

    private var adapter: HomePageEarnPointsAdapter? = null

    private var obj: ICKLongTermProgram? = null

    override val getLayoutID: Int
        get() = R.layout.activity_home_page_earn_points

    override fun onInitView() {
        StatusBarHelper.setOverStatusBarDark(this)

        initToolbar()
        initSwipeLayout()
        initRecyclerView()
        initListener()
    }

    private fun initToolbar() {
        obj = intent.getSerializableExtra(ConstantsLoyalty.DATA_1) as ICKLongTermProgram?
        viewModel.collectionID = intent.getLongExtra(ConstantsLoyalty.DATA_2, -1)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    private fun initRecyclerView() {
        adapter = HomePageEarnPointsAdapter(obj?.banner?.original, viewModel.collectionID)

        recyclerView.layoutManager = LinearLayoutManager(this@HomePageEarnPointsActivity)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val scrollPosition = recyclerView.findViewHolderForAdapterPosition(0)?.itemView

                if (scrollPosition != null) {
                    val visibility = (1f / (-scrollPosition.height + toolbar.height)) * scrollPosition.y
                    toolbarAlpha.alpha = visibility
                }
            }
        })
    }

    private fun initListener() {
        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false

            if (adapter?.isEmpty == true) {
                adapter?.setError(it)
            } else {
                if (adapter?.listData?.size == 1) {
                    adapter?.setErrorNotEmpty(it)
                } else {
                    showLongError(it.title)
                }
            }
        })

        viewModel.setHeader.observe(this, Observer {
            obj = it
            adapter?.addHeader(it)
            viewModel.getTransactionHistory()
        })

        viewModel.setItem.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter?.addItem(it)
        })

        viewModel.onErrorEmpty.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter?.setErrorNotEmpty(it)
        })
    }

    private fun getData() {
        swipeLayout.isRefreshing = true

        if (obj != null) {
            viewModel.setHeader.postValue(obj)
        } else {
            viewModel.getHeaderHomePage()
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.ON_UPDATE_POINT -> viewModel.getHeaderHomePage()
        }
    }
}