package vn.icheck.android.screen.user.listdistributor

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_distributor2.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.ToastUtils

class ListDistributorActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    val adapter = ListDistributorAdapter(this)
    lateinit var viewModel: ListDistributorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_distributor2)
        viewModel = ViewModelProvider(this).get(ListDistributorViewModel::class.java)

        DialogHelper.showLoading(this)
        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
        txtTitle.setText(R.string.nha_phan_phoi)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
        viewModel.getData(intent)
        viewModel.onError.observe(this, {
            DialogHelper.closeLoading(this)
            swipeLayout.isRefreshing = false

            if (adapter.listData.isNullOrEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                ToastUtils.showLongError(this, it.message)
            }
        })

        viewModel.onSuccess.observe(this, {
            DialogHelper.closeLoading(this)
            swipeLayout.isRefreshing = false
            it.count?.let { safe ->
                tvCount.setText(R.string.d_nha_phan_phoi, safe)
            }

            if (!it.isLoadMore) {
                adapter.setData(it.listData)
            } else {
                adapter.addData(it.listData)
            }
        })
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getListDistributor()
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListDistributor(true)
    }
}