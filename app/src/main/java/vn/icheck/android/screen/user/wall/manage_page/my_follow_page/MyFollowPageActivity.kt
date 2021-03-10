package vn.icheck.android.screen.user.wall.manage_page.my_follow_page

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_my_follow_page.*
import kotlinx.android.synthetic.main.activity_my_follow_page.edtSearch
import kotlinx.android.synthetic.main.activity_my_follow_page.img_clear
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import java.util.concurrent.TimeUnit

class MyFollowPageActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    lateinit var viewModel: MyFollowPageViewModel
    lateinit var adapter: MyFollowPageAdapter
    private var disposable: Disposable? = null

    private var pageId: Long? = null
    private var isChange = false
    private var dialog: MyFollowPageDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_follow_page)

        viewModel = ViewModelProvider(this).get(MyFollowPageViewModel::class.java)
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

        DialogHelper.showLoading(this)

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
        adapter = MyFollowPageAdapter(false, this)
        rcvContent.adapter = adapter
    }

    private fun listenerData() {
        viewModel.getData(edtSearch.text.toString())

        viewModel.onSetData.observe(this, {
            swipe_layout.isRefreshing = false
            if (it.isNullOrEmpty()) {
                tvPageCount.beGone()
                adapter.setError(R.drawable.ic_search_90dp, "Xin lỗi chúng tôi không thể tìm được kết quả phù hợp với tìm kiếm của bạn", -1)
            } else {
                tvPageCount.beVisible()
                tvPageCount.text = TextHelper.formatMoneyPhay(viewModel.countPage) + " Trang đang theo dõi"
                adapter.setListData(it)
            }
        })

        viewModel.onAddData.observe(this, {
            swipe_layout.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.onState.observe(this, {
            swipe_layout.isRefreshing = false
            when (it.type) {
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.closeLoading(this)
                    if (adapter.isEmpty) {
                        adapter.setError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), R.string.thu_lai)
                    } else {
                        showShortError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                    }
                }
                else -> {
                    DialogHelper.closeLoading(this)
                    val error = it.data as String
                    if (adapter.isEmpty) {
                        adapter.setError(R.drawable.ic_error_request, error, R.string.thu_lai)
                    } else {
                        showShortError(error)
                    }
                }

            }
        })
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.UPDATE_FOLLOW_PAGE -> {
                pageId = event.data as Long
                viewModel.countPage -= 1
                tvPageCount.text = TextHelper.formatMoneyPhay(viewModel.countPage) + " Trang đang theo dõi"
                adapter.deleteItem(pageId!!)
                isChange = true
                if (viewModel.countPage <= 0) {
                    tvPageCount.beGone()
                    adapter.setError(R.drawable.ic_group_120dp, "Bạn chưa có trang nào", -1)
                }
            }
            ICMessageEvent.Type.SHOW_DIALOG_MY_FOLLOW_PAGE -> {
                if (event.data != null && event.data is Long) {
                    KeyboardUtils.hideSoftInput(edtSearch)
                    dialog = MyFollowPageDialog(event.data)
                    dialog?.show((ICheckApplication.currentActivity() as AppCompatActivity).supportFragmentManager, null)
                }
            }
            ICMessageEvent.Type.DISMISS_DIALOG -> {
                adapter.dismissDialog()
            }
            else -> {
            }
        }
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

    override fun onBackPressed() {
        if (isChange) {
            Intent().apply {
                setResult(Activity.RESULT_OK, this)
            }
        }
        super.onBackPressed()
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