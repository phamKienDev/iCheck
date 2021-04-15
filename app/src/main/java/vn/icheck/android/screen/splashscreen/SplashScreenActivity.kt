package vn.icheck.android.screen.splashscreen

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import vn.icheck.android.BuildConfig
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.checktheme.CheckThemeActivity
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.tracking.TrackingAllHelper

/**
 * Created by VuLCL on 3/5/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SplashScreenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SettingManager.setDeviceID(DeviceUtils.getUDID(this))
        SettingManager.appVersion = BuildConfig.VERSION_NAME


        val targetFull = intent?.getStringExtra(Constant.DATA_3)
        if (!targetFull.isNullOrEmpty()) {
            FirebaseDynamicLinksActivity.startDestinationUrl(this, targetFull)
            finish()
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

            if (targetType.isNullOrEmpty()) {
                if (HomeActivity.isOpen == true) {
                    finish()
                } else {
                    ActivityHelper.startActivityAndFinish<CheckThemeActivity>(this@SplashScreenActivity)
                }
            } else {
//                FirebaseDynamicLinksActivity.startTarget(this, targetType, targetID)
                FirebaseDynamicLinksActivity.startTargetPath(this, intent.getStringExtra("path"))
                finish()
            }

            overridePendingTransition(R.anim.none, R.anim.none)
        }

        Handler().postDelayed({
            TrackingAllHelper.trackAppStarted()
            InsiderHelper.setUserAttributes()
        }, 500)
    }
}