package vn.icheck.android.screen.user.bookmark

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_book_mark_v2.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.history.ICBigCorp
import vn.icheck.android.screen.user.cart.CartActivity
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.ToastUtils
import java.util.concurrent.TimeUnit

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class BookMarkV2Activity : BaseActivityMVVM(), IScanHistoryView, IRecyclerViewCallback {

    lateinit var viewModel: BookMarkV2ViewModel

    private var dispose: Disposable? = null

    var search: String? = null

    val adapter = BookMarkV2Adapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_mark_v2)
        viewModel = ViewModelProvider(this).get(BookMarkV2ViewModel::class.java)

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
        imgHome.setOnClickListener {

        }
        imgAction.setOnClickListener {
            startActivity<CartActivity>()
        }
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
        edtSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyboardUtils.hideSoftInput(this@BookMarkV2Activity)
                    return true
                }
                return false
            }
        })
        dispose = RxTextView.afterTextChangeEvents(edtSearch)
                .skipInitialValue().debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    search = it.view().text.toString()
                    viewModel.getListBookMark(false, it.view().text.toString())
                }

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

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getListBookMark(false, search)
    }

    override fun onClickQrType(item: String?) {

    }

    override fun onValidStamp(item: String?) {

    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListBookMark(true, search)
    }

    override fun onLoadmore() {

    }

    override fun onClickBigCorp(item: ICBigCorp) {

    }

    override fun onMessageErrorMenu() {

    }

    override fun unCheckAllFilterHistory() {

    }

    override fun onCloseDrawer() {

    }

    override fun onDestroy() {
        super.onDestroy()

        dispose?.dispose()
    }
}