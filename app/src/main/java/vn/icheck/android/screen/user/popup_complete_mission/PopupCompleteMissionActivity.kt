package vn.icheck.android.screen.user.popup_complete_mission

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_popup_complete_mission.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.screen.user.mygift.MyGiftActivity

class PopupCompleteMissionActivity : BaseActivityMVVM() {

    companion object {
        fun start(giftCount: Int?, campaign: String?, context: Context?) {
            val start = Intent(context, PopupCompleteMissionActivity::class.java)
            start.putExtra("giftCount", giftCount)
            start.putExtra("campaignId", campaign)
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            start.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context?.startActivity(start)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_complete_mission)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val giftCount = intent.getIntExtra("giftCount",0)
        val campaignId = intent.getStringExtra("campaignId")

        tvNameGift.text = "Bạn nhận được $giftCount lượt mở quà"

        btnClose.setOnClickListener {
            finish()
            this.overridePendingTransition(0, 0)
        }

        btnGift.setOnClickListener {
            if (campaignId != null) {
                startActivity<ListShakeGridBoxActivity>(Constant.DATA_1, campaignId)
            }
            finish()

        }
    }

}