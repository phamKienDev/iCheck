package vn.icheck.android.screen.user.listnotification.productnotice

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_product_notice.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.SizeHelper

/**
 * Created by VuLCL on 6/9/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ListProductNoticeActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    private lateinit var viewModel: ListProductNoticeViewModel

    private val adapter = ListProductNoticeAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product_notice)

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupSwipeLayout()
    }

    private fun setupToolbar() {
        layoutToolbar.setPadding(0, getStatusBarHeight + SizeHelper.size16, 0, 0)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.setText(R.string.thong_bao_ve_san_pham)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ListProductNoticeViewModel::class.java)

        viewModel.onSetData.observe(this, Observer {
            closeLoading()
            swipeLayout.isRefreshing = false
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.onError.observe(this, Observer {
            closeLoading()
            swipeLayout.isRefreshing = false
            it.message?.let { it1 -> adapter.setError(it.icon, it1, -1) }
        })
    }

    private fun closeLoading() {
        swipeLayout.isRefreshing = false

        if (layoutLoading != null) {
            layoutContainer.removeView(layoutLoading)
        }
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSwipeLayout() {
        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = true
            viewModel.getFriendRequest()
        }

        swipeLayout.post {
            swipeLayout.isRefreshing = true
            viewModel.getFriendRequest()
        }
    }

    override fun onMessageClicked() {
        viewModel.getFriendRequest()
    }

    override fun onLoadMore() {
        viewModel.getFriendRequest(true)
    }
}