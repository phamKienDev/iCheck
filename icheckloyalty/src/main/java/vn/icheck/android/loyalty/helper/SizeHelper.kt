package vn.icheck.android.loyalty.helper

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView

internal object SizeHelper {
    val size0 = dpToPx(0)
    val size1 = dpToPx(1)
    val size2 = dpToPx(2)
    val size3 = dpToPx(3)
    val size4 = dpToPx(4)
    val size5 = dpToPx(5)
    val size6 = dpToPx(6)
    val size8 = dpToPx(8)
    val size10 = dpToPx(10)
    val size12 = dpToPx(12)
    val size16 = dpToPx(16)
    val size20 = dpToPx(20)
    val size23 = dpToPx(23)
    val size24 = dpToPx(24)
    val size30 = dpToPx(30)
    val size32 = dpToPx(32)
    val size38 = dpToPx(38)
    val size40 = dpToPx(40)
    val size44 = dpToPx(44)
    val size50 = dpToPx(50)
    val size52 = dpToPx(52)
    val size67 = dpToPx(67)
    val size70 = dpToPx(70)
    val size74 = dpToPx(74)
    val size90 = dpToPx(90)
    val size136 = dpToPx(136)
    val size222 = dpToPx(222)
    val size152 = dpToPx(152)
    val size170 = dpToPx(170)
    val size220 = dpToPx(220)
    val size290 = dpToPx(290)
    val size333 = dpToPx(333)

    /**
     * Lấy kích thước của màn hình
     * @param activity ngữ cảnh
     * @return kết quả
     */
    fun getDisplaySize(activity: Activity): Point {
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    /**
     * Lấy vị trí x, y, width, height của TextView
     * @param textView baseView muốn lấy
     * @return kết quả
     */
    fun getTextViewBounds(textView: TextView): Rect {
        val rect = Rect()
        textView.paint.getTextBounds(textView.text.toString(), 0, textView.text.length, rect)
        return rect
    }

    /**
     * Lấy vị trí x, y, width, height của ImageView
     * @param imageView baseView muốn lấy
     * @return kết quả
     */
    fun getImageViewBounds(imageView: ImageView): Rect {
        return imageView.drawable.bounds
    }


    /**
     * Chuyển dip sang pixels
     * @param context ngữ cảnh
     * @param dp value muốn chuyển
     * @return kết quả pixels
     */
    fun dpToPx(context: Context?, dp: Int): Int {
        if (context == null)
            return 0

        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
    fun dpToPx(dp: Double): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    /**
     * Chuyển pixels sang dip
     * @param context ngữ cảnh
     * @param px value muốn chuyển
     * @return kết quả dip
     */
    fun pxToDp(context: Context, px: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    /**
     * Chuyển sp sang pixels
     * @param context ngữ cảnh
     * @param spValue value muốn chuyển
     * @return kết quả pixels
     */
    fun spToPx(context: Context, spValue: Float): Float {
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, displayMetrics)
    }

    fun dpToSp(dp: Int, context: Context): Int {
        return (dpToPx(context, dp) / context.resources.displayMetrics.scaledDensity).toInt()
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}