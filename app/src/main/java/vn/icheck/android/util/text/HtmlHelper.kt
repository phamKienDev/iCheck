package vn.icheck.android.util.text

import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.LeadingMarginSpan
import org.xml.sax.XMLReader
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ICheckApplication

object HtmlHelper {

    private val Int.dip: Int
        get() = if (ICheckApplication.currentActivity() != null)
            (this * ICheckApplication.currentActivity()!!.resources.displayMetrics.density).toInt()
        else 2

    @Suppress("DEPRECATION")
    fun AppCompatTextView.setHtmlText(html: String) {
        val htmlSpannable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html, null, LiTagHandler())
        }
        val spannableBuilder = SpannableStringBuilder(htmlSpannable)
        val bulletSpans = spannableBuilder.getSpans(0, spannableBuilder.length, BulletSpan::class.java)
        bulletSpans.forEach {
            val start = spannableBuilder.getSpanStart(it)
            val end = spannableBuilder.getSpanEnd(it)
            spannableBuilder.removeSpan(it)
            spannableBuilder.setSpan(
                    ImprovedBulletSpan(bulletRadius = 3.dip, gapWidth = 8.dip),
                    start,
                    end,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        this.text = spannableBuilder
    }
}
