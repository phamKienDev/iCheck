package vn.icheck.android.screen.user.home_page.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.View
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
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.helper.FileHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.product_need_review.ICProductNeedReview
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.campaign.calback.IBannerV2Listener
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.campaign.calback.IProductNeedReviewListener
import vn.icheck.android.screen.user.edit_review.EditReviewActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.home_page.home.adapter.HomePageAdapter
import vn.icheck.android.screen.user.home_page.home.reminders.ReminderHomeDialog
import vn.icheck.android.screen.user.list_trending_products.ListTrendingProductsActivity
import vn.icheck.android.screen.user.listnotification.ListNotificationActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.search_home.main.SearchHomeActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.util.AdsUtils
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

/**
 * Created by VuLCL on 9/19/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
@AndroidEntryPoint
class HomePageFragment : BaseFragmentMVVM(), IBannerV2Listener, IMessageListener, IProductNeedReviewListener, View.OnClickListener {
    private var homeAdapter = HomePageAdapter(this, this, this)

    private val viewModel: HomePageViewModel by activityViewModels()

    private val requestBannerSurvey = 1
    private val requestLoginNotification = 2
    private val requestLoginCart = 3
    private val requestProductNeedReview = 4
    private val requestOpenCart = 5

    private var isViewCreated = false

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

    override val getLayoutID: Int
        get() = R.layout.fragment_home

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        checkTheme()
    }

    private fun setupView() {
        layoutHeader.setPadding(0, getStatusBarHeight + SizeHelper.size16, 0, 0)

//        txtSearch.background = ViewHelper.createDrawableStateList(
//                ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.white_opacity_unknow), SizeHelper.size4.toFloat()),
//                ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.darkGray6), SizeHelper.size4.toFloat())
//        )

        tv_show_all_reminders.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity is HomeActivity) {
                    ReminderHomeDialog().apply {
                        show(activity.supportFragmentManager, null)
                    }
                }
            }
        }

        group_notification.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity is HomeActivity) {
                    ReminderHomeDialog().apply {
                        show(activity.supportFragmentManager, null)
                    }
                }
            }
        }

        getReminders()
    }

    private fun checkTheme() {
        homeAdapter.notifyDataSetChanged()

        val backgroundImage = BitmapFactory.decodeFile(FileHelper.getPath(this@HomePageFragment.requireContext()) + FileHelper.homeBackgroundImage)
        imgThemeBackground?.apply {
            if (backgroundImage != null) {
                setImageBitmap(backgroundImage)
            } else {
                setImageResource(0)
            }
            requestLayout()
        }

        val theme = SettingManager.themeSetting?.theme
        if (theme != null) {
            txtSearch.background = ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.white_opacity_unknow), SizeHelper.size4.toFloat())
            txtSearch.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.getDrawableFillColor(R.drawable.ic_icheck_70dp_17dp, theme.homeHeaderIconColor!!), null, null, null)
        } else {
            txtSearch.background = ViewHelper.createDrawableStateList(
                    ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.white_opacity_unknow), SizeHelper.size4.toFloat()),
                    ViewHelper.createShapeDrawable(ContextCompat.getColor(requireContext(), R.color.darkGray6), SizeHelper.size4.toFloat())
            )
            txtSearch.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_icheck_70dp_17dp), null, null, null)
        }

        if (!theme?.homeHeaderIconColor.isNullOrEmpty()) {
            txtAvatar.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_menu_white_24dp),
                    ViewHelper.getDrawableFillColor(R.drawable.ic_home_menu_white_24dp, theme!!.homeHeaderIconColor!!)
            ), null, null, null)

            tvViewCart.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_shop_white_24dp),
                    ViewHelper.getDrawableFillColor(R.drawable.ic_home_shop_white_24dp, theme.homeHeaderIconColor!!)
            ), null, null, null)

            txtNotification.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_notification_white_24),
                    ViewHelper.getDrawableFillColor(R.drawable.ic_notification_white_24, theme.homeHeaderIconColor!!)
            ), null, null, null)
        } else {
            txtAvatar.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_menu_white_24dp), null, null, null)
            tvViewCart.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_shop_white_24dp), null, null, null)
            txtNotification.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_notification_white_24), null, null, null)
        }
    }

    private fun setupViewModel() {
        viewModel.onShowPopup.observe(viewLifecycleOwner, Observer {
            AdsUtils.showAdsPopup(activity, it)
        })

        viewModel.onUpdateAds.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                closeLoading()
                homeAdapter.updateAds()
            }
        })

        viewModel.onError.observe(viewLifecycleOwner, Observer {
            closeLoading()
            it.message?.let { it1 ->
                homeAdapter.setError(it.icon, it1)
            }
        })

        viewModel.onAddData.observe(viewLifecycleOwner, Observer {
            homeAdapter.addItem(it)
        })

        viewModel.onUpdateData.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                closeLoading()
            }
            homeAdapter.updateItem(it)
            layoutHeader.beVisible()
        })

        viewModel.onUpdateListData.observe(viewLifecycleOwner, Observer {
            homeAdapter.updateItem(it)
            layoutHeader.beVisible()
        })
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
                    if (layoutContainer.currentState != layoutContainer.endState && viewModel.getRemindersCount() ?: 0 > 0) {
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
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.lightBlue), ContextCompat.getColor(requireContext(), R.color.lightBlue), ContextCompat.getColor(requireContext(), R.color.lightBlue))

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = true
//            setToolbarBackground(0f)
            homeAdapter.removeAllView()
            viewModel.getHomeLayout()
        }

        swipeLayout.post {
            viewModel.getHomeLayout()
            viewModel.getHomePopup()
            // Gọi api pvcombank
        }
    }

    private fun closeLoading() {
        swipeLayout.isRefreshing = false

        if (layoutLoading != null) {
            layoutContainer.removeView(layoutLoading)
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
                homeAdapter.notifyItemChanged(0)
            }
//            ICMessageEvent.Type.UPDATE_COUNT_CART -> {
//                val count = event.data as String?
//                tvCartCount.visibleOrInvisible(count != null)
//                tvCartCount.text = count
//            }
            ICMessageEvent.Type.ON_LOG_IN -> {
                getReminders()
                lifecycleScope.launch {
                    val file = File(FileHelper.getPath(requireContext()) + FileHelper.imageFolder)
                    if (file.exists()) {
                        FileHelper.deleteTheme(file)
                    }
                    homeAdapter.notifyDataSetChanged()
                    checkTheme()
                }
            }
            ICMessageEvent.Type.ON_LOG_OUT -> {
                lifecycleScope.launch {
                    val file = File(FileHelper.getPath(requireContext()) + FileHelper.imageFolder)
                    if (file.exists()) {
                        FileHelper.deleteTheme(file)
                    }
                    homeAdapter.notifyDataSetChanged()
                    checkTheme()
                }
                getCoin()
                layoutContainer.setTransition(R.id.no_reminder)
                tvCartCount.beGone()
            }
            ICMessageEvent.Type.ON_UPDATE_AUTO_PLAY_VIDEO -> {
                if (isVisible) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView, layoutToolbarAlpha.height)
                }
            }
            ICMessageEvent.Type.UPDATE_REMINDER -> {
                getReminders()
            }
            ICMessageEvent.Type.DISMISS_REMINDER -> {
                if (layoutContainer.currentState != layoutContainer.endState && viewModel.getRemindersCount() ?: 0 > 0) {
                    layoutContainer.transitionToEnd()
                }
            }
            ICMessageEvent.Type.ON_SET_THEME -> {
                checkTheme()
            }
            ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                if (isVisible && !SessionManager.isUserLogged) {
                    onRequireLogin()
                }
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

    override fun onResume() {
        super.onResume()

        if (!isViewCreated) {
            isViewCreated = true
            Handler().post {
                setupViewModel()
                setupRecyclerView()
                setupSwipeLayout()
                LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("home"))
                WidgetUtils.setClickListener(this, txtAvatar, txtNotification, tvViewCart, txtSearch)
            }
            return
        }

        viewModel.getAds(true)
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
//        if (!PreferenceManager.getDefaultSharedPreferences(ICheckApplication.getInstance()).getBoolean(FIREBASE_REGISTER_DEVICE, false)) {
//            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener<InstanceIdResult?> { task ->
//                if (!task.isSuccessful()) {
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                val token = task.result?.token ?: ""
//
//                // Log and toast
//                Log.e("token", token.toString())
//
//                viewModel.registerDevice(token)
//            })
//        }

        homeAdapter.notifyItemChanged(0)
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

                if (!it?.data?.rows.isNullOrEmpty() && isVisible) {
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
                        tv_show_all_reminders.text = "Xem tất cả lời nhắc (${viewModel.getRemindersCount()})"
                        tv_reminder_content.text = it?.data?.rows?.firstOrNull()?.message
                        if (!it?.data?.rows?.firstOrNull()?.label.isNullOrEmpty()) {
                            tv_action.text = it?.data?.rows?.firstOrNull()?.label
                        } else {
                            tv_action.setText(R.string.xem_chi_tiet)
                        }
                        tv_action.setOnClickListener { _ ->
                            FirebaseDynamicLinksActivity.startTargetPath(requireActivity(), it?.data?.rows?.firstOrNull()?.redirectPath)
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
//                9 -> {
//                    data?.getSerializableExtra(Constant.DATA_1)?.let { obj ->
//                        obj.toString()
//                    }
//                }
//                requestOpenCart -> {
//                    data?.getLongExtra("id", 0L)?.let {
//                        SuccessConfirmShipDialog(it).apply {
//                            isCancelable = false
//                        }.show(childFragmentManager, null)
//                    }
//                }
            }
        }
    }
}