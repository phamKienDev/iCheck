package vn.icheck.android.ichecklibs.base_dialog

import android.content.Context
import android.os.Handler
import kotlinx.android.synthetic.main.dialog_ads_campaign.*
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.ichecklibs.WidgetHelper

abstract class DialogAdsCampaign(context: Context, private val image: String?) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_ads_campaign
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        WidgetHelper.loadImageUrlRounded(imageView, image, SizeHelper.size10)

        imgClose.setOnClickListener {
            dismiss()
        }

        setOnDismissListener {
            onDismiss()
        }

        Handler().postDelayed({
            dismiss()
        }, 5000)
    }

    protected abstract fun onDismiss()
}