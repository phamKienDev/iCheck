package vn.icheck.android.loyalty.dialog

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.android.synthetic.main.dialog_accumulate_point_success.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.helper.PointHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.WidgetHelper

abstract class DialogAccumulatePointSuccess(
        context: Context,
        private val point: Long,
        private val avatar: String,
        private val nameCampaign: String,
        private val nameShop: String,
        private val campaignID: Long,
        private val backgroundButton: Int,
        private val pointName: String?,
        private val message: String
) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_accumulate_point_success
    override val getIsCancelable: Boolean
        get() = false

    @SuppressLint("SetTextI18n")
    override fun onInitView() {
        if (backgroundButton == R.drawable.bg_gradient_button_orange_yellow) {
            PointHelper.updatePoint(campaignID)
        } else {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_POINT))
        }

        tvPoint.apply {
            text = if (!pointName.isNullOrEmpty()) {
                "${TextHelper.formatMoneyPhay(point)} $pointName"
            } else {
                context.rText(R.string.s_diem_tich_luy, TextHelper.formatMoneyPhay(point))
            }
        }

        tvMessage.text = message

        WidgetHelper.loadImageUrl(imgAvatar, avatar)

        tvNameCampaign.text = nameCampaign

        tvNameShop.text = nameShop

        imgClose.setOnClickListener {
            dismiss()
        }

        btnDefault.setBackgroundResource(backgroundButton)

        btnDefault.setOnClickListener {
            onClick(campaignID)
        }

        setOnDismissListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_POINT))
            onDismiss()
        }
    }

    protected abstract fun onClick(campaignID: Long)
    protected abstract fun onDismiss()
}