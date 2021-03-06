package vn.icheck.android.screen.user.option_manger_user_follow

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_manager_user_follow.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.models.wall.ICUserFollowWall
import java.util.concurrent.TimeUnit

class  ManagerUserFollowActivity : BaseActivityMVVM(), IUserFollowWallView {

    private val viewModel: ManagerUserFollowViewModel by viewModels()

    private val adapter = UserFollowAdapter(this)

    private var key: String? = null


    private var disposable: Disposable? = null

    private var initFirst = false

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_user_follow)

        initView()
        initSwipeLayput()
        listener()
        initRecyclerView()
        listenerGetData()
        viewModel.getListUserFollow()
    }

    private fun initView() {
        txtTitle.setText(R.string.quan_ly_danh_sach_nguoi_theo_doi)
        edtSearch.background= ViewHelper.bgGrayCorners4(this)
    }

    private fun initSwipeLayput() {
        val swipeColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipe_layout.setColorSchemeColors(swipeColor, swipeColor, swipeColor)

        swipe_layout.setOnRefreshListener {
            swipe_layout.isRefreshing = true
            viewModel.getListUserFollow(key)
        }
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        rcv_follow.layoutManager = LinearLayoutManager(this)
        rcv_follow.adapter = adapter
    }

    private fun initTextListener() {
        initFirst = true
        disposable = RxTextView.afterTextChangeEvents(edtSearch)
                .skipInitialValue()
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    key = it.view().text.toString()
                    adapter.clearListData()
                    viewModel.getListUserFollow(it.view().text.toString(),false,true)
                }
    }

    private fun listenerGetData() {
        runOnUiThread {
            viewModel.listData.observe(this, {
                swipe_layout.isRefreshing = false
                tvCount.setText(R.string.d_nguoi_dang_theo_doi_ban, it.count)
                adapter.addListData(it.rows)
                if (!initFirst){
                    initTextListener()
                }
            })

            viewModel.isLoadMoreData.observe(this, {
                adapter.removeDataWithoutUpdate()
            })

            viewModel.statusCode.observe(this, {
                when (it) {
                    ICMessageEvent.Type.ON_NO_INTERNET -> {
                        DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                            override fun onDisagree() {
                            }

                            override fun onAgree() {
                                viewModel.getListUserFollow()
                            }
                        })
                    }
                    ICMessageEvent.Type.ON_SHOW_LOADING -> {
                        DialogHelper.showLoading(this)
                    }
                    ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                        DialogHelper.closeLoading(this)
                    }
                    else -> {
                    }
                }
            })

            viewModel.errorPutData.observe(this, {
                when (it) {
                    Constant.ERROR_INTERNET -> {
                        showShortError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    }
                    Constant.ERROR_UNKNOW -> {
                        showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                    Constant.ERROR_SERVER -> {
                        showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
            })

            viewModel.errorData.observe(this, {
                swipe_layout.isRefreshing = false
                when (it) {
                    Constant.ERROR_EMPTY_SEARCH -> {
                        adapter.setErrorCode(Constant.ERROR_EMPTY_SEARCH)
                    }
                    Constant.ERROR_EMPTY_FOLLOW -> {
                        edtSearch.visibility = View.GONE
                        tvCount.visibility = View.GONE
                        adapter.setErrorCode(Constant.ERROR_EMPTY_FOLLOW)
                    }

                    Constant.ERROR_SERVER -> {
                        adapter.setErrorCode(Constant.ERROR_SERVER)
                    }

                    Constant.ERROR_INTERNET -> {
                        adapter.setErrorCode(Constant.ERROR_INTERNET)
                    }
                }
            })
        }
    }

    override fun addFriend(item: ICUserFollowWall, position: Int) {
        viewModel.addFriend(item.id,null)
    }

    override fun acceptFriend(item: ICUserFollowWall, position: Int) {
        viewModel.addFriend(item.id,2)
    }

    override fun onLoadMore() {
        runOnUiThread {
            swipe_layout.isRefreshing = false
            viewModel.getListUserFollow(key, true)
        }
    }

    override fun onRefresh() {
        runOnUiThread {
            swipe_layout.isRefreshing = true
            viewModel.getListUserFollow(key)
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.ON_CHANGE_FOLLOW -> {
                viewModel.getListUserFollow(key)
            }
            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        edtSearch.setText("")
        viewModel.getListUserFollow()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
    }
}