package vn.icheck.android.screen.user.listnotification.productnotice

import android.content.Context
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.dialog_product_notice_option.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.util.kotlin.WidgetUtils

abstract class ProductNoticeOptionDialog(context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_product_notice_option, true) {

    fun show(obj: ICNotification) {
        WidgetUtils.loadImageUrl(dialog.imgAvatar, obj.sourceUser?.firstOrNull()?.avatar, R.drawable.img_default_business_logo)

        val name = obj.sourceUser?.firstOrNull()?.getName
        dialog.tvTitle.text = Html.fromHtml(dialog.context.getString(R.string.html_bold_xxx_xxx, name, obj.description))

        dialog.layoutContent.apply {
            getChildAt(0).apply {
                if (obj.status == 1) {
                    visibility = View.GONE
                }

                setOnClickListener {
                    dialog.dismiss()
                    onRead()
                }
            }

            getChildAt(1).setOnClickListener {
                dialog.dismiss()
                onRemove()
            }
        }

        dialog.show()
    }

    protected abstract fun onRead()
    protected abstract fun onRemove()
}