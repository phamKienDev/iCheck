package vn.icheck.android.loyalty.dialog

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.android.synthetic.main.dialog_exchange_gifts_point_success.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.helper.PointHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.sdk.LoyaltySdk

open class DialogExchangeGiftsPointSuccess(context: Context, private val point: Long?, private val campaignID: Long?, private val backgroundButton: Int) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_exchange_gifts_point_success
    override val getIsCancelable: Boolean
        get() = false

    @SuppressLint("SetTextI18n")
    override fun onInitView() {
        if (campaignID != null) {
            PointHelper.updatePoint(campaignID)
            SharedLoyaltyHelper(context).putLong(ConstantsLoyalty.COUNT_GIFT, SharedLoyaltyHelper(context).getLong(ConstantsLoyalty.COUNT_GIFT) - 1)
        }

        btnDefault.setBackgroundResource(backgroundButton)

        imgClose.setOnClickListener {
            dismiss()
        }

        tvMessage rText R.string.xu_cua_ban_da_duoc_cong_vao_kho
        tvPoint.rText(R.string.s_xu, TextHelper.formatMoneyPhay(point))
        btnDefault.setVisible()

        btnDefault.setOnClickListener {
            dismiss()
            LoyaltySdk.openActivity("point_transitions")
        }

        setOnDismissListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_COUNT_GIFT))
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_POINT))
        }
    }
}