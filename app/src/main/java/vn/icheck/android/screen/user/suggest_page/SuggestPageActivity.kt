package vn.icheck.android.screen.user.suggest_page

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_suggest_page.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICSuggestPage
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.ick.startClearTopActivity

class SuggestPageActivity : BaseActivityMVVM(), IRecyclerViewCallback {

    lateinit var viewModel: SuggestPageViewModel
    lateinit var adapter: SuggestPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_page)

        viewModel = ViewModelProvider(this).get(SuggestPageViewModel::class.java)
        initView()
        initRecyclerView()
        setUpListener()

        DialogHelper.showLoading(this)
        viewModel.getData(intent)
    }


    private fun initView() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnHome.apply {
            background = ViewHelper.bgOutlinePrimary1Corners4(context)
            setOnClickListener {
                DialogHelper.showLoading(this@SuggestPageActivity)
                viewModel.postFavouriteTopic(adapter.listSelected)
            }
        }
    }

    private fun initRecyclerView() {
        adapter = SuggestPageAdapter(this)
        rcv_suggest_page.adapter = adapter
        rcv_suggest_page.layoutManager = CustomGridLayoutManager(this, 2, RecyclerView.VERTICAL, reverseLayout = false, isScrollVertical = true).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getListData.isEmpty()) {
                        2
                    } else {
                        if (position < adapter.getListData.size && adapter.getListData[position] is ICSuggestPage) {
                            1
                        } else {
                            2
                        }
                    }
                }
            }
        }
    }

    private fun setUpListener() {
        viewModel.onSuggestPage.observe(this, Observer {
            DialogHelper.closeLoading(this)
            adapter.addListData(it)
        })

        viewModel.onSuggestFriend.observe(this, Observer {
            DialogHelper.closeLoading(this)
            adapter.addFriend(it)
        })

        viewModel.onError.observe(this, Observer {
            DialogHelper.closeLoading(this)
            if (adapter.getListData.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it.icon, it1, R.string.thu_lai) }
            } else {
                showShortError(ICheckApplication.getError(it.message))
            }
        })
        viewModel.onPostSuccess.observe(this, Observer {
            DialogHelper.closeLoading(this)
            finish()
            startClearTopActivity(HomeActivity::class.java)
        })
    }

    override fun onMessageClicked() {
        DialogHelper.showLoading(this)
        viewModel.getData(intent)
    }

    override fun onLoadMore() {
        viewModel.getSuggestPage(true)
    }
}
