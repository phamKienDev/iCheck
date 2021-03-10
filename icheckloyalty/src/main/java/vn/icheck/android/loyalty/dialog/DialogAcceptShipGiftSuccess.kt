package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_accept_ship_gift_success.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.WidgetHelper

abstract class DialogAcceptShipGiftSuccess(context: Context, val image: String, val id: Long, private val backgroundButton: Int) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_accept_ship_gift_success
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        SharedLoyaltyHelper(context).putLong(ConstantsLoyalty.COUNT_GIFT, SharedLoyaltyHelper(context).getLong(ConstantsLoyalty.COUNT_GIFT) - 1)
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_COUNT_GIFT))
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_POINT))

        btnDefault.setBackgroundResource(backgroundButton)

        imgClose.setOnClickListener {
            dismiss()
        }

        btnDefault.setOnClickListener {
            dismiss()
            onClick(id)
        }

        WidgetHelper.loadImageUrl(imgProduct, image)

        setOnDismissListener {
            onDismiss()
        }
    }

    protected abstract fun onDismiss()

    protected abstract fun onClick(id: Long)
}