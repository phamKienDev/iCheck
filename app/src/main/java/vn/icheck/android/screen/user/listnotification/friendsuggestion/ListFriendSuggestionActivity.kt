package vn.icheck.android.screen.user.listnotification.friendsuggestion

import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_friend_suggestion.*
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
class ListFriendSuggestionActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    private lateinit var viewModel: ListFriendSuggestionViewModel

    private val adapter = ListFriendSuggestionAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_friend_suggestion)

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

        txtTitle.setText(R.string.goi_y_ket_ban)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ListFriendSuggestionViewModel::class.java)

        viewModel.onSetData.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false
            it.message?.let { it1 -> adapter.setError(it.icon, it1, -1) }
        })
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.disableLoadMore()
        val horizontalDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        horizontalDecoration.setDrawable(ShapeDrawable().apply {
            paint.color = ContextCompat.getColor(this@ListFriendSuggestionActivity, R.color.colorBackgroundGra1y)
            intrinsicHeight = SizeHelper.size1
        })
        recyclerView.addItemDecoration(horizontalDecoration)
    }

    private fun setupSwipeLayout() {
        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = true
            doSearch()
        }

        swipeLayout.post {
            swipeLayout.isRefreshing = true
            doSearch()
        }
        edt_search.addTextChangedListener {
            doSearch()
        }
    }

    private fun doSearch() {
        delayAction({
            if (edt_search.text?.trim().toString().isNotEmpty()) {
                viewModel.getFriendRequest(filterString = edt_search.text?.trim().toString())
            } else {
                viewModel.getFriendRequest()
            }
        })
    }

    override fun onMessageClicked() {
        doSearch()
    }

    override fun onLoadMore() {
        if (edt_search.text?.trim().toString().isNotEmpty()) {
            viewModel.getFriendRequest(true,filterString = edt_search.text?.trim().toString())
        } else {
            viewModel.getFriendRequest(true)
        }
    }
}