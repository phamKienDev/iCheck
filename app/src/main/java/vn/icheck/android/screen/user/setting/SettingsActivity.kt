package vn.icheck.android.screen.user.setting

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.confirm.ConfirmDialog
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.ichecklibs.util.showShortSuccessToast

class SettingsActivity : BaseActivityMVVM() {

    lateinit var viewModel: SettingViewModel

    var size: Long = 0
    var vibrate = false
    var more = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

        setupView()
        initView()
        initializeCache()
        initListener()
        getDataServer()
    }

    private fun setupView() {
        btnReset.background = ViewHelper.bgWhiteStrokePrimary1Corners4(this)
    }

    private fun initView() {
        txtTitle.text = "Cấu hình"

        imgBack.setOnClickListener {
            onBackPressed()
        }

        viewModel.getNotifySetting()

        if (SettingManager.getVibrateSetting) {
            switchVibrate.setImageResource(R.drawable.ic_switch_on_24px)
        } else {
            switchVibrate.setImageResource(R.drawable.ic_switch_off_24px)
        }

        if (SettingManager.getSoundSetting) {
            switchSound.setImageResource(R.drawable.ic_switch_on_24px)
        } else {
            switchSound.setImageResource(R.drawable.ic_switch_off_24px)
        }

        if (SettingManager.getLanguageENSetting) {
            textView.text = "Language:"
            tvLanguage.text = "English"
            imgLanguage.setImageResource(R.drawable.ic_language_english_24)

            appCompatTextView.text = "Ngôn ngữ:"
            tvLanguageEN.text = "Việt Nam"
            imgLanguageEN.setImageResource(R.drawable.ic_language_viet_nam_24)
        } else {
            appCompatTextView.text = "Language:"
            tvLanguageEN.text = "English"
            imgLanguageEN.setImageResource(R.drawable.ic_language_english_24)

            textView.text = "Ngôn ngữ:"
            tvLanguage.text = "Việt Nam"
            imgLanguage.setImageResource(R.drawable.ic_language_viet_nam_24)
        }
    }

    private fun initializeCache() {
        size += viewModel.getDirSize(this.cacheDir)
        size += viewModel.getDirSize(this.externalCacheDir)
        tvClearCache.text = viewModel.readableFileSize(size)
    }

    private fun initListener() {
        tvClearCache.setOnClickListener {
            showDialogClearCache()
        }

        switchVibrate.setOnClickListener {
            if (!SettingManager.getVibrateSetting) {
                vibrate = true
                SettingManager.setVibrateSetting(true)
                switchVibrate.setImageResource(R.drawable.ic_switch_on_24px)
            } else {
                vibrate = false
                SettingManager.setVibrateSetting(false)
                switchVibrate.setImageResource(R.drawable.ic_switch_off_24px)
            }
            viewModel.checkSwitchVibrate(vibrate)
        }

        switchSound.setOnClickListener {
            if (!SettingManager.getSoundSetting) {
                SettingManager.setSoundSetting(true)
                switchSound.setImageResource(R.drawable.ic_switch_on_24px)
            } else {
                SettingManager.setSoundSetting(false)
                switchSound.setImageResource(R.drawable.ic_switch_off_24px)
            }
        }

        btnReset.setOnClickListener {
            val ob = object : ConfirmDialog(this,"Đặt lại mặc định?", "Bạn chắc chắn muốn đặt lại cài đặt \n" +
                    "về mặc định ban đầu?", "Để Sau", "Đồng ý", true) {
                override fun onDisagree() {
                    dismiss()
                }

                override fun onAgree() {
                    resetData()
                }

                override fun onDismiss() {
                }
            }
            ob.show()
        }

        imgMore.setOnClickListener {
            if (more) {
                more = false
                imgMore.setImageResource(R.drawable.ic_arrow_up_light_blue_24px)
                layoutEN.visibility = View.VISIBLE
            } else {
                more = true
                imgMore.setImageResource(R.drawable.ic_arrow_down_light_blue_24px)
                layoutEN.visibility = View.GONE
            }
        }

        layoutEN.setOnClickListener {
            if (SettingManager.getLanguageENSetting) {
                SettingManager.setLanguageENSetting(false)

                appCompatTextView.text = "Language:"
                tvLanguageEN.text = "English"
                imgLanguageEN.setImageResource(R.drawable.ic_language_english_24)

                textView.text = "Ngôn ngữ:"
                tvLanguage.text = "Việt Nam"
                imgLanguage.setImageResource(R.drawable.ic_language_viet_nam_24)
            } else {
                SettingManager.setLanguageENSetting(true)

                textView.text = "Language:"
                tvLanguage.text = "English"
                imgLanguage.setImageResource(R.drawable.ic_language_english_24)

                appCompatTextView.text = "Ngôn ngữ:"
                tvLanguageEN.text = "Việt Nam"
                imgLanguageEN.setImageResource(R.drawable.ic_language_viet_nam_24)
            }
        }
    }

    private fun getDataServer() {
        viewModel.onError.observe(this, Observer {
            it.message?.let { it1 -> showLongError(it1) }
        })

        viewModel.onSuccess.observe(this, Observer { obj ->
            if (obj.NEWS) {
                switchNews.setImageResource(R.drawable.ic_switch_on_24px)
            } else {
                switchNews.setImageResource(R.drawable.ic_switch_off_24px)
            }
            if (obj.CAMPAIGN) {
                switchCampaign.setImageResource(R.drawable.ic_switch_on_24px)
            } else {
                switchCampaign.setImageResource(R.drawable.ic_switch_off_24px)
            }
            if (obj.MESSAGE) {
                switchMessage.setImageResource(R.drawable.ic_switch_on_24px)
            } else {
                switchMessage.setImageResource(R.drawable.ic_switch_off_24px)
            }

            switchNews.setOnClickListener {
                if (obj.NEWS) {
                    viewModel.postNotifySetting("NEWS", false)
                } else {
                    viewModel.postNotifySetting("NEWS", true)
                }
            }

            switchCampaign.setOnClickListener {
                if (obj.CAMPAIGN) {
                    viewModel.postNotifySetting("CAMPAIGN", false)
                } else {
                    viewModel.postNotifySetting("CAMPAIGN", true)
                }
            }

            switchMessage.setOnClickListener {
                if (obj.MESSAGE) {
                    viewModel.postNotifySetting("MESSAGE", false)
                } else {
                    viewModel.postNotifySetting("MESSAGE", true)
                }
            }
        })

        viewModel.onNews.observe(this, Observer {

            if (it) {
                switchNews.setImageResource(R.drawable.ic_switch_on_24px)
            } else {
                switchNews.setImageResource(R.drawable.ic_switch_off_24px)
            }
        })

        viewModel.onCampaign.observe(this, Observer {
            if (it) {
                switchCampaign.setImageResource(R.drawable.ic_switch_on_24px)
            } else {
                switchCampaign.setImageResource(R.drawable.ic_switch_off_24px)
            }
        })

        viewModel.onMessage.observe(this, Observer {
            if (it) {
                switchMessage.setImageResource(R.drawable.ic_switch_on_24px)
            } else {
                switchMessage.setImageResource(R.drawable.ic_switch_off_24px)
            }
        })
    }

    private fun resetData() {
        SettingManager.setVibrateSetting(true)
        SettingManager.setSoundSetting(true)
        SettingManager.setLanguageENSetting(false)
        viewModel.postNotifySetting("MESSAGE", true)
        viewModel.postNotifySetting("CAMPAIGN", true)
        viewModel.postNotifySetting("NEWS", true)
        switchSound.setImageResource(R.drawable.ic_switch_on_24px)
        switchVibrate.setImageResource(R.drawable.ic_switch_on_24px)

        appCompatTextView.text = "Language:"
        tvLanguageEN.text = "English"
        imgLanguageEN.setImageResource(R.drawable.ic_language_english_24)

        textView.text = "Ngôn ngữ:"
        tvLanguage.text = "Việt Nam"
        imgLanguage.setImageResource(R.drawable.ic_language_viet_nam_24)
        viewModel.getNotifySetting()
    }

    private fun showDialogClearCache() {
        DialogHelper.showConfirm(this, getString(R.string.xoa_cache), getString(R.string.ban_co_muon_giai_phong_dung_luong), getString(R.string.de_sau), getString(R.string.dong_y), true, object : ConfirmDialogListener {
            override fun onDisagree() {
            }

            override fun onAgree() {
                size = 0
                viewModel.deleteCache()
                tvClearCache.text = viewModel.readableFileSize(size)
                this@SettingsActivity.showShortSuccessToast("Bạn đã xóa cache thành công!")
            }
        })
    }
}