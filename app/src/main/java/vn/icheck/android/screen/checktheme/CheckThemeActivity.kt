package vn.icheck.android.screen.checktheme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_check_theme.*
import kotlinx.coroutines.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.chat.icheckchat.sdk.ChatSdk
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.ICK_TOKEN
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.FileHelper
import vn.icheck.android.helper.RelationshipHelper
import vn.icheck.android.helper.ShareSessionToModule
import vn.icheck.android.loyalty.helper.StatusBarHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.*
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.user.welcome.WelcomeActivity
import vn.icheck.android.util.ick.openAppInGooglePlay
import java.io.File

class CheckThemeActivity : BaseActivityMVVM() {
    private lateinit var viewModel: CheckThemeViewModel

    private var notificationPath: String? = null

    private var isShowUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_theme)

        getData()
        onInitView()
    }

    private fun getData() {
        notificationPath = intent?.getStringExtra(Constant.DATA_1)
    }

    private fun onInitView() {
        StatusBarHelper.setOverStatusBarLight(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.new_notification)
            val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_HIGH)
            mChannel.description = getString(R.string.default_notification_channel_desc)
            val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
            mChannel.setSound(soundUri, audioAttributes)

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(mChannel)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            Glide.get(this@CheckThemeActivity).clearDiskCache()
        }

        viewModel = ViewModelProvider(this).get(CheckThemeViewModel::class.java)

        startLoading()
        checkLogin()
    }

    private fun startLoading() {
        progressBar?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_looping))
    }

    private fun checkLogin() {
        if (!SessionManager.isLoggedAnyType) {
            viewModel.loginAnonymous().observe(this, Observer {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        if (!it.data?.data?.token.isNullOrEmpty()) {
                            it.data!!.data?.user = ICUser().apply {
                                this.id = it.data!!.data?.appUserId ?: 0L
                            }
                            SessionManager.session = it.data!!.data!!
                            ShareSessionToModule.setSession(it.data!!.data!!)
                            PreferenceManager.getDefaultSharedPreferences(ICheckApplication.getInstance()).edit().putString(ICK_TOKEN, it.data!!.data?.token).apply()
                        }
                        getThemeSetting()
                    }
                    else -> {
                        getThemeSetting()
                    }
                }
            })
        } else {
            getThemeSetting()
        }
    }

    private fun getThemeSetting() {
        ChatSdk.shareIntent(SessionManager.session.firebaseToken, SessionManager.session.user?.id, SessionManager.session.token, DeviceUtils.getUniqueDeviceId(),SessionManager.isUserLogged)

        lifecycleScope.launch {
            var themeSettingRes: ICResponse<ICThemeSetting>? = null
            var domainMarketingRes: ICResponse<ICListResponse<ICClientSetting>>? = null
            var domainVerifyRes: ICResponse<ICListResponse<ICClientSetting>>? = null
            var appInitRes: ICResponse<ICListResponse<ICClientSetting>>? = null
            var productContactRes: ICResponse<ICListResponse<ICClientSetting>>? = null
            var relationshipInformationRes: ICResponse<ICRelationshipsInformation>? = null
            var configUpdateAppRes: ICResponse<ICConfigUpdateApp>? = null

            listOf(
                    lifecycleScope.async {
                        try {
                            themeSettingRes = withTimeoutOrNull(5000L) { viewModel.getThemeSetting() }
                        } catch (e: Exception) {
                        }
                    },
                    lifecycleScope.async {
                        try {
                            domainMarketingRes = withTimeoutOrNull(5000L) { viewModel.getClientSetting("domain-marketing") }
                        } catch (e: Exception) {
                        }
                    },
                    lifecycleScope.async {
                        try {
                            domainVerifyRes = withTimeoutOrNull(5000L) { viewModel.getClientSetting("domain-verify") }
                        } catch (e: Exception) {
                        }
                    },
                    lifecycleScope.async {
                        try {
                            appInitRes = withTimeoutOrNull(5000L) { viewModel.getClientSetting("app-init", "app-default-scheme") }
                        } catch (e: Exception) {
                        }
                    },
                    lifecycleScope.async {
                        try {
                            productContactRes = withTimeoutOrNull(5000L) { viewModel.getClientSetting("product-contact") }
                        } catch (e: Exception) {
                        }
                    },
                    lifecycleScope.async {
                        try {
                            relationshipInformationRes = withTimeoutOrNull(5000L) { viewModel.getRelationshipInformation() }
                        } catch (e: Exception) {
                        }
                    },
                    lifecycleScope.async {
                        try {
                            configUpdateAppRes = withTimeoutOrNull(5000L) { viewModel.getConfigUpdateApp() }
                        } catch (e: Exception) {
                        }
                    },
            ).awaitAll()

            if (themeSettingRes != null) {
                if (SettingManager.themeSetting?.updatedAt != themeSettingRes?.data?.updatedAt) {
                    val file = File(FileHelper.getPath(this@CheckThemeActivity) + FileHelper.imageFolder)
                    if (file.exists()) {
                        FileHelper.deleteTheme(file)
                    }
                }
                SettingManager.themeSetting = themeSettingRes?.data
                SettingManager.setAppThemeColor(themeSettingRes?.data)
            }
            if (domainMarketingRes?.data?.rows != null) {
                for (i in domainMarketingRes!!.data!!.rows.size - 1 downTo 1) {
                    domainMarketingRes!!.data!!.rows.removeAt(i)
                }
                SettingManager.domainQr = domainMarketingRes!!.data!!.rows
            }
            if (domainVerifyRes?.data?.rows != null) {
                for (i in domainVerifyRes!!.data!!.rows.size - 1 downTo 1) {
                    domainVerifyRes!!.data!!.rows.removeAt(i)
                }
                SettingManager.trustDomain = domainVerifyRes!!.data!!.rows
            }
            appInitRes?.data?.rows?.firstOrNull()?.let {
                if (!it.value.isNullOrEmpty()) {
                    viewModel.appInitScheme = it.value!!
                }
            }
            productContactRes?.data?.rows?.let {
                SettingManager.productContact = it
            }
            relationshipInformationRes?.let {
                if (it.data != null) {
                    RelationshipHelper.saveData(it.data!!)
                }
            }
            SettingManager.configUpdateApp = configUpdateAppRes?.data

            if (configUpdateAppRes?.data?.isForced == true) {
                showUpdateDialog()
            } else {
                onGoToHome()
            }
        }
    }

    private fun showUpdateDialog() {
        isShowUpdate = true
        DialogHelper.showNotification(this@CheckThemeActivity, R.string.force_update_title, R.string.force_update_content, R.string.cap_nhat, false, object : NotificationDialogListener {
            override fun onDone() {
                this@CheckThemeActivity.openAppInGooglePlay()
            }
        })
    }

    private fun onGoToHome() {
        if (viewModel.appInitScheme.isNotEmpty()) {
            val intent = Intent(this@CheckThemeActivity, WelcomeActivity::class.java)
            intent.putExtra(Constant.DATA_2, viewModel.appInitScheme)
            intent.putExtra(Constant.DATA_3, notificationPath)
            startActivityAndFinish(intent)
        } else {
            startActivityAndFinish<WelcomeActivity>()
        }
    }

    override fun onResume() {
        super.onResume()

        if (isShowUpdate) {
            showUpdateDialog()
        }
    }
}