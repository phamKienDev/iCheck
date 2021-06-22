package vn.icheck.android.screen.splashscreen

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import vn.icheck.android.BuildConfig
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.checktheme.CheckThemeActivity
import vn.icheck.android.screen.dialog.DialogFragmentNotificationFirebaseAds
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.tracking.insider.InsiderHelper

/**
 * Created by VuLCL on 3/5/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SettingManager.setDeviceID(DeviceUtils.getUDID(this))
        SettingManager.appVersion = BuildConfig.VERSION_NAME

        val path = intent?.getStringExtra(Constant.DATA_3) ?: intent?.extras?.getString("path")
        if (!path.isNullOrEmpty()) {
            goToCheckTheme(path, null, null)
        } else {
            var targetType = intent?.getStringExtra(Constant.DATA_1)
            if (targetType.isNullOrEmpty()) {
                targetType = intent?.extras?.getString("target_type")
            }
            if (targetType.isNullOrEmpty()) {
                targetType = intent?.extras?.getString("type")
            }

            var targetID = intent?.getStringExtra(Constant.DATA_2)
            if (targetID.isNullOrEmpty()) {
                targetID = intent?.extras?.getString("target_id")
            }
            if (targetID.isNullOrEmpty()) {
                targetID = intent?.extras?.getString("id")
            }

            when {
                (targetType ?: "").contains("popup_image") -> {
                    showDialogNotification(image = targetID, schema = intent?.extras?.getString("action") ?: "")
                }
                (targetType ?: "").contains("popup_html") -> {
                    showDialogNotification(htmlText = targetID)
                }
                (targetType ?: "").contains("popup_link") -> {
                    showDialogNotification(link = targetID)
                }
                else -> {
                    if (targetType.isNullOrEmpty()) {
                        if (HomeActivity.isOpen == true) {
                            finish()
                        } else {
                            goToCheckTheme()
                        }
                    } else {
                        goToCheckTheme(null, targetType, targetID)
                    }
                }
            }

            overridePendingTransition(R.anim.none, R.anim.none)
        }

        Handler().postDelayed({
            TrackingAllHelper.trackAppStarted()
            InsiderHelper.setUserAttributes()
        }, 500)
    }

    private fun showDialogNotification(image: String? = null, htmlText: String? = null, link: String? = null, schema: String? = null) {
        ActivityHelper.startActivityAndFinish<HomeActivity>(this)
        Handler().postDelayed({
            DialogFragmentNotificationFirebaseAds.showPopupFirebase(this, image, htmlText, link, schema)
        }, 2000)
    }

    private fun goToCheckTheme(path: String? = null, type: String? = null, id: String? = null) {
        when {
            !path.isNullOrEmpty() -> {
                if (HomeActivity.isOpen != true) {
                    ActivityHelper.startActivity<CheckThemeActivity>(this, Constant.DATA_1, path)
                } else {
                    FirebaseDynamicLinksActivity.startDestinationUrl(this, path)
                }
            }
            !type.isNullOrEmpty() -> {
                if (HomeActivity.isOpen != true) {
                    ActivityHelper.startActivity<CheckThemeActivity>(this, Constant.DATA_1, vn.icheck.android.ichecklibs.Constant.getPath(type, id))
                } else {
                    FirebaseDynamicLinksActivity.startTarget(this, type, id)
                }
            }
            else -> {
                ActivityHelper.startActivity<CheckThemeActivity>(this)
            }
        }

        finish()
    }
}