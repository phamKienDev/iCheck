package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_scan_success_congrats.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.helper.WidgetHelper

abstract class DialogSuccessScanGame(context: Context, val title: String, val nameCampaign: String, val nameShop: String, val avatarShop: String) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_scan_success_congrats
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        WidgetHelper.loadImageUrl(imgAvatar, avatarShop)

        tvTitle.text = title
        tvNameCampaign.text = nameCampaign
        tvNameShop.text = nameShop

        btnClose.setOnClickListener {
            dismiss()
        }

        btnScan.setOnClickListener {
            onDone()
        }

        setOnDismissListener {
            onDismiss()
        }
    }

    abstract fun onDone()
    abstract fun onDismiss()
}