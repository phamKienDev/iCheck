package vn.icheck.android.ichecklibs

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue

object SizeHelper {

    val size0_5 = dpToPx(0.5)
    val size0_1 = dpToPx(0.1)
    val size1 = dpToPx(1)
    val size1_5 = dpToPx(1.5)
    val size2 = dpToPx(2)
    val size3 = dpToPx(3)
    val size4 = dpToPx(4)
    val size5 = dpToPx(5)
    val size6 = dpToPx(6)
    val size7 = dpToPx(7)
    val size8 = dpToPx(8)
    val size9 = dpToPx(9)
    val size10 = dpToPx(10)
    val size11dot5 = dpToPx(11.5)
    val size12 = dpToPx(12)
    val size13 = dpToPx(13)
    val size14 = dpToPx(14)
    val size16 = dpToPx(16)
    val size17 = dpToPx(17)
    val size18 = dpToPx(18)
    val size20 = dpToPx(20)
    val size22 = dpToPx(22)
    val size23 = dpToPx(23)
    val size24 = dpToPx(24)
    val size25 = dpToPx(25)
    val size26 = dpToPx(26)
    val size27 = dpToPx(27)
    val size28 = dpToPx(28)
    val size30 = dpToPx(30)
    val size32 = dpToPx(32)
    val size35 = dpToPx(35)
    val size36 = dpToPx(36)
    val size37 = dpToPx(37)
    val size38 = dpToPx(38)
    val size40 = dpToPx(40)
    val size42 = dpToPx(42)
    val size44 = dpToPx(44)
    val size45 = dpToPx(45)
    val size46 = dpToPx(46)
    val size50 = dpToPx(50)
    val size52 = dpToPx(52)
    val size60 = dpToPx(60)
    val size64 = dpToPx(64)
    val size66 = dpToPx(66)
    val size67 = dpToPx(67)
    val size70 = dpToPx(70)
    val size74 = dpToPx(74)
    val size80 = dpToPx(80)
    val size84 = dpToPx(84)
    val size88 = dpToPx(88)
    val size90 = dpToPx(90)
    val size100 = dpToPx(100)
    val size132 = dpToPx(132)
    val size140 = dpToPx(140)
    val size150 = dpToPx(150)
    val size152 = dpToPx(152)
    val size170 = dpToPx(170)
    val size180 = dpToPx(180)
    val size200 = dpToPx(200)
    val size259 = dpToPx(259)
    val size290 = dpToPx(290)
    val size220 = dpToPx(220)
    val size120 = dpToPx(120)
    val size246 = dpToPx(246)
    val size280 = dpToPx(280)
    val size331 = dpToPx(331)
    val size400 = dpToPx(400)

    val size16sp = spToPx(16f)

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
     * Chuyển sp sang pixels
     * @param context ngữ cảnh
     * @param spValue value muốn chuyển
     * @return kết quả pixels
     */
    fun spToPx(spValue: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, Resources.getSystem().displayMetrics)
    }
}