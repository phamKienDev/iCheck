package vn.icheck.android.screen.dialog

import android.content.Context
import android.content.DialogInterface
import android.text.Html
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.R
import vn.icheck.android.component.popup_view.BasePopupView
import vn.icheck.android.ichecklibs.ViewHelper

abstract class ScanBuyPopUp(context: Context, private val isCancelable: Boolean) : BasePopupView(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_no_shops_around

    override val getIsCancelable: Boolean
        get() = isCancelable

    override fun onInitView() {

        findViewById<LinearLayout>(R.id.linearLayout)?.run {
            background=ViewHelper.bgWhiteRadius10(context)
        }
        findViewById<AppCompatTextView>(R.id.txtContent)?.run {
            text = Html.fromHtml(context.getString(R.string.hay_tu_ban_nhung_san_pham_tot_nhat))
        }

        findViewById<AppCompatTextView>(R.id.txtChat)?.run {
            setOnClickListener {
                dismiss()
                onClickChat()
            }
        }

        findViewById<AppCompatImageView>(R.id.imgSeller)?.run {
            setImageResource(R.drawable.ic_seller_google_play)
            setOnClickListener {
                dismiss()
                onClickSeller()
            }
        }
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        onClose()
    }

    protected abstract fun onClose()
    protected abstract fun onClickSeller()
    protected abstract fun onClickChat()
}