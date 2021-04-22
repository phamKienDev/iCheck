package vn.icheck.android.screen.dialog

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.text.Html
import kotlinx.android.synthetic.main.dialog_notification_firebase.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.chat.icheckchat.base.view.setGoneView
import vn.icheck.android.chat.icheckchat.base.view.setVisible
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.WidgetHelper
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity

abstract class DialogNotificationFirebaseAds(context: Activity, private val image: String?, private val htmlText: String?, private val link: String?, private val schema: String?) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_notification_firebase
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        setGoneView(imageView, textView, layoutWeb)

        when {
            image != null -> {
                imageView.setVisible()
                WidgetHelper.loadImageUrlRounded(imageView, image, SizeHelper.size10)

                imageView.setOnClickListener {
                    dismiss()
                    ICheckApplication.currentActivity()?.let { activity ->
                        FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                    }
                }
            }
            htmlText != null -> {
                textView.setVisible()
                textView.text = Html.fromHtml(htmlText)
            }
            link != null -> {
                layoutWeb.setVisible()
                webView.loadUrl(link)
            }
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        setOnDismissListener {
            onDismiss()
        }
    }

    protected abstract fun onDismiss()
}