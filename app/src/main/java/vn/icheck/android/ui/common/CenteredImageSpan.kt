package vn.icheck.android.ui.common

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import java.lang.ref.WeakReference

class CenteredImageSpan @JvmOverloads constructor(context: Context, resource: Int, verticalAlignment: Int = ALIGN_BOTTOM) : ImageSpan(context, resource, verticalAlignment) {
    private var mDrawableRef: WeakReference<Drawable>? = null

    // Extra variables used to redefine the Font Metrics when an ImageSpan is added
    private var initialDescent = 0
    private var extraSpace = 0

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val d = getCachedDrawable()
        val rect: Rect = d!!.bounds

//        if (fm != null) {
//            Paint.FontMetricsInt pfm = paint.getFontMetricsInt();
//            // keep it the same as paint's fm
//            fm.ascent = pfm.ascent;
//            fm.descent = pfm.descent;
//            fm.top = pfm.top;
//            fm.bottom = pfm.bottom;
//        }
        if (fm != null) {
            // Centers the text with the ImageSpan
            if (rect.bottom - (fm.descent - fm.ascent) >= 0) {
                // Stores the initial descent and computes the margin available
                initialDescent = fm.descent
                extraSpace = rect.bottom - (fm.descent - fm.ascent)
            }
            fm.descent = extraSpace / 2 + initialDescent
            fm.bottom = fm.descent
            fm.ascent = -rect.bottom + fm.descent
            fm.top = fm.ascent
        }
        return rect.right
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val b = getCachedDrawable()
        canvas.save()

//        int drawableHeight = b.getIntrinsicHeight();
//        int fontAscent = paint.getFontMetricsInt().ascent;
//        int fontDescent = paint.getFontMetricsInt().descent;
//        int transY = bottom - b.getBounds().bottom +  // align bottom to bottom
//                (drawableHeight - fontDescent + fontAscent) / 2;  // align center to center


//        int drawableHeight = b.getIntrinsicHeight();
//        int fontAscent = paint.getFontMetricsInt().ascent;
//        int fontDescent = paint.getFontMetricsInt().descent;
//        int transY = bottom - b.getBounds().bottom +  // align bottom to bottom
//                (drawableHeight - fontDescent + fontAscent) / 2;  // align center to center
        var transY = R.attr.bottom - b!!.bounds.bottom
        // this is the key
        // this is the key
        transY -= paint.fontMetricsInt.descent / 2 - 8

//        int bCenter = b.getIntrinsicHeight() / 2;
//        int fontTop = paint.getFontMetricsInt().top;
//        int fontBottom = paint.getFontMetricsInt().bottom;
//        int transY = (bottom - b.getBounds().bottom) -
//                (((fontBottom - fontTop) / 2) - bCenter);


//        int bCenter = b.getIntrinsicHeight() / 2;
//        int fontTop = paint.getFontMetricsInt().top;
//        int fontBottom = paint.getFontMetricsInt().bottom;
//        int transY = (bottom - b.getBounds().bottom) -
//                (((fontBottom - fontTop) / 2) - bCenter);
        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }

    // Redefined locally because it is a private member from DynamicDrawableSpan
    private fun getCachedDrawable(): Drawable? {
        val wr = mDrawableRef
        var d: Drawable? = null
        if (wr != null) d = wr.get()
        if (d == null) {
            d = drawable
            mDrawableRef = WeakReference(d)
        }
        return d
    }
}