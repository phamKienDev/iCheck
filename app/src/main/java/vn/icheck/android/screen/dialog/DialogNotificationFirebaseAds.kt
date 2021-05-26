package vn.icheck.android.screen.dialog

import android.app.Activity
import android.os.Build
import android.webkit.WebSettings
import kotlinx.android.synthetic.main.dialog_notification_firebase.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.chat.icheckchat.base.view.setGoneView
import vn.icheck.android.chat.icheckchat.base.view.setVisible
import vn.icheck.android.ichecklibs.Constant.getHtmlData
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
                WidgetHelper.loadImageUrlRounded10FitCenter(imageView, image)

                imageView.setOnClickListener {
                    dismiss()
                    ICheckApplication.currentActivity()?.let { activity ->
                        FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                    }
                }
            }
            htmlText != null -> {
                textView.setVisible()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webViewHtml.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING;
                } else {
                    webViewHtml.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL;
                }
                webViewHtml.loadDataWithBaseURL(null, getHtmlData(htmlText), "text/html", "utf-8", "")
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