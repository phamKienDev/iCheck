package vn.icheck.android.screen.user.wall.manage_page.my_owner_page

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_my_owner_page.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.showShortError
import java.util.concurrent.TimeUnit

class MyOwnerPageActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    lateinit var viewModel: MyOwnerPageViewModel
    lateinit var adapter: MyOwnerPageAdapter
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_owner_page)

        viewModel = ViewModelProvider(this).get(MyOwnerPageViewModel::class.java)
        DialogHelper.showLoading(this)
        initView()
        initRecyclerView()
        listenerData()
    }

    private fun initView() {
        img_back.setOnClickListener {
            onBackPressed()
        }

        img_clear.setOnClickListener {
            edtSearch.setText("")
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        swipe_layout.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.lightBlue))
        swipe_layout.setOnRefreshListener {
            getData()
        }
        disposable = RxTextView.textChangeEvents(edtSearch)
                .skipInitialValue()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    getData()
                }
    }

    private fun initRecyclerView() {
        adapter = MyOwnerPageAdapter(false, this)
        rcvContent.adapter = adapter
    }

    private fun listenerData() {
        viewModel.getData(edtSearch.text.toString())

        viewModel.onSetData.observe(this, Observer {
            swipe_layout.isRefreshing = false
            if (it.rows.isNullOrEmpty()) {
                tvPageCount.beGone()
                adapter.setError(R.drawable.ic_search_90dp, "Xin lỗi chúng tôi không thể tìm được kết quả phù hợp với tìm kiếm của bạn", -1)
            } else {
                tvPageCount.beVisible()
                tvPageCount.setText(TextHelper.formatMoneyPhay(it.count) + " Trang của tôi")
                adapter.setListData(it.rows)
            }
        })

        viewModel.onAddData.observe(this, Observer {
            adapter.addListData(it)
        })

        viewModel.onState.observe(this, Observer {
            swipe_layout.isRefreshing = false
            when (it.type) {
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                    DialogHelper.closeLoading(this)
                    val error = it.data as ICError
                    if (adapter.isEmpty) {
                        error.message?.let { it1 -> adapter.setError(error.icon, it1, R.string.thu_lai) }
                    } else {
                        showShortError(error.message)
                    }
                }
            }
        })
    }

    private fun getData() {
        if (edtSearch.text.toString().isEmpty()) {
            img_clear.beGone()
        } else {
            img_clear.beVisible()
        }

        swipe_layout.isRefreshing = true
        viewModel.getData(edtSearch.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getData(edtSearch.text.toString(), true)
    }
}