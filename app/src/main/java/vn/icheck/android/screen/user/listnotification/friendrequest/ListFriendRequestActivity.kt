package vn.icheck.android.screen.user.listnotification.friendrequest

import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_list_friend_request.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICError
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.SizeHelper
import java.util.concurrent.TimeUnit

/**
 * Created by VuLCL on 6/9/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ListFriendRequestActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    private val viewModel: ListFriendRequestViewModel by viewModels()

    private lateinit var adapter: ListFriendRequestAdapter

    private lateinit var dispose: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_friend_request)

        setupToolbar()
        setupRecyclerView()
        setupViewModel()
        setupSwipeLayout()
        setupListener()
    }

    private fun setupToolbar() {
        layoutToolbar.setPadding(0, SizeHelper.size16, 0, 0)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.setText(R.string.loi_moi_ket_ban)
    }

    private fun setupRecyclerView() {
        adapter = ListFriendRequestAdapter(this).apply {
            onUpdateRequest = {
                this.removeItem(it)
                if (this.isEmpty) {
                    if (edtSearch.text.isNullOrEmpty()) {
                        setError(ICError(R.drawable.ic_no_campaign, ICheckApplication.getString(R.string.khong_co_loi_moi_ket_ban_nao), null, 0))
                    } else {
                        setError(ICError(R.drawable.ic_search_90dp, ICheckApplication.getString(R.string.message_not_found), null, 0))
                    }
                }
                setResult(RESULT_OK)
            }
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val horizontalDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        horizontalDecoration.setDrawable(ShapeDrawable().apply {
            paint.color = ContextCompat.getColor(this@ListFriendRequestActivity, R.color.darkGray6)
            intrinsicHeight = SizeHelper.size1
        })
        recyclerView.addItemDecoration(horizontalDecoration)
    }

    private fun setupViewModel() {
        viewModel.onSetMessage.observe(this, Observer {
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

    private fun setupSwipeLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.lightBlue))

        swipeLayout.setOnRefreshListener {
            getListFriend()
        }

        swipeLayout.post {
            getListFriend()
        }
    }

    private fun setupListener() {
        dispose = RxTextView.textChangeEvents(edtSearch)
                .skipInitialValue()
                .distinctUntilChanged()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    getListFriend()
                }
    }

    private fun getListFriend() {
        swipeLayout.isRefreshing = true
        viewModel.getFriendRequest(edtSearch.text.toString())
    }

    override fun onMessageClicked() {
        getListFriend()
    }

    override fun onLoadMore() {
        viewModel.getFriendRequest(edtSearch.text.toString(), true)
    }

    override fun onDestroy() {
        dispose.dispose()
        viewModel.dispose()
        super.onDestroy()
    }
}