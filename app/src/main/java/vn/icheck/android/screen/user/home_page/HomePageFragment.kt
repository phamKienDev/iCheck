package vn.icheck.android.screen.user.home_page

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.layoutContainer
import kotlinx.android.synthetic.main.fragment_home.layoutLoading
import kotlinx.android.synthetic.main.fragment_home.recyclerView
import kotlinx.android.synthetic.main.fragment_home.swipeLayout
import kotlinx.android.synthetic.main.fragment_home.tvCartCount
import kotlinx.android.synthetic.main.fragment_home.viewShadow
import kotlinx.android.synthetic.main.fragment_page_detail.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.component.view.ViewHelper.setScrollSpeed
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.helper.FileHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableEndText
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.product_need_review.ICProductNeedReview
import vn.icheck.android.screen.dialog.DialogFragmentNotificationFirebaseAds
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.campaign.calback.IBannerV2Listener
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.campaign.calback.IProductNeedReviewListener
import vn.icheck.android.screen.user.edit_review.EditReviewActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.home_page.adapter.HomePageAdapter
import vn.icheck.android.screen.user.home_page.callback.IHomePageView
import vn.icheck.android.screen.user.home_page.holder.primaryfunction.HomeFunctionHolder
import vn.icheck.android.screen.user.home_page.reminders.ReminderHomeDialog
import vn.icheck.android.screen.user.list_trending_products.ListTrendingProductsActivity
import vn.icheck.android.screen.user.listnotification.ListNotificationActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.pvcombank.authen.CreatePVCardActivity
import vn.icheck.android.screen.user.pvcombank.authen.CreatePVCardViewModel
import vn.icheck.android.screen.user.pvcombank.cardhistory.HistoryPVCardActivity
import vn.icheck.android.screen.user.pvcombank.listcard.ListPVCardActivity
import vn.icheck.android.screen.user.search_home.main.SearchHomeActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.AdsUtils
import vn.icheck.android.util.ick.loadImageWithHolder
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.ick.simpleText
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 9/19/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
@AndroidEntryPoint
class HomePageFragment : BaseFragmentMVVM(), IBannerV2Listener, IMessageListener, IProductNeedReviewListener, IHomePageView, View.OnClickListener {
    private var homeAdapter = HomePageAdapter(this, this, this, this)

    private val viewModel: HomePageViewModel by activityViewModels()

    private val requestBannerSurvey = 1
    private val requestLoginNotification = 2
    private val requestLoginCart = 3
    private val requestProductNeedReview = 4
    private val requestOpenCart = 5
    private val requestPVCombank = 6

    private var pvCombankType = 0
    private var isViewCreated = false
    private var isOpen = false
    private var isRefreshLayout = false

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (val intExtra = intent?.getLongExtra("home_action", 0)) {
                1L -> {
                    ListTrendingProductsActivity.create(requireActivity(), intExtra)
                }
            }
            val categoryId = intent?.getLongExtra("select_category", 0L)
            if (categoryId != 0L) {
                homeAdapter.selectCategory(categoryId!!)
            }
        }
    }

    companion object {
        var INSTANCE: HomePageFragment? = null
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        checkTheme()
        INSTANCE = this
    }

    private fun setupView() {
        tvNotificationCount.background=vn.icheck.android.ichecklibs.ViewHelper.bgRedNotifyHome(requireContext())
        tv_count.background=vn.icheck.android.ichecklibs.ViewHelper.bgAccentRedCorners6(requireContext())
        layoutHeader.setPadding(0, getStatusBarHeight + SizeHelper.size16, 0, 0)
        tvCartCount.background=vn.icheck.android.ichecklibs.ViewHelper.bgAccentGreenNotificationHome(requireContext())

//        txtSearch.background = ViewHelper.createDrawableStateList(
//                ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.white_opacity_unknow), SizeHelper.size4.toFloat()),
//                ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.darkGray6), SizeHelper.size4.toFloat())
//        )

        tv_show_all_reminders.setOnClickListener {
            lifecycleScope.launch {
                tv_show_all_reminders.isEnabled = false
                ICheckApplication.currentActivity()?.let { activity ->
                    if (activity is HomeActivity) {
                        ReminderHomeDialog().apply {
                            show(activity.supportFragmentManager, null)
                        }
                    }
                }
                delay(500)
                tv_show_all_reminders.isEnabled = true
            }
        }

        group_notification.setOnClickListener {
            lifecycleScope.launch {
                group_notification.isEnabled = false
                ICheckApplication.currentActivity()?.let { activity ->
                    if (activity is HomeActivity) {
                        ReminderHomeDialog().apply {
                            show(activity.supportFragmentManager, null)
                        }
                    }
                }
                delay(500)
                group_notification.isEnabled = true
            }
        }

        getReminders()
    }

    private fun checkTheme() {
        homeAdapter.notifyDataSetChanged()

        val backgroundImage = BitmapFactory.decodeFile(FileHelper.getPath(this@HomePageFragment.requireContext()) + FileHelper.homeBackgroundImage)
        if (backgroundImage != null) {
            imgBackground?.apply {
                setImageBitmap(backgroundImage)
            }
            imgThemeBackground?.apply {
                layoutParams = FrameLayout.LayoutParams(swipeLayout.width, swipeLayout.height)
                setImageBitmap(backgroundImage)
            }
        } else {
            imgBackground?.setImageResource(0)
            imgThemeBackground?.setImageResource(0)
        }

        val theme = SettingManager.themeSetting?.theme
        if (theme != null) {
            txtSearch.background = ViewHelper.createDrawableStateList(
                    ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.white_opacity_unknow), SizeHelper.size4.toFloat()),
                    ViewHelper.createShapeDrawable(vn.icheck.android.ichecklibs.ColorManager.getAppBackgroundGrayColor(requireContext()), SizeHelper.size4.toFloat())
            )
            txtSearch.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_icheck_70dp_17dp), null, null, null)
        } else {
            txtSearch.background = ViewHelper.createDrawableStateList(
                    ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.white_opacity_unknow), SizeHelper.size4.toFloat()),
                    ViewHelper.createShapeDrawable(vn.icheck.android.ichecklibs.ColorManager.getAppBackgroundGrayColor(requireContext()), SizeHelper.size4.toFloat())
            )
            txtSearch.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_icheck_70dp_17dp), null, null, null)
        }

        if (!theme?.homeHeaderIconColor.isNullOrEmpty()) {
            txtAvatar.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_menu_white_24dp),
                    ViewHelper.getDrawableFillColor(R.drawable.ic_home_menu_white_24dp, ColorManager.convertColorRGBA(theme!!.homeHeaderIconColor!!))
            ), null, null, null)

            tvViewCart.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_shop_white_24dp),
                    ViewHelper.getDrawableFillColor(R.drawable.ic_home_shop_white_24dp, ColorManager.convertColorRGBA(theme.homeHeaderIconColor!!))
            ), null, null, null)

            txtNotification.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_notification_white_24),
                    ViewHelper.getDrawableFillColor(R.drawable.ic_notification_white_24, ColorManager.convertColorRGBA(theme.homeHeaderIconColor!!))
            ), null, null, null)
        } else {
            txtAvatar.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_menu_white_24dp), null, null, null)
            tvViewCart.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_shop_white_24dp), null, null, null)
            txtNotification.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_notification_white_24), null, null, null)
        }

        for (i in homeAdapter.listData.indices) {
            recyclerView.findViewHolderForAdapterPosition(i)?.let { viewHolder ->
                if (viewHolder is HomeFunctionHolder) {
                    viewHolder.updateTheme()
                    return
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel.onShowPopup.observe(viewLifecycleOwner, Observer {
            if(viewLifecycleOwner == null){
                return@Observer
            }
            AdsUtils.showAdsPopup(activity, it)
        })

        viewModel.onUpdateAds.observe(viewLifecycleOwner, Observer {
            if(viewLifecycleOwner == null){
                return@Observer
            }
            if (it == true) {
                closeLoading()
                homeAdapter.updateAds()
            }
        })

        viewModel.onError.observe(viewLifecycleOwner, Observer {
            if(viewLifecycleOwner == null){
                return@Observer
            }
            closeLoading()
            it.message?.let { it1 ->
                homeAdapter.setError(it.icon, it1)
            }
        })

        viewModel.onAddData.observe(viewLifecycleOwner, Observer {
            if(viewLifecycleOwner == null){
                return@Observer
            }
            homeAdapter.addItem(it)
        })

        viewModel.onUpdateData.observe(viewLifecycleOwner, Observer {
            if(viewLifecycleOwner == null){
                return@Observer
            }
            if (it.data != null) {
                closeLoading()
            }
            homeAdapter.updateItem(it)
            layoutHeader.beVisible()
        })

        viewModel.onUpdateListData.observe(viewLifecycleOwner, Observer {
            if(viewLifecycleOwner == null){
                return@Observer
            }
            homeAdapter.updateItem(it)
            layoutHeader.beVisible()
        })


        viewModel.onPopupAds.observe(viewLifecycleOwner, Observer {
            DialogFragmentNotificationFirebaseAds.showPopupAds(this@HomePageFragment.requireActivity(),it)
//            object : DialogNotificationFirebaseAds(requireActivity(),null,null,"http://icheck.com.vn",null) {
//                override fun onDismiss() {
//                }
//            }.show()
        })

//        viewModel.onUpdatePVCombank.observe(viewLifecycleOwner, Observer {
//            for (i in homeAdapter.listData.indices) {
//                if (homeAdapter.listData[i].viewType == ICViewTypes.HOME_PRIMARY_FUNC) {
//                    if (homeAdapter.listData[i].data != null) {
//                        val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
//                        if (viewHolder is HomeFunctionHolder) {
//                            viewHolder.updateHomePVCombank(it)
//                        } else {
//                            (homeAdapter.listData[i].data as  MutableList<Any?>).apply {
//                                if (size > 1) {
//                                    removeLast()
//                                }
//                                add(it)
//                            }
//                        }
//                    }
//                    return@Observer
//                }
//            }
//        })
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = homeAdapter
        recyclerView.setScrollSpeed()

        val alphaHeight = SizeHelper.size150
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemTopPosition = recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.y

                if (itemTopPosition != null) {
                    val visibility = when {
                        -itemTopPosition > 0 -> {
                            if (-itemTopPosition <= alphaHeight) {
                                (1f / alphaHeight) * -itemTopPosition
                            } else {
                                1f
                            }
                        }
                        -itemTopPosition < 0 -> {
                            if (-itemTopPosition < -alphaHeight) {
                                (-1f / alphaHeight) * -itemTopPosition
                            } else {
                                0f
                            }
                        }
                        else -> {
                            0f
                        }
                    }

                    setToolbarBackground(visibility)
                } else if (!swipeLayout.isRefreshing) {
                    setToolbarBackground(1f)
                    if (layoutContainer.currentState != layoutContainer.endState && viewModel.getRemindersCount() > 0) {
                        layoutContainer.transitionToEnd()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView, toolbarBackground.height)
                }
            }
        })
    }

    private fun setToolbarBackground(visibility: Float) {
        toolbarBackground.alpha = visibility
        viewShadow.alpha = visibility

        if (visibility < 0.5) {
            txtSearch.isChecked = false
            txtAvatar.isChecked = false
            tvViewCart.isChecked = false
            txtNotification.isChecked = false
            tvCartCount.isChecked = true
            tvNotificationCount.isChecked = true
        } else {
            txtSearch.isChecked = true
            txtAvatar.isChecked = true
            tvViewCart.isChecked = true
            txtNotification.isChecked = true
            tvNotificationCount.isChecked = true
            tvCartCount.isChecked = true
        }
    }

    private fun setupSwipeLayout() {
        val swipeColor = ColorManager.getPrimaryColor(requireContext())
        swipeLayout.setColorSchemeColors(swipeColor, swipeColor, swipeColor)

        swipeLayout.setOnRefreshListener {
            refreshHomeData()
        }

        swipeLayout.post {
            viewModel.getHomeLayout()
            viewModel.getHomePopup()
            viewModel.getPopupAds()
            // Gọi api pvcombank
        }
    }

    fun refreshHomeData() {
        if (!isOpen) {
            isRefreshLayout = true
        }

        swipeLayout.isRefreshing = true
        homeAdapter.removeAllView()
        viewModel.getHomeLayout()
        lifecycleScope.launch {
            delay(400)
            getReminders()
        }
    }

    private fun closeLoading() {
        swipeLayout.isRefreshing = false

        if (layoutLoading != null) {
            layoutContainer.removeView(layoutLoading)
        }
    }

    private fun updateHomeHeader() {
        for (i in homeAdapter.listData.indices) {
            recyclerView.findViewHolderForAdapterPosition(i)?.let { viewHolder ->
                if (viewHolder is HomeFunctionHolder) {
                    viewHolder.updateHomeHeader()
                    return
                }
            }
        }
    }

    override fun onBannerSurveyClicked(id: Long) {
        AdsUtils.bannerSurveyClicked(this, requestBannerSurvey, id)
    }

    override fun onMessageClicked() {
        swipeLayout.isRefreshing = true
        viewModel.getHomeLayout()
    }

    override fun onItemClickReview(position: Int, item: ICProductNeedReview) {
        if (!item.barcode.isNullOrEmpty() && item.id != null) {
            val intent = Intent(context, EditReviewActivity::class.java)
            intent.putExtra(Constant.DATA_1, item.id!!)
            intent.putExtra(Constant.DATA_2, item.barcode)
            intent.putExtra(Constant.DATA_3, "home")
            intent.putExtra(Constant.DATA_4, position)
            startActivityForResult(intent, requestProductNeedReview)
        }
    }

    override fun onItemClickProduct(position: Int, item: ICProductNeedReview) {
        if (!item.barcode.isNullOrEmpty() && item.id != null) {
            ICheckApplication.currentActivity()?.let {
                IckProductDetailActivity.start(it, item.barcode!!)
            }
        }
    }

    override fun onCreatePVCombank() {
        FirebaseDynamicLinksActivity.startDestinationUrl(this@HomePageFragment.requireActivity(), "digital_bank")
    }

    override fun onRechargePVCombank() {
        checkPVCombank(1)
    }

    override fun onInfoPVCombank() {
        checkPVCombank(2)
    }

    override fun onTransactionCombank() {
        checkPVCombank(3)
    }

    fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }

    private fun checkPVCombank(type: Int) {
        CreatePVCardViewModel().apply {
            checkHasCard(5000L).observe(this@HomePageFragment, Observer { checkCardRes ->
                this@HomePageFragment.apply {
                    when (checkCardRes.status) {
                        Status.LOADING -> {
                            DialogHelper.showLoading(this)
                        }
                        Status.ERROR_NETWORK -> {
                            DialogHelper.closeLoading(this)
                            ToastHelper.showLongError(requireContext(), R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                        }
                        Status.ERROR_REQUEST -> {
                            DialogHelper.closeLoading(this)
                            ToastHelper.showLongError(requireContext(), ICheckApplication.getError(checkCardRes.message))
                        }
                        Status.SUCCESS -> {
                            if (checkCardRes.data?.data == true) {
                                if (SettingManager.getSessionPvcombank.isEmpty()) {
                                    getFormAuth(5000L).observe(this, Observer { formAuthRes ->
                                        when (formAuthRes.status) {
                                            Status.LOADING -> {
                                            }
                                            Status.SUCCESS -> {
                                                DialogHelper.closeLoading(this)
                                                if (formAuthRes.data?.data?.redirectUrl.isNullOrEmpty() || formAuthRes.data?.data?.authUrl.isNullOrEmpty()) {
                                                    ToastHelper.showLongError(requireContext(), getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                                                } else {
                                                    pvCombankType = type
                                                    CreatePVCardActivity.redirectUrl = formAuthRes.data!!.data!!.redirectUrl
                                                    WebViewActivity.start(requireActivity(), formAuthRes.data!!.data!!.authUrl)
                                                }
                                            }
                                            else -> {
                                                DialogHelper.closeLoading(this)
                                                ToastHelper.showLongError(requireContext(), ICheckApplication.getError(checkCardRes.message))
                                            }
                                        }
                                    })
                                } else {
                                    DialogHelper.closeLoading(this)
                                    goToPVCombank(type)
                                }
                            } else {
                                DialogHelper.closeLoading(this)
                                goToPVCombank(type)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun goToPVCombank(type: Int) {
        when (type) {
            1 -> {

            }
            2 -> {
                ActivityHelper.startActivityForResult<ListPVCardActivity>(this, requestPVCombank)
            }
            3 -> {
                ActivityHelper.startActivityForResult<HistoryPVCardActivity>(this, requestPVCombank)
            }
        }

        pvCombankType = 0
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)

        when (requestCode) {
            requestLoginNotification -> {
                startActivity<ListNotificationActivity>()
            }
            requestLoginCart -> {
//                startActivity<CartActivity>()
                startActivityForResult(Intent(context, ShipActivity::class.java).apply {
                    putExtra(Constant.CART, true)
                }, requestOpenCart)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.GO_TO_HOME -> {
                recyclerView.smoothScrollToPosition(0)
            }
            ICMessageEvent.Type.UPDATE_UNREAD_NOTIFICATION -> {

                tvNotificationCount.visibility = if (event.data as Long > 0) {
                    if (event.data > 9) {
                        tvNotificationCount simpleText "9+"
                    } else {
                        tvNotificationCount simpleText event.data.toString()
                    }
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
            ICMessageEvent.Type.UPDATE_COIN_AND_RANK -> {
//                homeAdapter.notifyItemChanged(0)
                updateHomeHeader()
            }
//            ICMessageEvent.Type.UPDATE_COUNT_CART -> {
//                val count = event.data as String?
//                tvCartCount.visibleOrInvisible(count != null)
//                tvCartCount.text = count
//            }
//            ICMessageEvent.Type.ON_LOG_IN -> {

//                lifecycleScope.launch {
//                    val file = File(FileHelper.getPath(requireContext()) + FileHelper.imageFolder)
//                    if (file.exists()) {
//                        FileHelper.deleteTheme(file)
//                    }
//                    homeAdapter.notifyDataSetChanged()
//                    checkTheme()
//                    delay(400)
//                    getReminders()
//                    refreshHomeData()
//                }
//            }
//            ICMessageEvent.Type.ON_LOG_OUT -> {
//                lifecycleScope.launch {
//                    val file = File(FileHelper.getPath(requireContext()) + FileHelper.imageFolder)
//                    if (file.exists()) {
//                        FileHelper.deleteTheme(file)
//                    }
//                    homeAdapter.notifyDataSetChanged()
//                    checkTheme()
//                    delay(400)
//                    getReminders()
//                }
//                getCoin()
//
//                layoutContainer.setTransition(R.id.no_reminder)
//                tvCartCount.beGone()
//            }
            ICMessageEvent.Type.ON_UPDATE_AUTO_PLAY_VIDEO -> {
                if (isOpen) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView, layoutToolbarAlpha.height)
                }
            }
            ICMessageEvent.Type.UPDATE_REMINDER -> {
                getReminders()
            }
            ICMessageEvent.Type.DISMISS_REMINDER -> {
                getReminders()
//                if (layoutContainer.currentState != layoutContainer.endState && viewModel.getRemindersCount() ?: 0 > 0) {
//                    layoutContainer.transitionToEnd()
//                }
            }
            ICMessageEvent.Type.ON_SET_THEME -> {
                checkTheme()
            }
            ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                if (isOpen && !SessionManager.isUserLogged) {
                    onRequireLogin()
                }
            }
            ICMessageEvent.Type.ON_DESTROY_PVCOMBANK -> {
                viewModel.getPVCombank()
            }
            ICMessageEvent.Type.FINISH_CREATE_PVCOMBANK -> {
                goToPVCombank(pvCombankType)
            }
            else -> {
            }
        }
    }

    private fun getCoin() {
        // Lấy về Coin hiện tại
        viewModel.getCoin().observe(this, Observer {
            if (it.status == Status.SUCCESS) {
                SessionManager.setCoin(it.data?.data?.availableBalance ?: 0)
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COIN_AND_RANK))
            }
        })

        // Lấy về số lượng sản phẩm trong giỏ hàng
        viewModel.updateCountCart()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtAvatar -> {
                activity?.let {
                    if (it is HomeActivity) {
                        it.openSlideMenu()
                    }
                }
            }
            R.id.txtNotification -> {
//                if (SessionManager.isUserLogged) {
                startActivity<ListNotificationActivity>()
//                } else {
//                    onRequireLogin(requestLoginNotification)
//                }
            }
            R.id.tvViewCart -> {
                if (SessionManager.isUserLogged) {
                    startActivityForResult(Intent(context, ShipActivity::class.java).apply {
                        putExtra(Constant.CART, true)
                    }, requestOpenCart)
//                        ShipActivity.startForResult(requireActivity())
                } else {
                    onRequireLogin(requestLoginCart)
                }
            }
            R.id.txtSearch -> {
                startActivity<SearchHomeActivity>()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isOpen = false
    }

    override fun onResume() {
        super.onResume()
        isOpen = true

        TrackingAllHelper.trackHomePageViewed()

        if (requireActivity().intent?.getStringExtra(Constant.DATA_3).isNullOrEmpty()) {
            if (!isViewCreated) {
                isViewCreated = true
                Handler(Looper.getMainLooper()).post {
                    setupViewModel()
                    setupRecyclerView()
                    setupSwipeLayout()
                    LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("home"))
                    WidgetUtils.setClickListener(this, txtAvatar, txtNotification, tvViewCart, txtSearch)
                }
                return
            }
        } else {
            requireActivity().intent?.putExtra(Constant.DATA_3, "")
        }

//        viewModel.getAds(true)
        tvNotificationCount.visibility = if (RelationshipManager.unreadNotify > 0) {
            if (RelationshipManager.unreadNotify > 9) {
                tvNotificationCount simpleText "9+"
            } else {
                tvNotificationCount simpleText RelationshipManager.unreadNotify.toString()
            }
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        if (isRefreshLayout) {
            isRefreshLayout = false
            refreshHomeData()
        }

        updateHomeHeader()
        getCoin()
        getReminders()

        viewModel.getCartCount().observe(viewLifecycleOwner) {
            if (it.data != null) {
                val i = it.data
                viewModel.cartCount = it.data!!
                when {
                    i ?: 0 > 9 -> {
                        tvCartCount.text = "9+"
                        tvCartCount.beVisible()
                    }
                    i ?: 0 > 0 -> {
                        tvCartCount.text = "$i"
                        tvCartCount.beVisible()
                    }
                    else -> {
                        tvCartCount.beGone()
                    }
                }
            }
        }
    }

    private fun getReminders() {
        this@HomePageFragment.apply {
            viewModel.getReminders().observe(viewLifecycleOwner, Observer {
                if (tv_count == null) {
                    return@Observer
                }

                if (!it?.data?.rows.isNullOrEmpty() && isOpen) {
                    val i = viewModel.getRemindersCount() ?: 0
                    if (i > 9) {
                        tv_count.text = "9+"
                    } else if (i > 0) {
                        tv_count.text = "$i"
                    }
                    if (i == 0) {
                        tv_count.beGone()
                        layoutContainer.setTransition(R.id.no_reminder)
                    } else {
                        layoutContainer.setTransition(R.id.reminder)
                        tv_count.beVisible()
                        group_notification.beVisible()
                        tv_show_all_reminders.setText(R.string.xem_tat_ca_loi_nhac_d, viewModel.getRemindersCount())
                        tv_reminder_content.text = it?.data?.rows?.firstOrNull()?.message
                        tv_show_all_reminders.fillDrawableEndText(R.drawable.ic_arrow_down_blue_24dp)
                        if (!it?.data?.rows?.firstOrNull()?.label.isNullOrEmpty()) {
                            tv_action.text = it?.data?.rows?.firstOrNull()?.label
                        } else {
                            tv_action.setText(R.string.xem_chi_tiet)
                        }
                        tv_action.setOnClickListener { _ ->
                            lifecycleScope.launch {
                                tv_action.isEnabled = false
                                FirebaseDynamicLinksActivity.startTargetPath(requireActivity(), it?.data?.rows?.firstOrNull()?.redirectPath)
                                delay(400)
                                tv_action.isEnabled = true
                            }

                        }
                        imageView14.loadImageWithHolder(it?.data?.rows?.firstOrNull()?.icon, R.drawable.ic_reminder_item)
                    }
                } else {
                    tv_count.beGone()
                    layoutContainer.setTransition(R.id.no_reminder)
                }
            })
        }
    }

    override fun onDestroy() {
        try {
            viewModel.disposeApi()
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
        INSTANCE = null
       viewModel.onUpdateAds.value=null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestPVCombank) {
            viewModel.getPVCombank()
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestBannerSurvey -> {
                    val surveyID = data?.getLongExtra(Constant.DATA_1, -1)

                    if (surveyID != null && surveyID != -1L) {
                        homeAdapter.hideSurvey(surveyID)
                    }
                }
                requestProductNeedReview -> {
                    val idReview = data?.getLongExtra(Constant.DATA_1, -1)

                    if (idReview != null && idReview != -1L) {
                        homeAdapter.removeReviewProduct(idReview)
                    }
                }
            }
        }
    }
}