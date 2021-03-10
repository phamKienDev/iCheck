package vn.icheck.android.screen.user.option_manager_user_watching

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_manager_user_watching.edtSearch
import kotlinx.android.synthetic.main.activity_manager_user_watching.imgBack
import kotlinx.android.synthetic.main.activity_manager_user_watching.rcv_follow
import kotlinx.android.synthetic.main.activity_manager_user_watching.swipe_layout
import kotlinx.android.synthetic.main.activity_manager_user_watching.tvCount
import kotlinx.android.synthetic.main.activity_manager_user_watching.txtTitle
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.wall.ICUserFollowWall
import vn.icheck.android.screen.user.option_manger_user_follow.IUserFollowWallView
import vn.icheck.android.screen.user.option_manger_user_follow.UserFollowAdapter
import java.util.concurrent.TimeUnit

class ManagerUserWatchingActivity : BaseActivityMVVM(), IUserFollowWallView {

    private val viewModel: ManagerUserWatchingViewModel by viewModels()

    private val adapter = UserFollowAdapter(this)

    private var key: String? = null

    private var positionList: Int? = null

    private var disposable: Disposable? = null

    private var initFirst = false

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_user_watching)
        initView()
        initSwipeLayput()
        listener()
        initRecyclerView()
        listenerGetData()
        viewModel.getListUserWatching()
    }

    private fun initView() {
        txtTitle.text = "Quản lý danh sách đang theo dõi"
    }

    private fun initSwipeLayput() {
        swipe_layout.setColorSchemeColors(ContextCompat.getColor(this, R.color.lightBlue), ContextCompat.getColor(this, R.color.lightBlue), ContextCompat.getColor(this, R.color.lightBlue))

        swipe_layout.setOnRefreshListener {
            swipe_layout.isRefreshing = true
            viewModel.getListUserWatching()
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
                    viewModel.getListUserWatching(it.view().text.toString(),false,true)
                }
    }

    private fun listenerGetData() {
        runOnUiThread {
            viewModel.addFriend.observe(this, Observer {
                adapter.updateState(positionList!!)
            })

            viewModel.listData.observe(this, Observer {
                swipe_layout.isRefreshing = false
                tvCount.text = "${it.count} người bạn đang theo dõi"
                adapter.addListData(it.rows)

                if (!initFirst){
                    initTextListener()
                }
            })

            viewModel.isLoadMoreData.observe(this, Observer {
                adapter.removeDataWithoutUpdate()
            })

            viewModel.statusCode.observe(this, Observer {
                when (it) {
                    ICMessageEvent.Type.ON_NO_INTERNET -> {
                        DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                            override fun onDisagree() {
                            }

                            override fun onAgree() {
                                viewModel.getListUserWatching()
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

            viewModel.errorPutData.observe(this, Observer {
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

            viewModel.errorData.observe(this, Observer {
                swipe_layout.isRefreshing = false
                when (it) {
                    Constant.ERROR_EMPTY_SEARCH -> {
                        adapter.setErrorCode(Constant.ERROR_EMPTY_SEARCH)
                    }
                    Constant.ERROR_EMPTY_WATCHING -> {
                        edtSearch.visibility = View.GONE
                        tvCount.visibility = View.GONE
                        adapter.setErrorCode(Constant.ERROR_EMPTY_WATCHING)
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
        positionList = position
        viewModel.addFriend(item.id)
    }

    override fun onLoadMore() {
        runOnUiThread {
            swipe_layout.isRefreshing = false
            viewModel.getListUserWatching(key, true)
        }
    }

    override fun onRefresh() {
        runOnUiThread {
            swipe_layout.isRefreshing = true
            viewModel.getListUserWatching()
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.ON_CHANGE_FOLLOW -> {
                viewModel.getListUserWatching()
            }
            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        edtSearch.setText("")
        viewModel.getListUserWatching()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
    }
}