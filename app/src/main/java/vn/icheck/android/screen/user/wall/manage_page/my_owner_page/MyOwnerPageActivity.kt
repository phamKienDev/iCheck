package vn.icheck.android.screen.user.wall.manage_page.my_owner_page

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_my_owner_page.*
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.Status
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.StatusBarUtils
import java.util.concurrent.TimeUnit

class MyOwnerPageActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    lateinit var viewModel: MyOwnerPageViewModel
    lateinit var adapter: MyOwnerPageAdapter
    private var disposable: Disposable? = null

    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_owner_page)

        viewModel = ViewModelProvider(this).get(MyOwnerPageViewModel::class.java)
        initView()
        initRecyclerView()
        listenData()
    }

    private fun initView() {
        edtSearch.background=vn.icheck.android.ichecklibs.ViewHelper.bgGrayCorners4(this)

        img_back.setOnClickListener {
            onBackPressed()
        }

        img_clear.setOnClickListener {
            edtSearch.setText("")
        }

        StatusBarUtils.setOverStatusBarDark(this)

        val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipe_layout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)
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

    private fun listenData() {
        lifecycleScope.launch {
            viewModel.getData(edtSearch.text.toString(), offset).observe(this@MyOwnerPageActivity, {
                swipe_layout.isRefreshing = false
                if (it.status == Status.SUCCESS) {
                    if (offset == 0) {
                        if (it.data?.data?.rows.isNullOrEmpty()) {
                            tvPageCount.beGone()
                            adapter.setError(R.drawable.ic_search_90dp, getString(R.string.khong_ket_qua_tim_kiem), -1)
                        } else {
                            tvPageCount.beVisible()
                            tvPageCount.setText(R.string.s_trang_cua_toi, TextHelper.formatMoneyPhay(it.data?.data?.count))
                            adapter.setListData(it.data?.data?.rows ?: mutableListOf())
                        }
                    } else {
                        adapter.addListData(it.data?.data?.rows ?: mutableListOf())
                    }
                    offset += APIConstants.LIMIT
                } else if (it.status == Status.ERROR_NETWORK || it.status == Status.ERROR_REQUEST) {
                    if (adapter.isEmpty) {
                        adapter.setError(if (it.status == Status.ERROR_NETWORK) {
                            R.drawable.ic_error_network
                        } else {
                            R.drawable.ic_error_request
                        }, it.message
                                ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai), R.string.thu_lai)
                    } else {
                        showShortError(it.message
                                ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }
            })
        }
    }

    private fun getData() {
        if (edtSearch.text.toString().isEmpty()) {
            img_clear.beGone()
        } else {
            img_clear.beVisible()
        }

        swipe_layout.isRefreshing = true
        offset = 0
        listenData()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        listenData()
    }
}