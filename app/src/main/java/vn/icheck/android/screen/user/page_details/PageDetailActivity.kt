package vn.icheck.android.screen.user.page_details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_page_detail.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.header_page.bottom_sheet_header_page.MoreActionPageBottomSheet
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICMediaPage
import vn.icheck.android.network.models.ICPageOverview
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.screen.user.page_details.fragment.page.PageDetailFragment
import vn.icheck.android.screen.user.page_details.fragment.product.ProductOfPageFragment
import vn.icheck.android.screen.user.social_chat.SocialChatActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.*

/**
 * Phạm Hoàng Phi Hùng
 * hungphp@icheck.vn
 * 0974815770
 */
@AndroidEntryPoint
class PageDetailActivity : BaseActivityMVVM(), View.OnClickListener {
    private lateinit var viewModel: PageDetailViewModel
    private var pageID = -1L
    private var pageType = Constant.PAGE_BRAND_TYPE
    private var isShowLayoutFollow = false

    private val requireLogin = 1
    private val requestPageImage = 2
    private var isActivityVisible = false

    companion object {
        fun start(activity: Activity, pageID: Long, pageType: Int = Constant.PAGE_BRAND_TYPE) {
            val intent = Intent(activity, PageDetailActivity::class.java)
            intent.putExtra(Constant.DATA_1, pageID)
            intent.putExtra(Constant.DATA_2, pageType)
            ActivityUtils.startActivity(activity, intent)
        }

        fun start(context: Context, pageID: Long, pageType: Int = Constant.PAGE_BRAND_TYPE) {
            val intent = Intent(context, PageDetailActivity::class.java)
            intent.putExtra(Constant.DATA_1, pageID)
            intent.putExtra(Constant.DATA_2, pageType)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_detail)

        StatusBarUtils.setOverStatusBarDark(this)

        getData()
        setupViewPager()
        setupViewModel()
        setupView()
        setupListener()
        listenerFirebase()
        initButton()
    }

    private fun getData() {
        pageID = intent?.getLongExtra(Constant.DATA_1, -1) ?: -1
        pageType = intent?.getIntExtra(Constant.DATA_2, Constant.PAGE_BRAND_TYPE)
                ?: Constant.PAGE_BRAND_TYPE
    }

    private fun setupViewPager() {
        viewPager.offscreenPageLimit = 2
        viewPager.setPagingEnabled(false)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, mutableListOf<ICFragment>().apply {
            add(ICFragment(null, PageDetailFragment.newInstance(pageID, pageType)))
            add(ICFragment(null, ProductOfPageFragment.newInstance()))
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PageDetailViewModel::class.java)
    }

    private fun setupView() {
        ViewHelper.textColorDisableTextUncheckPrimaryChecked(this).apply {
            tvHome.setTextColor(this)
            tvPost.setTextColor(this)
            tvProduct.setTextColor(this)
        }

        ViewHelper.bgOutlinePrimary1Corners4(this).apply {
            tvExtra.background = this
            imgMenu.background = this
        }

        btnFollow.background = ViewHelper.bgPrimaryCorners4(this)
    }

    private fun setupListener() {
        WidgetUtils.setClickListener(this, tvHome, tvPost, tvProduct)
    }

    private fun selectTab(position: Int) {
        when (position) {
            1 -> {
                isChecked(tvHome)
                viewPager.setCurrentItem(0, false)
            }
            2 -> {
                isChecked(tvPost)
                viewPager.setCurrentItem(0, false)
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ONCLICK_LISTPOST_OF_PAGE))
            }
            3 -> {
                isChecked(tvProduct)
                viewPager.setCurrentItem(2, false)
            }
        }
    }

    private fun isChecked(view: AppCompatCheckedTextView): Boolean {
        return if (view.isChecked) {
            true
        } else {
            unCheckAll()
            view.isChecked = true
            false
        }
    }

    private fun unCheckAll() {
        tvHome.isChecked = false
        tvPost.isChecked = false
        tvProduct.isChecked = false
    }

    private fun listenerFirebase() {
        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFollowingPageIdList, pageID.toString(), object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModel.isFollowPage = snapshot.value != null && snapshot.value is Long
                    viewModel.pageOverview?.isFollow = viewModel.isFollowPage
                    checkFollowState(viewModel.isFollowPage)
                }

                override fun onCancelled(error: DatabaseError) {
                    logError(error.toException())
                }
            })

        } else {
            viewModel.isFollowPage = false
            checkFollowState(viewModel.isFollowPage)
        }
    }

    private fun showLayoutAction(isShow: Boolean) {
        if (isShow && viewModel.pageDetail != null) {
            Handler().postDelayed({
                layoutFollow.beVisible()
                view2.beVisible()
            }, 300)
        } else {
            Handler().postDelayed({
                layoutFollow.beGone()
                view2.beGone()
            }, 300)
        }
    }

    private fun initButton() {
        btnFollow.setOnClickListener {
            if (SessionManager.isUserLogged) {
                if (viewModel.isFollowPage) {
                    SocialChatActivity.createPageChat(this, pageID)
//                    ChatSocialDetailActivity.createRoomChat(this@PageDetailActivity, pageID, "page")
                } else {
                    viewModel.pageOverview?.let { page ->
                        viewModel.followPage(page.id)
                    }
                }
            } else {
                onRequireLogin(requireLogin)
            }
        }


        tvExtra.setOnClickListener {
            val phone = viewModel.pageDetail?.phone ?: ""
            if (phone.isNotEmpty()) {
                DialogHelper.showConfirm(this, applicationContext.getString(R.string.ban_co_muon_goi_dien_thoai_den_x, Constant.formatPhone(phone)), null, "Để sau", "Đồng ý", null, null, true, object : ConfirmDialogListener {
                    override fun onDisagree() {

                    }

                    override fun onAgree() {
                        phone.startCallPhone()
                    }
                })
            } else {
                DialogHelper.showDialogErrorBlack(this, this.getString(R.string.sdt_dang_cap_nhat))
            }
        }


        imgMenu.setOnClickListener {
            viewModel.pageOverview?.let { page ->
                object : MoreActionPageBottomSheet(this, page) {
                    override fun onClickUnfollow() {
                        if (SessionManager.isUserLogged) {
                            unfollowAndFollowPage(page)
                        } else {
                            onRequireLogin(requireLogin)
                        }
                    }

                    override fun onClickStateNotification() {
                        if (!page.unsubscribeNotice) {
                            viewModel.reSubcribePage(pageID)
                        } else {
                            viewModel.unSubcribePage(pageID)
                        }
                    }

                    override fun onClickReportPage() {
                        if (SessionManager.isUserLogged) {
                            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_BOTTOM_SHEET_REPORT))
                        } else {
                            onRequireLogin(requireLogin)
                        }
                    }
                }.show()
            }
        }
    }

    private fun checkFollowState(isFollow: Boolean) {
        tvPrimary.run {
            if (isFollow) {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_white_16dp, 0, 0, 0)
                setText(R.string.nhan_tin)
            } else {
                setText(R.string.theo_doi)
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_page, 0, 0, 0)
            }
        }
    }

    private fun unfollowAndFollowPage(data: ICPageOverview) {
        if (data.id != null) {
            if (viewModel.isFollowPage) {
                DialogHelper.showConfirm(this@PageDetailActivity, getString(R.string.ban_chac_chan_bo_theo_doi_trang_nay), null, getString(R.string.de_sau), getString(R.string.dong_y), true, object : ConfirmDialogListener {
                    override fun onDisagree() {}

                    override fun onAgree() {
                        viewModel.unFollowPage(data.id)
                    }
                })
            } else {
                viewModel.followPage(data.id)
            }
        }
    }

    fun openPageImage(item: ICMediaPage) {
        if (item.postId != null) {
            MediaInPostActivity.start(item.postId!!, this, null, requestPageImage)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvHome -> {
                if (!isChecked(view as AppCompatCheckedTextView)) {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ONCLICK_PAGE_OVERVIEW))
                    viewPager.setCurrentItem(0, false)
                }
            }
            R.id.tvPost -> {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ONCLICK_LISTPOST_OF_PAGE))
            }
            R.id.tvProduct -> {
                isShowLayoutFollow = false
                showLayoutAction(isShowLayoutFollow)
                if (!isChecked(view as AppCompatCheckedTextView)) {
                    viewPager.setCurrentItem(2, false)
                }
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.BACK -> {
                if (event.data != null && event.data is Int) {
                    selectTab(event.data)
                }
            }
            ICMessageEvent.Type.ON_UPDATE_PAGE_NAME -> {
                btnFollow.tag = event.data
            }
            ICMessageEvent.Type.HIDE_OR_SHOW_FOLLOW -> {
                isShowLayoutFollow = event.data as Boolean
                showLayoutAction(isShowLayoutFollow)
            }
            ICMessageEvent.Type.FOLLOW_PAGE -> {
                checkFollowState(true)
            }
            ICMessageEvent.Type.UNFOLLOW_PAGE -> {
                checkFollowState(false)
            }
            ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                if (isActivityVisible) {
                    onRequireLogin(requireLogin)
                }
            }
            ICMessageEvent.Type.ON_LOG_IN_FIREBASE -> {
                listenerFirebase()
            }
            ICMessageEvent.Type.CLICK_LISTPOST_OF_PAGE -> {
                if (event.data as Boolean) {
                    isChecked(tvPost)
                    isShowLayoutFollow = false
                    viewPager.setCurrentItem(0, false)
                    showLayoutAction(isShowLayoutFollow)
                } else {
                    showSimpleErrorToast(this.getString(R.string.hien_chua_co_bai_viet_nao))
                }
            }
            ICMessageEvent.Type.CLICK_PRODUCT_OF_PAGE -> {
                tvProduct.performClick()
            }
            else -> {
            }
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        if (requestCode == requireLogin) {
            viewModel.getLayoutPage()
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }


    override fun onBackPressed() {
        Intent().apply {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestPageImage) {
            if (resultCode == Activity.RESULT_OK) {
                tvHome.performClick()
                (viewPager.adapter as ViewPagerAdapter?)?.getFragment(0)?.let { fragment ->
                    if (fragment is PageDetailFragment) {
                        fragment.scrollToTop()
                    }
                }
            }
        }
    }
}