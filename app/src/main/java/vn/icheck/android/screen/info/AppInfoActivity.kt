package vn.icheck.android.screen.info

import android.os.Bundle
import vn.icheck.android.BuildConfig
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.R
import vn.icheck.android.databinding.ActivityAppInfoBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.util.ick.*

class AppInfoActivity : BaseActivityMVVM() {

    lateinit var binding: ActivityAppInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.txtTitle.setText(R.string.thong_tin_ung_dung)
        binding.header.imgBack.setOnClickListener {
            finish()
        }

        binding.tvVersion simpleText BuildConfig.VERSION_NAME

        binding.btnUpdate.background = ViewHelper.bgPrimaryCorners4(this)
        binding.btnUpdate.setOnClickListener {
            openAppInGooglePlay()
        }

        binding.groupUpdate.visibleOrGone(SettingManager.configUpdateApp?.isSuggested == true)
    }
}