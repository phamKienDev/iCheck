package vn.icheck.android.screen.user.brand

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_brand_page.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import vn.icheck.android.R
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.screen.user.brand.adapter.BrandPageAdapter
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.ToastUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class BrandPageActivity : AppCompatActivity(), IRecyclerViewCallback {
    val adapter = BrandPageAdapter(this)
    lateinit var viewModel: BrandPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand_page)
        viewModel = ViewModelProvider(this).get(BrandPageViewModel::class.java)

        setUpToolbar()
        setUpRecyclerView()
        setUpSwipeLayout()
        initListener()
    }

    private fun setUpToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle rText R.string.cac_nhan_hang
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun setUpSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            getData()
        }
        swipeLayout.post {
            viewModel.getCollectionID(intent)
        }
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getWidgetBrand(false)
    }

    private fun initListener() {
        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                ToastUtils.showLongError(this, it.message)
            }
        })

        viewModel.liveData.observe(this, Observer {
            swipeLayout.isRefreshing = false

            if (!it.isLoadMore) {
                adapter.setData(it.listData)
            } else {
                adapter.addData(it.listData)
            }
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getWidgetBrand(true)
    }
}