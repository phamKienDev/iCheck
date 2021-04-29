package vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.bottom_sheet

import android.content.Context
import kotlinx.android.synthetic.main.bottom_sheet_more_action_shake.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.network.models.ICCampaign

abstract class MoreShakeBottomSheet(val context: Context, val objCampaign: ICCampaign?) : BaseBottomSheetDialog(context,R.layout.bottom_sheet_more_action_shake, true) {

    fun show() {
        dialog.tvTitleCampaign.text = objCampaign?.title

        dialog.show()
        listener()
    }

    private fun listener() {
        dialog.layoutHistoryShake.setOnClickListener {
            onClickHistoryShake()
        }

        dialog.layoutHistoryReward.setOnClickListener {
            onClickHistoryReward()
        }

        dialog.layoutTutorialShake.setOnClickListener {
            onClickTutorial()
        }

        dialog.layoutInformationShake.setOnClickListener {
            onClickInfor()
        }

        dialog.imgCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    protected abstract fun onClickHistoryShake()
    protected abstract fun onClickHistoryReward()
    protected abstract fun onClickTutorial()
    protected abstract fun onClickInfor()
}