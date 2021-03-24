package vn.icheck.android

//import vn.teko.hestia.trackingbridge.AppTrackingBridgeManager
//import vn.teko.hestia.trackingbridge.TrackingBridgeManager
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDex
import androidx.work.Configuration
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.useinsider.insider.Insider
import com.useinsider.insider.InsiderCallbackType
import dagger.hilt.android.HiltAndroidApp
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.sdk.ChatSdk
import vn.icheck.android.constant.Constant
import vn.icheck.android.loyalty.helper.CampaignLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.loyalty.sdk.LoyaltySdk
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICNetworkManager2
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.splashscreen.SplashScreenActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.services.FTActivityLifecycleCallbacks
import vn.icheck.android.tracking.insider.TrackingBridge
import vn.icheck.android.util.ick.logDebug
import vn.teko.android.tracker.core.Tracker
import vn.teko.android.tracker.core.TrackerConfig
import vn.teko.hestia.trackingbridge.AppTrackingBridgeManager
//import vn.teko.terra.core.android.terra.TerraApp
//import vn.teko.terra.core.android.terra.TerraApp
import javax.inject.Inject

@HiltAndroidApp
class ICheckApplication : Application(), Configuration.Provider {

    lateinit var mFirebase: FirebaseContainer

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    val mFTActivityLifecycleCallbacks = FTActivityLifecycleCallbacks()

    private val product = "product"
    private val icheckProduct = "icheck_product"
    private val barcode = "barcode"
    private val evs6 = "evs6"
    private val evs61 = "evs61"
    private val review = "review"
    private val productQuestion = "product_question"
    private val shopProduct = "shop_product"
    private val loyaltyCampaign = "loyalty_campaign"
    private val dailyMissions = "daily_missions"
    private val advanceMissions = "advance_missions"
    private val completedMission = "completed_mission"
    private val completedMissions = "completed_missions"
    private val breakReward = "break_reward"

//    private var friendList = arrayListOf<Long>()
//
//    fun setFriendList(friendIdList: List<Long>) {
//        friendList.clear()
//        friendList.addAll(friendIdList)
//    }

    lateinit var dataCaptureContext: DataCaptureContext
    override fun onCreate() {
        super.onCreate()
        val key = if (BuildConfig.FLAVOR.contentEquals("dev")) getString(R.string.scandit_v6_key_dev) else getString(R.string.scandit_v6_key_live)
        dataCaptureContext = DataCaptureContext.forLicenseKey(key)
        FirebaseApp.initializeApp(this)
        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)
//        ScanditLicense.setAppKey(APIConstants.scanditLicenseKey())

        INSTANCE = this
        mFirebase = FirebaseContainer()
        registerActivityLifecycleCallbacks(mFTActivityLifecycleCallbacks)

        ICNetworkManager2.registerPVCombank(object : ICNetworkManager2.PVComBankListener {
            override fun onEndOfToken() {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.FINISH_ALL_PVCOMBANK))
            }
        })

        LoyaltySdk.startFirebaseDynamicLinksActivity(object : LoyaltySdk.SdkLoyaltyFirebaseDynamicLinksListener {
            override fun startActivity(schema: String?) {
                currentActivity()?.let { activity ->
                    FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                }
            }
        })

        LoyaltySdk.startActivityForResultLogin(object : LoyaltySdk.IOpenLoginListener {
            override fun startActivityForResultLogin(obj: ICKLoyalty, code: String) {
                currentActivity()?.let { activity ->
                    LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(activity, "requestCode", 1, CampaignLoyaltyHelper.REQUEST_CHECK_CODE, obj, code)
                }
            }
        })

        ChatSdk.startFirebaseDynamicLinksActivity(object : ChatSdk.SdkChatListener {
            override fun startActivity(schema: String?) {
                currentActivity()?.let { activity ->
                    FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                }
            }
        })

        /*
        *Insider
        */
        Insider.Instance.init(this, APIConstants.insiderPartnerName)
        Insider.Instance.setSplashActivity(SplashScreenActivity::class.java)
        initDeeplinkInsider()

        /*
        *Teko
        */
        TRACKER.initialize(
                getInstance().applicationContext.packageManager.getApplicationLabel(getInstance().applicationContext.packageManager.getApplicationInfo(getInstance().applicationContext.packageName, 0)).toString(),
                TrackerConfig(this,
                        APIConstants.trackingAppId,
                        getInstance().applicationContext.packageManager.getPackageInfo(getInstance().applicationContext.packageName, 0).versionName,
                        APIConstants.trackingUrlTeko,
                        true))
        TRACKER.resetCurrentViewId()

        /*
        * VNShop
        * */
//        TerraApp.initializeApp(
//                this,
//                "icheck:android:playstore:0.0.1", //for icheck: icheck:android:playstore:0.0.1
//                "iCheck",
//                "icheck_pandora_config.json",
//                false
//        )

        /*
        * Tracking Bridge
        * */
        AppTrackingBridgeManager.createInstance(TrackingBridge())
        Thread.UncaughtExceptionHandler { thread, throwable ->
            if (thread.id == Looper.getMainLooper().thread.id) {
                if (currentActivity() !is HomeActivity) {
                    currentActivity()?.finish()
                }
                currentActivity()?.recreate()
            }
        }
    }

    private fun initDeeplinkInsider() {
        Insider.Instance.registerInsiderCallback { p0, p1 ->
            if (p1 == InsiderCallbackType.NOTIFICATION_OPEN || p1 == InsiderCallbackType.INAPP_BUTTON_CLICK) {
                var targetType = try {
                    p0?.getJSONObject("data")?.getString("target_type")
                } catch (e: Exception) {
                    null
                }
                if (targetType.isNullOrEmpty()) {
                    targetType = try {
                        p0?.getJSONObject("data")?.getString("type")
                    } catch (e: Exception) {
                        null
                    }
                }

                if (!targetType.isNullOrEmpty()) {
                    var des = "icheck://$targetType"

                    try {
                        when (targetType) {
                            evs61 -> {
                                val targetCode = try {
                                    p0?.getJSONObject("data")?.getString("code")
                                } catch (e: Exception) {
                                    null
                                }

                                if (!targetCode.isNullOrEmpty()) {
                                    des += "?code=$targetCode"
                                } else {
                                    val targetId = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    if (!targetId.isNullOrEmpty()) {
                                        des += "?id=$targetCode"
                                    }
                                }
                            }
                            evs6 -> {
                                val targetCode = try {
                                    p0?.getJSONObject("data")?.getString("code")
                                } catch (e: Exception) {
                                    null
                                }

                                if (!targetCode.isNullOrEmpty()) {
                                    des += "?code=$targetCode"
                                } else {
                                    val targetQri = try {
                                        p0?.getJSONObject("data")?.getString("qri")
                                    } catch (e: Exception) {
                                        null
                                    }

                                    if (!targetQri.isNullOrEmpty()) {
                                        des += "?qri=$targetQri"
                                    } else {
                                        try {
                                            val targetId = p0?.getJSONObject("data")?.getString("qri")
                                            if (!targetId.isNullOrEmpty()) {
                                                des += "?qri=$targetId"
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            }
                            product -> {
                                val targetBarcode = try {
                                    p0?.getJSONObject("data")?.getString("barcode")
                                } catch (e: Exception) {
                                    null
                                }

                                if (!targetBarcode.isNullOrEmpty()) {
                                    val isScan = try {
                                        p0?.getJSONObject("data")?.getString("isScan")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    des += if (!isScan.isNullOrEmpty()) {
                                        "?barcode=$targetBarcode&isScan=$isScan"
                                    } else {
                                        "?barcode=$targetBarcode"
                                    }
                                } else {
                                    val targetId = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    if (!targetId.isNullOrEmpty()) {
                                        des += "?id=$targetId"
                                    }
                                }
                            }
                            barcode -> {
                                val targetBarcode = try {
                                    p0?.getJSONObject("data")?.getString("id")
                                } catch (e: Exception) {
                                    null
                                }
                                if (!targetBarcode.isNullOrEmpty()) {
                                    des += "?id=$targetBarcode"
                                }
                            }
                            icheckProduct -> {
                                val targetBarcode = try {
                                    p0?.getJSONObject("data")?.getString("barcode")
                                } catch (e: Exception) {
                                    null
                                }
                                val targetId = try {
                                    p0?.getJSONObject("data")?.getString("id")
                                } catch (e: Exception) {
                                    null
                                }

                                if (targetId.isNullOrEmpty()) {
                                    if (!targetBarcode.isNullOrEmpty()) {
                                        val isScan = p0?.getJSONObject("data")?.getString("isScan")
                                        des += if (!isScan.isNullOrEmpty()) {
                                            "?barcode=$targetBarcode&isScan=$isScan"
                                        } else {
                                            "?barcode=$targetBarcode"
                                        }
                                    }
                                } else {
                                    if (!targetId.isNullOrEmpty()) {
                                        des += "?id=$targetId"
                                    }
                                }
                            }
                            review -> {
                                val objectType = try {
                                    p0?.getJSONObject("data")?.getString("object_type")
                                } catch (e: Exception) {
                                    null
                                }
                                if (!objectType.isNullOrEmpty()) {
                                    val objectId = try {
                                        p0?.getJSONObject("data")?.getString("object_id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    des += if (!objectId.isNullOrEmpty()) {
                                        "?object_type=$objectType&object_id=$objectId"
                                    } else {
                                        "?object_type=$objectType"
                                    }
                                } else {
                                    val id = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    if (!id.isNullOrEmpty()) {
                                        des += "?id=$id"
                                    }
                                }
                            }
                            productQuestion -> {
                                val objectType = try {
                                    p0?.getJSONObject("data")?.getString("object_type")
                                } catch (e: Exception) {
                                    null
                                }

                                if (!objectType.isNullOrEmpty()) {
                                    val objectId = try {
                                        p0?.getJSONObject("data")?.getString("object_id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    des += if (!objectId.isNullOrEmpty()) {
                                        "?object_type=$objectType&object_id=$objectId"
                                    } else {
                                        "?object_type=$objectType"
                                    }
                                } else {
                                    val id = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    if (!id.isNullOrEmpty()) {
                                        des += "?id=$id"
                                    }
                                }
                            }
                            shopProduct -> {
                                val variantId = try {
                                    p0?.getJSONObject("data")?.getString("variantId")
                                } catch (e: Exception) {
                                    null
                                }
                                if (!variantId.isNullOrEmpty()) {
                                    val productId = try {
                                        p0?.getJSONObject("data")?.getString("productId")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    des += if (!productId.isNullOrEmpty()) {
                                        "?variantId=$variantId&productId=$productId"
                                    } else {
                                        "?variantId=$variantId"
                                    }
                                } else {
                                    val id = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    if (!id.isNullOrEmpty()) {
                                        des += "?id=$id"
                                    }
                                }
                            }
                            loyaltyCampaign -> {
                                val id = try {
                                    p0?.getJSONObject("data")?.getString("id")
                                } catch (e: Exception) {
                                    null
                                }
                                if (!id.isNullOrEmpty()) {
                                    val target = try {
                                        p0?.getJSONObject("data")?.getString("target")
                                    } catch (e: Exception) {
                                        null
                                    }
                                    des += if (!target.isNullOrEmpty()) {
                                        "?id=$id&productId=$target"
                                    } else {
                                        "?id=$id"
                                    }
                                }
                            }
                            dailyMissions -> {
                                var campaignId = try {
                                    p0?.getJSONObject("data")?.getString("campaignId")
                                } catch (e: Exception) {
                                    null
                                }
                                if (campaignId.isNullOrEmpty()) {
                                    campaignId = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (!campaignId.isNullOrEmpty()) {
                                    des += "?campaignId=$campaignId"
                                }
                            }
                            advanceMissions -> {
                                var campaignId = try {
                                    p0?.getJSONObject("data")?.getString("campaignId")
                                } catch (e: Exception) {
                                    null
                                }
                                if (campaignId.isNullOrEmpty()) {
                                    campaignId = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (!campaignId.isNullOrEmpty()) {
                                    des += "?campaignId=$campaignId"
                                }
                            }
                            completedMission -> {
                                var campaignId = try {
                                    p0?.getJSONObject("data")?.getString("campaignId")
                                } catch (e: Exception) {
                                    null
                                }
                                if (campaignId.isNullOrEmpty()) {
                                    campaignId = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (!campaignId.isNullOrEmpty()) {
                                    des += "?campaignId=$campaignId"
                                }
                            }
                            completedMissions -> {
                                var campaignId = try {
                                    p0?.getJSONObject("data")?.getString("campaignId")
                                } catch (e: Exception) {
                                    null
                                }
                                if (campaignId.isNullOrEmpty()) {
                                    campaignId = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (!campaignId.isNullOrEmpty()) {
                                    des += "?campaignId=$campaignId"
                                }
                            }
                            breakReward -> {
                                var campaignId = try {
                                    p0?.getJSONObject("data")?.getString("campaignId")
                                } catch (e: Exception) {
                                    null
                                }
                                if (campaignId.isNullOrEmpty()) {
                                    campaignId = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (!campaignId.isNullOrEmpty()) {
                                    des += "?campaignId=$campaignId"
                                }
                            }
                            else -> {
                                var targetID = try {
                                    p0?.getJSONObject("data")?.getString("target_id")
                                } catch (e: Exception) {
                                    null
                                }
                                if (targetID.isNullOrEmpty()) {
                                    targetID = try {
                                        p0?.getJSONObject("data")?.getString("id")
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (!targetID.isNullOrEmpty()) {
                                    des += "?id=$targetID"
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }

                    val intent = Intent(this, SplashScreenActivity::class.java)
                    intent.putExtra(Constant.DATA_3, des)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .setWorkerFactory(workerFactory)
                .build()
    }

    companion object {
        private lateinit var INSTANCE: ICheckApplication
        private val TRACKER = Tracker()

        fun getInstance(): ICheckApplication {
            return INSTANCE
        }

        fun getTracker(): Tracker {
            return TRACKER
        }

        fun currentActivity(): Activity? {
            return INSTANCE.mFTActivityLifecycleCallbacks.currentActivity
        }

        fun getString(resource: Int): String {
            return INSTANCE.getString(resource)
        }

        fun getError(message: String?): String {
            return message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
    }
}