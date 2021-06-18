package vn.icheck.android.loyalty.dialog

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.android.synthetic.main.dialog_guide_play_game.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.sdk.CampaignType

abstract class DialogGuidePlayGame(context: Context) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_guide_play_game
    override val getIsCancelable: Boolean
        get() = false

    @SuppressLint("SetTextI18n")
    override fun onInitView() {
        tvMessage.text = if (SharedLoyaltyHelper(context).getBoolean(CampaignType.ACCUMULATE_LONG_TERM_POINT_QR_MAR)){
            "Quét mã vạch được dán\ntrên bao bì sản phẩm để nhận thêm lượt\nquay nhé!"
        }else{
            "Quét tem QRcode được dán\ntrên bao bì sản phẩm để nhận thêm lượt\nquay nhé!"
        }
        imgClose.setOnClickListener {
            dismiss()
        }

        btnScanNow.setOnClickListener {
            dismiss()
            onClick()
        }
    }

    abstract fun onClick()
}