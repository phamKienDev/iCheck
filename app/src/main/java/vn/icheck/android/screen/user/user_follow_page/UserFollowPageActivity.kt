package vn.icheck.android.screen.user.user_follow_page

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_user_follow_page.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.invite_friend_follow_page.InviteFriendFollowPageActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils

class UserFollowPageActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    lateinit var viewModel: UserFollowPageViewModel
    lateinit var adapter: UserFollowPageAdapter

    private val requestLoginV2 = 1
    private var pageId: Any? = null
    private var isActivityVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_follow_page)

        viewModel = ViewModelProvider(this).get(UserFollowPageViewModel::class.java)
        initView()
        initRecyclerView()
        listenerData()
    }

    private fun initView() {
        img_back.setOnClickListener { onBackPressed() }

        DialogHelper.showLoading(this)
        viewModel.getData(intent)

        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipe_container.setColorSchemeColors(primaryColor, primaryColor, primaryColor)
        swipe_container.setOnRefreshListener {
            getData()
        }
    }

    private fun initRecyclerView() {
        adapter = UserFollowPageAdapter(this)
        rcvContent.adapter = adapter
        rcvContent.layoutManager = LinearLayoutManager(this)
    }

    private fun listenerData() {
        viewModel.onInfoData.observe(this, {
            DialogHelper.closeLoading(this)
            swipe_container.isRefreshing = false
            adapter.addHeader(it)
        })

        viewModel.onCountUserData.observe(this, {
            swipe_container.isRefreshing = false
            adapter.addCount(it)
        })

        viewModel.onListUserData.observe(this, {
            DialogHelper.closeLoading(this)
            swipe_container.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.onError.observe(this, {
            DialogHelper.closeLoading(this)
            swipe_container.isRefreshing = false
            if (!adapter.isEmpty) {
                ToastUtils.showShortError(this, it.message)
            } else {
                adapter.setError(it)
            }
        })
    }

    fun getData() {
        swipe_container.isRefreshing = true
        adapter.resetData()
        viewModel.getData(intent)
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (isActivityVisible) {
            if (event.type == ICMessageEvent.Type.ON_LOG_IN) {
                pageId = event.data
                onRequireLogin(requestLoginV2)
            }
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        if(requestCode==requestLoginV2){
            if (pageId != null) {
                ActivityUtils.startActivity<InviteFriendFollowPageActivity, Long>(this, Constant.DATA_1, pageId!! as Long)
            } else {
                getData()
            }
            pageId = null
        }
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListUserFollowPage(true)
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

}