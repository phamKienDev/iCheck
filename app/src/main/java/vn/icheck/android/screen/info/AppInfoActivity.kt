package vn.icheck.android.screen.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import vn.icheck.android.BuildConfig
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.databinding.ActivityAppInfoBinding
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.util.ick.*

class AppInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityAppInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.tvTitle rText R.string.thong_tin_ung_dung
        binding.header.icBack.setOnClickListener {
            finish()
        }

        binding.tvVersion simpleText BuildConfig.VERSION_NAME

        binding.btnUpdate.setOnClickListener {
            openAppInGooglePlay()
        }

        binding.groupUpdate.visibleOrGone(SettingManager.configUpdateApp?.isSuggested == true)
    }
}