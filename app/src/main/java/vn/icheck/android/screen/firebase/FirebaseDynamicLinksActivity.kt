package vn.icheck.android.screen.firebase

//import vn.teko.android.auth.core.TerraAuth
//import vn.teko.android.auth.login.TerraLogin
//import vn.teko.android.auth.login.provider.AUTH_MANAGER_EXTRA_CUSTOM_TOKEN_IDTOKEN
//import vn.teko.android.auth.login.provider.AUTH_MANAGER_EXTRA_CUSTOM_TOKEN_PROVIDER
//import vn.teko.android.auth.login.provider.AUTH_MANAGER_RC_LOGIN
//import vn.teko.android.auth.login.provider.LoginType
//import vn.teko.hestia.android.TerraHestia
//import vn.teko.hestia.android.utils.uiHelper.DefaultAndroidHestiaUIHelper
//import vn.teko.terra.core.android.terra.TerraApp
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.tripi.flight.config.Flight
import com.tripi.flight.config.FlightConfig
import com.tripi.hotel.config.HotelConfig
import com.tripi.hotel.config.HotelSDK
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialog
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.ICK_REQUEST_CAMERA
import vn.icheck.android.helper.*
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.screen.gift_voucher.GiftDetailFromAppActivity
import vn.icheck.android.loyalty.screen.url_gift_detail.UrlGiftDetailActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICLink
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.scan.MyQrActivity
import vn.icheck.android.screen.scan.V6ScanditActivity
import vn.icheck.android.screen.user.buy_mobile_card.BuyMobileCardV2Activity
import vn.icheck.android.screen.user.buy_mobile_card_success.BuyCardSuccessActivity
import vn.icheck.android.screen.user.coinhistory.CoinHistoryActivity
import vn.icheck.android.screen.user.createqrcode.home.CreateQrCodeHomeActivity
import vn.icheck.android.screen.user.detail_my_reward.DetailMyRewardActivity
import vn.icheck.android.screen.user.detail_post.DetailPostActivity
import vn.icheck.android.screen.user.detail_stamp_v5.home.DetailStampV5Activity
import vn.icheck.android.screen.user.detail_stamp_v6.home.DetailStampV6Activity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.list_campaign.ListCampaignActivity
import vn.icheck.android.screen.user.my_gift_warehouse.list_mission.list.ListMissionActivity
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.screen.user.icheckstore.list.ProductStoreiCheckActivity
import vn.icheck.android.screen.user.list_product_question.ListProductQuestionActivity
import vn.icheck.android.screen.user.listnotification.ListNotificationActivity
import vn.icheck.android.screen.user.listproduct.ListProductActivity
import vn.icheck.android.screen.user.missiondetail.MissionDetailActivity
import vn.icheck.android.screen.user.mygift.MyGiftActivity
import vn.icheck.android.screen.user.newsdetailv2.NewDetailV2Activity
import vn.icheck.android.screen.user.newslistv2.NewsListV2Activity
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.payment_topup_success.BuyTopupSuccessActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.pvcombank.authen.CreatePVCardActivity
import vn.icheck.android.screen.user.pvcombank.authen.CreatePVCardViewModel
import vn.icheck.android.screen.user.pvcombank.home.HomePVCardActivity
import vn.icheck.android.screen.user.rank_of_user.RankOfUserActivity
import vn.icheck.android.screen.user.recharge_phone.RechargePhoneActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.screen.user.social_chat.SocialChatActivity
import vn.icheck.android.screen.user.surveydetail.answer.SurveyDetailActivity
import vn.icheck.android.screen.user.utilities.UtilitiesActivity
import vn.icheck.android.screen.user.vinmart.VinMartActivity
import vn.icheck.android.screen.user.voucher.VoucherActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.util.ick.logDebug
import vn.icheck.android.util.ick.simpleStartForResultActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import java.net.URL
import java.util.*

class FirebaseDynamicLinksActivity : AppCompatActivity() {
    private val requestLogin = 1
    private val requestFlight = 2
    private val requestHotel = 3

    private val home = "home"
    private val mall = "mall"
    private val history = "history"
    private val scan = "scan"
    private val scanAndBuy = "scan_and_buy"
    private val missions = "missions"
    private val campaign = "campaign"
    private val campaigns = "campaigns"
    private val listCampaign = "list_campaign"
    private val dailyMissions = "daily_missions"
    private val advanceMissions = "advance_missions"
    private val completedMission = "completed_mission"
    private val completedMissions = "completed_missions"
    private val rewardsStore = "rewards_store"
    private val myRewards = "my_rewards"
    private val myGiftBoxes = "my_gift_boxes"
    private val rank = "rank"
    private val pointTransitions = "point_transitions"
    private val cart = "cart"
    private val updateAccount = "update_account"
    private val breakReward = "break_reward"
    private val topup = "topup"
    private val buyCard = "buy_card"
    private val createQrCode = "create_qrcode"
    private val myCard = "my_card"
    private val vouchers = "vouchers"
    private val login = "login"
    private val levelUp = "level_up"
    private val guide = "guide"
    private val policy = "policy"
    private val term = "term"
    private val post = "post"

    private val product = "product"
    private val icheckProduct = "icheck_product"
    private val barcode = "barcode"
    private val evs6 = "evs6"
    private val evs61 = "evs61"
    private val page = "page"
    private val collection = "collection"
    private val survey = "survey"
    private val inbox = "inbox"
    private val inboxUser = "inbox_user"
    private val inboxPage = "inbox_page"
    private val user = "user"
    private val review = "review"
    private val news = "news"
    private val list_news = "list_news"
    private val productQuestion = "product_question"
    private val store = "store"

    private val shopProduct = "shop_product"
    private val link = "link"
    private val url = "url"
    private val web = "web"
    private val order = "order"
    private val mision = "mission"
    private val reward = "reward"
    private val myReward = "my_reward"
    private val completedMiniMission = "completed_mini_mission"
    private val orders = "orders"
    private val completedOrders = "completed_orders"
    private val rechargeCard = "recharge_card"
    private val loyaltyCampaign = "loyalty_campaign"
    private val vinmart = "vinmart"
    private val digitalBank = "digital_bank"
    private val flightBooking = "flight_booking"
    private val hotelBooking = "hotel_booking"
    private val utilities = "utilities"
    private val rechargeCardSuccess = "recharge_card_success"
    private val buyCardSuccess = "buy_card_success"

    /**
     * Loyalty
     */
    private val games = "games"
    private val luckyWheel = "luckywheel"
    private val accumulatePoint = "point_campaign"
    private val memberPoint = "member_point"
    private val memberPointCampaigns = "member_point_campaigns"
    private val memberPointStoreGifts = "member_point_store_gifts"
    private val memberPointExchangedGifts = "member_point_exchanged_gifts"
    private val memberPointCampaign = "member_point_campaign"
    private val memberPointStoreGift = "member_point_store_gift"
    private val memberPointExchangedGift = "member_point_exchanged_gift"
    private val loyaltyReward = "loyalty_reward"
    private val notification = "notification"

    private var deepLink: Uri? = null
    private var targetType: String = ""

    companion object {
        @MainThread
        fun startDestinationUrl(fragmentActivity: Activity?, uri: String?) {
            if (fragmentActivity != null && !uri.isNullOrEmpty()) {
                val intent = Intent(fragmentActivity, FirebaseDynamicLinksActivity::class.java)

                if (uri.startsWith("icheck://") || uri.startsWith("http://") || uri.startsWith("https://")) {
                    intent.data = Uri.parse(uri)
                } else {
                    intent.data = Uri.parse("icheck://$uri")
                }

                fragmentActivity.startActivity(intent)
                fragmentActivity.overridePendingTransition(R.anim.none, R.anim.none)
            }
        }

        fun startTarget(fragmentActivity: FragmentActivity, targetType: String?) {
            if (!targetType.isNullOrEmpty()) {
                val intent = Intent(fragmentActivity, FirebaseDynamicLinksActivity::class.java)
                intent.data = Uri.parse("icheck://$targetType")
                fragmentActivity.startActivity(intent)
                fragmentActivity.overridePendingTransition(R.anim.none, R.anim.none)
            }
        }

        fun startTargetPath(fragmentActivity: Context, targetType: String?) {
            if (!targetType.isNullOrEmpty()) {
                val intent = Intent(fragmentActivity, FirebaseDynamicLinksActivity::class.java)
                intent.data = Uri.parse(targetType)
                fragmentActivity.startActivity(intent)
            }
        }

        fun startTarget(context: Activity, targetType: String?) {
            if (!targetType.isNullOrEmpty()) {
                val intent = Intent(context, FirebaseDynamicLinksActivity::class.java)
                intent.data = Uri.parse("icheck://$targetType")
                context.startActivity(intent)
            }
        }

        fun startTarget(fragmentActivity: FragmentActivity, targetType: String?, targetID: String?) {
            if (!targetType.isNullOrEmpty()) {
                if (!targetID.isNullOrEmpty()) {
                    val intent = Intent(fragmentActivity, FirebaseDynamicLinksActivity::class.java)
                    intent.data = Uri.parse("icheck://$targetType?${Constant.ID}=$targetID")
                    fragmentActivity.startActivity(intent)
                    fragmentActivity.overridePendingTransition(R.anim.none, R.anim.none)
                } else {
                    startTarget(fragmentActivity, targetType)
                }
            }
        }

        fun startTarget(context: Activity, targetType: String?, targetID: String?) {
            if (!targetType.isNullOrEmpty()) {
                if (!targetID.isNullOrEmpty()) {
                    val intent = Intent(context, FirebaseDynamicLinksActivity::class.java)
                    intent.data = Uri.parse("icheck://$targetType?${Constant.ID}=$targetID")
                    context.startActivity(intent)
                } else {
                    startTarget(context, targetType)
                }
            }
        }

        fun startTarget(context: Activity, targetType: String?, targetID: Long?) {
            if (!targetType.isNullOrEmpty()) {
                if (targetID != null) {
                    val intent = Intent(context, FirebaseDynamicLinksActivity::class.java)
                    intent.data = Uri.parse("icheck://$targetType?${Constant.ID}=$targetID")
                    context.startActivity(intent)
                } else {
                    startTarget(context, targetType)
                }
            }
        }

        fun startTargetWidthBarcode(fragmentActivity: FragmentActivity, targetType: String?, barcode: String?) {
            if (!targetType.isNullOrEmpty()) {
                if (!barcode.isNullOrEmpty()) {
                    val intent = Intent(fragmentActivity, FirebaseDynamicLinksActivity::class.java)
                    intent.data = Uri.parse("icheck://$targetType?${Constant.BARCODE}=$barcode")
                    fragmentActivity.startActivity(intent)
                    fragmentActivity.overridePendingTransition(R.anim.none, R.anim.none)
                } else {
                    startTarget(fragmentActivity, targetType)
                }
            }
        }

        fun startTargetWidthCode(fragmentActivity: FragmentActivity, targetType: String?, code: String?) {
            if (!targetType.isNullOrEmpty()) {
                if (!code.isNullOrEmpty()) {
                    val intent = Intent(fragmentActivity, FirebaseDynamicLinksActivity::class.java)
                    intent.data = Uri.parse("icheck://$targetType?${Constant.CODE}=$code")
                    fragmentActivity.startActivity(intent)
                    fragmentActivity.overridePendingTransition(R.anim.none, R.anim.none)
                } else {
                    startTarget(fragmentActivity, targetType)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deepLink = intent.data

        targetType = try {
            if (deepLink?.scheme == "icheck") {
                deepLink?.host ?: ""
            } else {
                URL(deepLink.toString()).path.removePrefix("/")
            }
        } catch (e: Exception) {
            ""
        }

        if (targetType.isNotEmpty()) {
            checkTarget()
        } else {
            checkLink()
            finishActivity()
        }
    }

    private fun checkLink() {
        if (deepLink?.toString()?.startsWith("http") == true) {
            when {
                deepLink.toString().contains("icheck.page.link") -> {
                    openInChrome()
                }
                deepLink.toString().contains("icheckdev.com.vn") -> {
                    openInChrome()
                }
                deepLink.toString().contains("icheckdev.com.vn") -> {
                    openInChrome()
                }
                deepLink.toString().contains("vn.icheck.android") -> {
                    openInChrome()
                }
                deepLink.toString().contains("icheckdev.page.link") -> {
                    openInChrome()
                }
                deepLink.toString().contains("icheckdev.page.link") -> {
                    openInChrome()
                }
                else -> {
                    ActivityUtils.startActivity<WebViewActivity, String>(this, Constant.DATA_1, deepLink.toString())
                }
            }
        }
    }

    private fun openInChrome() {
        val intent = Intent(Intent.ACTION_VIEW, deepLink)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            ActivityHelper.startActivity(this, intent)
        } catch (ex: ActivityNotFoundException) {
        }
    }

    private fun checkTarget() {
        when (targetType.toLowerCase(Locale.ROOT)) {
            home -> {
                if (HomeActivity.isOpen == true) {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
                } else {
                    ActivityUtils.startActivity<HomeActivity, Int>(this, Constant.DATA_1, 1)
                }
            }
            mall -> {
                if (HomeActivity.isOpen == true) {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 2))
                } else {
                    ActivityUtils.startActivity<HomeActivity, Int>(this, Constant.DATA_1, 2)
                }
            }
            history -> {
                if (HomeActivity.isOpen == true) {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 3))
                } else {
                    ActivityUtils.startActivity<HomeActivity, Int>(this, Constant.DATA_1, 3)
                }
            }
            scan -> {
                if (PermissionHelper.checkPermission(this@FirebaseDynamicLinksActivity, Manifest.permission.CAMERA, ICK_REQUEST_CAMERA)) {
                    V6ScanditActivity.create(this)
                } else {
                    return
                }
            }
            scanAndBuy -> {
                if (PermissionHelper.checkPermission(this@FirebaseDynamicLinksActivity, Manifest.permission.CAMERA, ICK_REQUEST_CAMERA)) {
                    V6ScanditActivity.create(this, 2)
                } else {
                    return
                }
            }
            login -> {
                if (!SessionManager.isUserLogged) {
                    ActivityUtils.startActivity<IckLoginActivity>(this)
                }
            }
            missions -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("campaignId")
                    if (!id.isNullOrEmpty()) {
                        ListMissionActivity.show(this, id)
                    }
                }
            }
            listCampaign -> {
                ActivityUtils.startActivity<ListCampaignActivity>(this)
            }
            campaign -> {

                val id = deepLink?.getQueryParameter("id")
                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<ListShakeGridBoxActivity, String>(this, Constant.DATA_1, id)
                } else {
                    ActivityUtils.startActivity<ListCampaignActivity>(this)
                }
            }
            campaigns -> {
                ActivityUtils.startActivity<ListCampaignActivity>(this)
            }
            dailyMissions -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("campaignId")
                    if (!id.isNullOrEmpty()) {
                        ListMissionActivity.show(this, id)
                    }
                }
            }
            advanceMissions -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("campaignId")
                    if (!id.isNullOrEmpty()) {
                        ListMissionActivity.show(this, id)
                    }
                }
            }
            completedMission -> {
                logDebug("completedMission")

                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("id")
                    if (!id.isNullOrEmpty()) {
                        ActivityUtils.startActivity<MissionDetailActivity, String>(this, Constant.DATA_1, id)
                    }
                }
            }
            completedMissions -> {
                logDebug("completedMissions")
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("campaignId")
                    if (!id.isNullOrEmpty()) {
                        ListMissionActivity.show(this, id)
                    }
                }
            }
            completedMiniMission -> {
                logDebug("completedMiniMission")

                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("campaignId")
                    if (!id.isNullOrEmpty()) {
                        ListMissionActivity.show(this, id)
                    }
                }
            }
            rewardsStore -> {
                ActivityUtils.startActivity<ProductStoreiCheckActivity>(this)
            }
            myRewards -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    ActivityUtils.startActivity<MyGiftActivity>(this)
                }
            }
            myGiftBoxes -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    ActivityUtils.startActivity<MyGiftActivity>(this)
                }
            }
            rank -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    ActivityUtils.startActivity<RankOfUserActivity>(this)
                }
            }
            pointTransitions -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    ActivityUtils.startActivity<CoinHistoryActivity>(this)
                }
            }
            cart -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    ActivityUtils.startActivity<ShipActivity, Boolean>(this, Constant.CART, true)
                }
            }
            updateAccount -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    IckUserWallActivity.openInfor(SessionManager.session.user?.id, this)
                }
            }
            orders -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    ActivityUtils.startActivity<OrderHistoryActivity>(this)
                }
            }
            completedOrders -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    ActivityUtils.startActivity<OrderHistoryActivity, Int>(this, Constant.DATA_1, OrderHistoryActivity.delivered)
                }
            }
            levelUp -> {
                if (NetworkHelper.isConnected(this)) {
                    SettingHelper.getSystemSetting("ranking-support.direction", "ranking-support", object : ISettingListener {
                        override fun onRequestError(error: String) {
                            logDebug(error)
                        }

                        override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                            WebViewActivity.start(this@FirebaseDynamicLinksActivity, list?.firstOrNull()?.value, null, "Cách tích điểm")
                        }
                    })
                }
            }
            guide -> {
                if (NetworkHelper.isConnected(this)) {
                    SettingHelper.getSystemSetting("app-support.support-url", "app-support", object : ISettingListener {
                        override fun onRequestError(error: String) {
                            logDebug(error)
                        }

                        override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                            WebViewActivity.start(this@FirebaseDynamicLinksActivity, list?.firstOrNull()?.value, null, "Hướng dẫn sử dụng")
                        }
                    })
                }
            }
            policy -> {
                if (NetworkHelper.isConnected(this)) {
                    SettingHelper.getSystemSetting("app-support.privacy-url", "app-support", object : ISettingListener {
                        override fun onRequestError(error: String) {
                            logDebug(error)
                        }

                        override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                            WebViewActivity.start(this@FirebaseDynamicLinksActivity, list?.firstOrNull()?.value, null, "Điều khoản sử dụng")
                        }
                    })
                }
            }
            term -> {
                if (NetworkHelper.isConnected(this)) {
                    SettingHelper.getSystemSetting("app-support.privacy-url", "app-support", object : ISettingListener {
                        override fun onRequestError(error: String) {
                            logDebug(error)
                        }

                        override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                            WebViewActivity.start(this@FirebaseDynamicLinksActivity, list?.firstOrNull()?.value, null, "Điều khoản sử dụng")
                        }
                    })
                }
            }

            rechargeCard -> {
                if (SessionManager.isLoggedAnyType) {
                    ActivityUtils.startActivity<BuyMobileCardV2Activity>(this)
                }
            }
            breakReward -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("campaignId")
                    if (!id.isNullOrEmpty()) {
                        getListCampaign(id)
                    }
                }
            }
            topup -> {
                if (SessionManager.isLoggedAnyType) {
                    ActivityUtils.startActivity<RechargePhoneActivity>(this)
                }
            }
            buyCard -> {
                if (SessionManager.isLoggedAnyType) {
                    ActivityUtils.startActivity<BuyMobileCardV2Activity>(this)
                }
            }
            myCard -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    if (PermissionHelper.checkPermission(this@FirebaseDynamicLinksActivity, Manifest.permission.CAMERA, ICK_REQUEST_CAMERA)) {
//                        V6ScanditActivity.create(this, 3)
                        MyQrActivity.createOnly(this)
                    } else {
                        return
                    }
                }
            }
            vouchers -> {
                ActivityUtils.startActivity<VoucherActivity>(this)
            }
            evs6 -> {
                val v5 = deepLink?.getQueryParameter("v5")

                if (!v5.isNullOrEmpty()) {
                    val html = "https://cg.icheck.com.vn/${v5}"
                    ActivityUtils.startActivity<DetailStampV5Activity, String>(this, "data", html)
                } else {
                    val qri = deepLink?.getQueryParameter("qri")

                    if (!qri.isNullOrEmpty()) {
                        val html = "https://ktra.vn/1.1.${qri}"
                        ActivityUtils.startActivity<DetailStampV6Activity, String>(this, "data", html)
                    } else {
                        val qrm = deepLink?.getQueryParameter("qrm")

                        if (!qrm.isNullOrEmpty()) {
                            val html = "https://ktra.vn/1.0.${qrm}"
                            ActivityUtils.startActivity<DetailStampV6Activity, String>(this, "data", html)
                        }
                    }
                }
            }
            evs61 -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<DetailStampActivity, String>(this, Constant.DATA_1, id)
                } else {
                    val targetCode = deepLink?.getQueryParameter("code")

                    if (!targetCode.isNullOrEmpty()) {
                        ActivityUtils.startActivity<DetailStampActivity, String>(this, Constant.DATA_1, targetCode)
                    }
                }
            }
            news -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<NewDetailV2Activity, Long>(this, Constant.DATA_1, id.toLong())
                } else {
                    ActivityUtils.startActivity<NewsListV2Activity>(this)
                }
            }
            list_news -> {
                ActivityUtils.startActivity<NewsListV2Activity>(this)
            }
            product -> {
                val targetID = deepLink?.getQueryParameter("id")

                if (!targetID.isNullOrEmpty()) {
                    IckProductDetailActivity.start(this, targetID.toLong())
                } else {
                    val targetBarcode = deepLink?.getQueryParameter("barcode")

                    if (!targetBarcode.isNullOrEmpty()) {
                        val isScan = deepLink?.getQueryParameter("isScan") ?: "0"
                        IckProductDetailActivity.start(this, targetBarcode, isScan.toInt() == 1)
                    }
                }
            }
            icheckProduct -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    IckProductDetailActivity.start(this, id.toLong())
                } else {
                    val targetBarcode = deepLink?.getQueryParameter("barcode")

                    if (!targetBarcode.isNullOrEmpty()) {
                        val isScan = deepLink?.getQueryParameter("isScan") ?: "0"
                        IckProductDetailActivity.start(this, targetBarcode, isScan.toInt() == 1)
                    }
                }
            }
            barcode -> {
                val barcode = deepLink?.getQueryParameter("id")

                if (!barcode.isNullOrEmpty()) {
                    IckProductDetailActivity.start(this, barcode)
                }
            }
//            review -> {
//                val id = deepLink?.getQueryParameter("id")
//                ActivityUtils.startActivity<ListProductReviewActivity, Long>(this, Constant.DATA_1, id?.toLong()!!)
//            }
            productQuestion -> {
                val id = deepLink?.getQueryParameter("id")
//                val type = deepLink?.getQueryParameter("type")

                if (!id.isNullOrEmpty()) {
                    ListProductQuestionActivity.start(this, id.toLong(), null, null, null)
                } else {
                    val object_id = deepLink?.getQueryParameter("object_id")
//                    val object_type = deepLink?.getQueryParameter("object_type")

                    if (!object_id.isNullOrEmpty()) {
                        ListProductQuestionActivity.start(this, object_id.toLong(), null, null, null)
                    }
                }
            }
            shopProduct -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<PageDetailActivity, Long>(this, Constant.DATA_1, id.toLong())
                } else {
                    val variantId = deepLink?.getQueryParameter("variantId")
                    val productId = deepLink?.getQueryParameter("productId")

                    if (!variantId.isNullOrEmpty() && !productId.isNullOrEmpty()) {
                        val intent = Intent(this, PageDetailActivity::class.java)
                        intent.putExtra(Constant.DATA_2, variantId.toLong())
                        intent.putExtra(Constant.DATA_3, productId.toLong())
                        ActivityUtils.startActivity(this, intent)
                    }
                }
            }
            page -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<PageDetailActivity, Long>(this, Constant.DATA_1, id.toLong())
                }
            }
            collection -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    val url = APIConstants.defaultHost + APIConstants.Product.LIST
                    val params = hashMapOf<String, Any>()
                    params.put("collection_id", id)

                    val name = deepLink?.getQueryParameter("name")

                    TrackingAllHelper.trackCategoryViewed(name, InsiderHelper.FIREBASE)
                    ListProductActivity.start(this, url, params, deepLink?.getQueryParameter("name"))
                }
            }
            survey -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<SurveyDetailActivity, Long>(this, Constant.DATA_1, id.toLong())
                }
            }
            inbox -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    if (!SessionManager.isUserLogged) {
                        showLoginDialog()
                        return
                    } else {
//                        ChatSocialDetailActivity.openRoomChatWithKey(this@FirebaseDynamicLinksActivity, id)
                        SocialChatActivity.createRoomChat(this@FirebaseDynamicLinksActivity, null, id)
                    }
                }
            }
            inboxUser -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    if (!SessionManager.isUserLogged) {
                        showLoginDialog()
                        return
                    } else if (ValidHelper.validNumber(id)) {
//                        ChatSocialDetailActivity.createRoomChat(this@FirebaseDynamicLinksActivity, id.toLong(), "user")
                        SocialChatActivity.createRoomChat(this@FirebaseDynamicLinksActivity, id.toLong())
                    }
                }
            }
            inboxPage -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    if (!SessionManager.isUserLogged) {
                        showLoginDialog()
                        return
                    } else if (ValidHelper.validNumber(id)) {
//                        ChatSocialDetailActivity.createRoomChat(this@FirebaseDynamicLinksActivity, id.toLong(), "page")
                        SocialChatActivity.createRoomChat(this@FirebaseDynamicLinksActivity, null, id)
                    }
                }
            }
            user -> {
                val id = deepLink?.getQueryParameter("id")
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    if (!id.isNullOrEmpty()) {
                        IckUserWallActivity.create(id.toLong(), this)
                    } else {
                        IckUserWallActivity.create(SessionManager.session.user?.id, this)
                    }
                }

            }
            link -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<WebViewActivity, String>(this, Constant.DATA_1, id)
                }
            }
            url -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<WebViewActivity, String>(this, Constant.DATA_1, id)
                }
            }
            web -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<WebViewActivity, String>(this, Constant.DATA_1, id)
                }
            }
            order -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ShipActivity.startDetailOrder(this, id.toLong())
                }
            }
            mision -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    ActivityUtils.startActivity<MissionDetailActivity, String>(this, Constant.DATA_1, id)
                }
            }
            reward -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    if (!SessionManager.isUserLogged) {
                        showLoginDialog()
                        return
                    } else {
                        ActivityUtils.startActivity<DetailMyRewardActivity, Long>(this, Constant.DATA_1, id.toLong())
                    }
                }
            }
            myReward -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
                    if (!SessionManager.isUserLogged) {
                        showLoginDialog()
                        return
                    } else {
                        ActivityUtils.startActivity<MyGiftActivity>(this)
                    }
                }
            }
            store -> {
                val id = deepLink?.getQueryParameter("id")

                if (!id.isNullOrEmpty()) {
//                    ActivityUtils.startActivity<ShopDetailActivity, Long>(this, Constant.DATA_1, id.toLong())
                }
            }
            createQrCode -> {
                ActivityUtils.startActivity<CreateQrCodeHomeActivity>(this)
            }
            loyaltyCampaign -> {
                val id = deepLink?.getQueryParameter("id")
                val target = deepLink?.getQueryParameter("target")

                val intent = Intent(this, UrlGiftDetailActivity::class.java)
                if (id != null) {
                    intent.putExtra("id", id.toLong())
                }
                if (!target.isNullOrEmpty()) {
                    intent.putExtra("target", target)
                }

                ActivityUtils.startActivity(this, intent)
            }
            vinmart -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    UserInteractor().getVnShopLink(object : ICApiListener<ICLink> {
                        override fun onSuccess(obj: ICLink) {
                            if (!obj.link.isNullOrEmpty()) {
                                ActivityUtils.startActivity<VinMartActivity, String>(this@FirebaseDynamicLinksActivity, Constant.DATA_1, obj.link!!)
                            }
                            finishActivity()
                        }

                        override fun onError(error: ICBaseResponse?) {
                            finishActivity()
                        }
                    })
                    return
                }

                /*
                * VNShop
                * */
//                val terraApp = TerraApp.getInstance("iCheck")
//                val token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjBkNTdhMGJlLTNlZDItNDJhMi1iM2U1LTM2Y2E3ZjkzMjEyMjQifQ.eyJzdWIiOjExMjM2OCwibmFtZSI6IlR14bqlbiBOZ3V54buFbiIsImVtYWlsIjoidHVhbkBnbWFpbC5jb20iLCJwaG9uZV9udW1iZXIiOiIwOTg3NjYzOTY3IiwicGljdHVyZSI6Imh0dHBzOi8vYXNzZXRzLmljaGVjay52bi9tdWx0aXBhcnQvMjAyMC9hcHAwMS85LzhhN2IwY2Y4NjFhOGQ1YzgwYjQzZTUzMWFkNjljODM0LnBuZyIsImFkZHJlc3MiOiIzNjggQ-G6p3UgR2nhuqV5IiwiYmlydGhkYXkiOiIxMi8yLzIwMDAiLCJhdWQiOiJpY2hlY2siLCJpYXQiOjE2MTQyMzYxNDUsImV4cCI6MTYxNDQwODk0NSwiaXNzIjoibXlpc3N1ZXJuYW1lIn0.ea3bGw59AjG-0koNs6pCPdRUXoF-BOUTNUmFHiJ0dUWB30GnrDszK3mr1K83_SkAXiP5sVz7DwZejOh8S-JYNA"
//
//                TerraLogin.getInstance(terraApp).login(this@FirebaseDynamicLinksActivity, LoginType.CUSTOM_TOKEN)
//                val data = Intent().apply {
//                    putExtra(AUTH_MANAGER_EXTRA_CUSTOM_TOKEN_IDTOKEN, token)
//                    putExtra(AUTH_MANAGER_EXTRA_CUSTOM_TOKEN_PROVIDER, "icheck")
//                }
//
//                TerraLogin.getInstance(terraApp).processLoginResult(AUTH_MANAGER_RC_LOGIN, Activity.RESULT_OK, data) { result ->
//                    when (result) {
//                        //Login Success
//                        is vn.teko.android.core.util.Result.Success -> {
//                            this@FirebaseDynamicLinksActivity.apply {
//                                TerraHestia.getInstance(terraApp).startApp("vnshop", TerraAuth.getInstance(terraApp), DefaultAndroidHestiaUIHelper(this))
//                            }
//                        }
//                        //Login Failure
//                        is vn.teko.android.core.util.Result.Failure -> {
//                            this@FirebaseDynamicLinksActivity.apply {
//                                finishActivity()
//                            }
//                        }
//                    }
//                }
//                return
            }
            flightBooking -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val subID = SessionManager.session.user?.id?.toString()
                    val tranID = Base64.encodeToString("$subID-${System.currentTimeMillis() / 1000}".toByteArray(charset("UTF-8")), Base64.DEFAULT).trim()
                    Flight.create(this)
                            .language(FlightConfig.Language.VI)
                            .subId(subID)
                            .tranId(tranID)
                            .caId(14)
                            .appToken(APIConstants.tripiTokenProduct)
                            .start(requestFlight)
                    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
                }
            }
            hotelBooking -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val subID = SessionManager.session.user?.id?.toString()
                    val tranID = Base64.encodeToString("$subID-${System.currentTimeMillis() / 1000}".toByteArray(charset("UTF-8")), Base64.DEFAULT).trim()
                    HotelSDK.create(this)
                            .language(HotelConfig.Language.VI)
                            .subId(subID)
                            .transId(tranID)
                            .caId(14)
                            .appToken(APIConstants.tripiTokenProduct)
                            .start(requestHotel)
                    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
                }
            }
            digitalBank -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    CreatePVCardViewModel().apply {
                        checkHasCard(5000L).observe(this@FirebaseDynamicLinksActivity, Observer { checkCardRes ->
                            this@FirebaseDynamicLinksActivity.apply {
                                when (checkCardRes.status) {
                                    Status.LOADING -> {
                                        DialogHelper.showLoading(this)
                                    }
                                    Status.ERROR_NETWORK -> {
                                        DialogHelper.closeLoading(this)
                                        ToastHelper.showLongError(this, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                                        finishActivity()
                                    }
                                    Status.ERROR_REQUEST -> {
                                        DialogHelper.closeLoading(this)
                                        ToastHelper.showLongError(this, ICheckApplication.getError(checkCardRes.message))
                                        finishActivity()
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
                                                                ToastHelper.showLongError(this, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                                                                finishActivity()
                                                            } else {
                                                                CreatePVCardActivity.redirectUrl = formAuthRes.data!!.data!!.redirectUrl
                                                                WebViewActivity.start(this, formAuthRes.data!!.data!!.authUrl)
                                                                finishActivity()
                                                            }
                                                        }
                                                        else -> {
                                                            DialogHelper.closeLoading(this)
                                                            ToastHelper.showLongError(this, ICheckApplication.getError(checkCardRes.message))
                                                            finishActivity()
                                                        }
                                                    }
                                                })
                                            } else {
                                                DialogHelper.closeLoading(this)
                                                ActivityUtils.startActivityAndFinish<HomePVCardActivity>(this)
                                            }
                                        } else {
                                            DialogHelper.closeLoading(this)
                                            ActivityUtils.startActivityAndFinish<CreatePVCardActivity>(this)
                                        }
                                    }
                                }
                            }
                        })
                    }
                    return
                }
            }
            utilities -> {
                ActivityUtils.startActivity<UtilitiesActivity>(this)
            }
            games -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityGameFromLabelsList(this@FirebaseDynamicLinksActivity)
                }
            }
            luckyWheel -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                } else {
                    DialogHelper.showLoading(this@FirebaseDynamicLinksActivity)
                    LoyaltySdk.startActivityVQMM(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"), true)
                }
                return
            }
            accumulatePoint -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityOnBoarding(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"))
                }
            }
            memberPoint -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityHomePageEarnPoints(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"))
                }
            }
            memberPointCampaigns -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityCampaignOfBusiness(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"))
                }
            }
            memberPointStoreGifts -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityGiftShop(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"))
                }
            }
            memberPointExchangedGifts -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityRedemptionHistory(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"), 1)
                }
            }
            memberPointCampaign -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityLoyaltyVipDetail(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"))
                }
            }
            memberPointStoreGift -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityGiftDetail(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"), 1)
                }
            }
            memberPointExchangedGift -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    LoyaltySdk.startActivityGiftDetail(this@FirebaseDynamicLinksActivity, deepLink?.getQueryParameter("id"), 0)
                }
            }
            loyaltyReward -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("id")
                    if (!id.isNullOrEmpty()) {
                        ActivityHelper.startActivity<GiftDetailFromAppActivity, Long>(this@FirebaseDynamicLinksActivity, ConstantsLoyalty.DATA_1, id.toLong())
                    } else {
                        ActivityUtils.startActivity<MyGiftActivity>(this)
                    }
                }
            }
            post, review -> {
                if (!SessionManager.isUserLogged) {
                    showLoginDialog()
                    return
                } else {
                    val id = deepLink?.getQueryParameter("id")?.toLongOrNull()
                    if (id != null) {
                        DetailPostActivity.start(this, id)
                    }
                }
            }
            rechargeCardSuccess -> {
                val responseCode = deepLink?.getQueryParameter("vnp_ResponseCode")
                val orderInfo = deepLink?.getQueryParameter("vnp_OrderInfo")

                if (responseCode == "00") {
                    ActivityHelper.startActivity<BuyTopupSuccessActivity, Long>(this@FirebaseDynamicLinksActivity, Constant.DATA_2, (orderInfo
                            ?: "-1").toLong())
                } else {
                    ActivityHelper.startActivity<BuyTopupSuccessActivity, Long>(this@FirebaseDynamicLinksActivity, Constant.DATA_2, -1)
                }
            }
            buyCardSuccess -> {
                val responseCode = deepLink?.getQueryParameter("vnp_ResponseCode")
                val orderInfo = deepLink?.getQueryParameter("vnp_OrderInfo")

                if (responseCode == "00") {
                    ActivityHelper.startActivity<BuyCardSuccessActivity, Long>(this@FirebaseDynamicLinksActivity, Constant.DATA_2, (orderInfo
                            ?: "-1").toLong())
                } else {
                    ActivityHelper.startActivity<BuyCardSuccessActivity, Long>(this@FirebaseDynamicLinksActivity, Constant.DATA_2, -1)
                }
            }
            notification -> {
                ActivityHelper.startActivity<ListNotificationActivity>(this@FirebaseDynamicLinksActivity)
            }
            else -> {
                checkLink()
            }
        }

        finishActivity()
    }

    private fun getListCampaign(id: String) {
        if (NetworkHelper.isConnected(this)) {
            DialogHelper.showLoading(this)
            ListCampaignInteractor().getListCampaign(0, 20, object : ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICCampaign>>) {
                    DialogHelper.closeLoading(this@FirebaseDynamicLinksActivity)
                    for (campaign in obj.data?.rows ?: mutableListOf()) {
                        if (campaign.id == id) {
                            val intent = Intent(this@FirebaseDynamicLinksActivity, ListShakeGridBoxActivity::class.java)
                            intent.putExtra(Constant.DATA_1, campaign)
                            intent.putExtra(Constant.DATA_2, 1)
                            startActivity(intent)
                        }
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(this@FirebaseDynamicLinksActivity)
                    if (HomeActivity.isOpen == true) {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
                    } else {
                        ActivityUtils.startActivity<HomeActivity, Int>(this@FirebaseDynamicLinksActivity, Constant.DATA_1, 1)
                    }
                }
            })
        } else {
            if (HomeActivity.isOpen == true) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
            } else {
                ActivityUtils.startActivity<HomeActivity, Int>(this@FirebaseDynamicLinksActivity, Constant.DATA_1, 1)
            }
        }
    }

    private fun showLoginDialog() {
        object : RewardLoginDialog(this@FirebaseDynamicLinksActivity) {
            override fun onLogin() {
                ActivityUtils.startActivityForResult<IckLoginActivity>(this@FirebaseDynamicLinksActivity, requestLogin)
            }

            override fun onRegister() {
                this@FirebaseDynamicLinksActivity.simpleStartForResultActivity(IckLoginActivity::class.java, requestLogin)
//                ActivityUtils.startActivityForResult<IckLoginActivity>(this@FirebaseDynamicLinksActivity, Constant.DATA_1, Constant.REGISTER_TYPE, requestLogin)
            }

            override fun onDismiss() {
                finishActivity()
            }
        }.show()
    }

    private fun finishActivity() {
        finish()
        overridePendingTransition(R.anim.none, R.anim.none)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestLogin) {
            if (resultCode == Activity.RESULT_OK) {
                checkTarget()
            } else {
                finishActivity()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PermissionHelper.checkResult(grantResults)) {
            if (requestCode == ICK_REQUEST_CAMERA) {
                if (requestCode == ICK_REQUEST_CAMERA) {
                    V6ScanditActivity.create(this)
                }
            }
        }

        finishActivity()
    }
}