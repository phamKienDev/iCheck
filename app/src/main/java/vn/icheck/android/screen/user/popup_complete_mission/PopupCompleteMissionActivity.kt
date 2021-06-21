package vn.icheck.android.screen.user.popup_complete_mission

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_popup_complete_mission.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.util.ick.rText

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

        val giftCount = intent.getIntExtra("giftCount", 0)
        val campaignId = intent.getStringExtra("campaignId")

        tvNameGift.rText(R.string.ban_nhan_duoc_s_luot_mo_qua, giftCount)

        btnClose.setOnClickListener {
            finish()
            this.overridePendingTransition(0, 0)
        }

        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REQUEST_MISSION_SUCCESS, campaignId))

        btnGift.apply {
            background = ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                if (campaignId != null) {
                    startActivity<ListShakeGridBoxActivity>(Constant.DATA_1, campaignId)
                }
                finish()
            }
        }
    }
}