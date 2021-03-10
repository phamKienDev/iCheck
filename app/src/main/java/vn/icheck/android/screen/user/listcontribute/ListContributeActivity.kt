package vn.icheck.android.screen.user.listcontribute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_contribute.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.util.kotlin.WidgetUtils

class ListContributeActivity : BaseActivityMVVM(), IRecyclerViewCallback, IListContributeListener {
    private var adapter = ListContributeAdapter(this, this, this)

    lateinit var viewModel: ListContributeViewModel
    private var isActivityVisible = false
    private var requestVoteContribute = 1
    private var isClickVote = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_contribute)
        viewModel = ViewModelProvider(this).get(ListContributeViewModel::class.java)

        setUpToolbar()
        setUpRecyclerView()
        setUpSwipe()
        getData()
    }

    private fun setUpToolbar() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        imgProduct.setOnClickListener {
            onBackPressed()
        }

        WidgetUtils.loadImageUrlRounded(imgProduct, intent.getStringExtra(Constant.DATA_2), R.drawable.default_product_image, SizeHelper.size4)
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun setUpSwipe() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            getDataTryAgain()
        }

        swipeLayout.post {
            viewModel.getCollectionID(intent)
        }
    }

    private fun getData() {
        viewModel.onError.observe(this, {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it.icon, it1) }
            } else {
                it.message?.let { it1 -> showLongError(it1) }
            }
        })

        viewModel.liveData.observe(this, {
            swipeLayout.isRefreshing = false

            if (!it.isLoadMore) {
                adapter.setData(it.listData)
            } else {
                adapter.addData(it.listData)
            }
        })
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.OPEN_PRODUCT_DETAIL -> {
                Intent().apply {
                    if (event.data != null && event.data is Long) {
                        putExtra(Constant.DATA_1, event.data)
                        setResult(Activity.RESULT_OK, this)
                        finish()
                    }
                }
            }
            ICMessageEvent.Type.REQUEST_VOTE_CONTRIBUTION -> {
                if (isActivityVisible) {
                    onRequireLogin(requestVoteContribute)
                }
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == requestVoteContribute) {
                isClickVote=true
                getDataTryAgain()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    private fun getDataTryAgain() {
        swipeLayout.isRefreshing = true
        viewModel.getListContribute(false)
    }

    override fun onMessageClicked() {
        getDataTryAgain()
    }

    override fun onLoadMore() {
        viewModel.getListContribute(true)
    }

    override fun onBackPressed() {
        if (isClickVote) {
            Intent().apply {
                setResult(RESULT_OK, this)
            }
        }
        super.onBackPressed()

    }

    override fun onClickVote(vote: Boolean?) {
        isClickVote = true
    }
}
