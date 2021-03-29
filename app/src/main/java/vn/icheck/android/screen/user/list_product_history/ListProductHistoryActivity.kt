package vn.icheck.android.screen.user.list_product_history

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_product_history.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.util.kotlin.WidgetUtils

class ListProductHistoryActivity : BaseActivityMVVM(), View.OnClickListener {
    lateinit var viewModel: ListProductHistoryViewModel
    lateinit var adapter: ListProductHistoryAdapter

    companion object {
        var INSTANCE: ListProductHistoryActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ListProductHistoryViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product_history)
        INSTANCE = this
        initView()
        initRecyclerView()
        listenerData()
        DialogHelper.showLoading(this)
        viewModel.getData()
    }


    private fun initView() {
        WidgetUtils.setClickListener(this, imgClose)
        swipe_container.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.lightBlue))

        swipe_container.setOnRefreshListener {
            getData()
        }
    }

    private fun initRecyclerView() {
        adapter = ListProductHistoryAdapter()
        rcvContent.adapter = adapter
        rcvContent.layoutManager = LinearLayoutManager(this)
    }

    private fun listenerData() {
        viewModel.onSetData.observe(this, Observer {
            DialogHelper.closeLoading(this)
            swipe_container.isRefreshing = false
            adapter.setData(it)
        })
        viewModel.onAddData.observe(this, Observer {
            DialogHelper.closeLoading(this)
            swipe_container.isRefreshing = false
            adapter.addData(it)
        })
        viewModel.onError.observe(this, Observer {
            DialogHelper.closeLoading(this)
            swipe_container.isRefreshing = false
            adapter.setError(it)
        })
    }

    fun loadMore() {
       Handler().postDelayed({
           viewModel.getData(true)
       },1000)
    }

    fun getData() {
        swipe_container.isRefreshing = true
        viewModel.getData()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgClose -> {
                onBackPressed()
            }
        }
    }
}