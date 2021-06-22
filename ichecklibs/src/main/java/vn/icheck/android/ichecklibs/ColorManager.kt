package vn.icheck.android.ichecklibs

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.core.content.ContextCompat

object ColorManager {

    fun saveColor(context: Context, key: String, value: String?) {
        val sharedPreferences = context.getSharedPreferences("app_color", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        edit.putString(key, value ?: "")
        edit.apply()
    }

    fun getColor(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences("app_color", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "") ?: ""
    }

    /*
    * Color Primary
    */
    fun getPrimaryColor(context: Context): Int {
        val primaryColor = getColor(context, Constant.PRIMARY_COLOR)
        return if (primaryColor.isNotEmpty()) {
            Color.parseColor(primaryColor)
        } else {
            ContextCompat.getColor(context, R.color.colorPrimary)
        }
    }

    fun getPrimaryColorCode(context: Context): String {
        val primaryColor = getColor(context, Constant.PRIMARY_COLOR)
        return if (primaryColor.isNotEmpty()) {
            primaryColor
        } else {
            "#057DDA"
        }
    }

    /*
    * Color secondary
    */
    fun getSecondaryColor(context: Context): Int {
        val secondaryColor = getColor(context, Constant.SECONDARY_COLOR)
        return if (secondaryColor.isNotEmpty()) {
            Color.parseColor(secondaryColor)
        } else {
            ContextCompat.getColor(context, R.color.colorSecondary)
        }
    }

    fun getSecondaryColorCode(context: Context): String {
        val secondaryColor = getColor(context, Constant.SECONDARY_COLOR)
        return if (secondaryColor.isNotEmpty()) {
            secondaryColor
        } else {
            "#3C5A99"
        }
    }


    /*
   *Color accent blue
   * * */

    fun getAccentBlueColor(context: Context) = ContextCompat.getColor(context, R.color.colorAccentBlue)

    const val getAccentBlueCode = "#00BAF2"


    /*
    *Color accent green
    * * */

    fun getAccentGreenColor(context: Context) = ContextCompat.getColor(context, R.color.colorAccentGreen)

    val getAccentGreenCode = "#85c440"


    /*
    *Color accent red
    * * */

    fun getAccentRedColor(context: Context) = ContextCompat.getColor(context, R.color.colorAccentRed)

    const val getAccentRedCode = "#ff0000"

    /*
    *Color accent yellow
    * * */

    fun getAccentYellowColor(context: Context) = ContextCompat.getColor(context, R.color.colorAccentYellow)

    const val getAccentYellowCode = "#FFB800"

    /*
    *Color accent cyan
    * * */

    fun getAccentCyanColor(context: Context) = ContextCompat.getColor(context, R.color.colorAccentCyan)

    const val getAccentCyanCode = "#CCF1FC"

    /*
    * Color normal text
    */
    fun getNormalTextColor(context: Context): Int {
        val normalTextColor = getColor(context, Constant.NORMAL_TEXT_COLOR)
        return if (normalTextColor.isNotEmpty()) {
            Color.parseColor(normalTextColor)
        } else {
            ContextCompat.getColor(context, R.color.colorNormalText)
        }
    }

    fun getNormalTextCode(context: Context): String {
        val normalTextColor = getColor(context, Constant.NORMAL_TEXT_COLOR)
        return if (normalTextColor.isNotEmpty()) {
            normalTextColor
        } else {
            "#212121"
        }
    }

    /*
    * Color second text
    */
    fun getSecondTextColor(context: Context): Int {
        val secondTextColor = getColor(context, Constant.SECOND_TEXT_COLOR)
        return if (secondTextColor.isNotEmpty()) {
            Color.parseColor(secondTextColor)
        } else {
            ContextCompat.getColor(context, R.color.colorSecondText)
        }
    }

    fun getSecondTextCode(context: Context): String {
        val secondTextColor = getColor(context, Constant.SECOND_TEXT_COLOR)
        return if (secondTextColor.isNotEmpty()) {
            secondTextColor
        } else {
            "#757575"
        }
    }

    /*
    * Color Disable Text
    */
    fun getDisableTextColor(context: Context): Int {
        val disableTextColor = getColor(context, Constant.DISABLE_TEXT_COLOR)
        return if (disableTextColor.isNotEmpty()) {
            Color.parseColor(disableTextColor)
        } else {
            ContextCompat.getColor(context, R.color.colorDisableText)
        }
    }

    fun getDisableTextCode(context: Context): String {
        val disableTextColor = getColor(context, Constant.DISABLE_TEXT_COLOR)
        return if (disableTextColor.isNotEmpty()) {
            disableTextColor
        } else {
            "#B4B4B4"
        }
    }


    /*
    * Color line
    */
    fun getLineColor(context: Context): Int {
        val lineColor = getColor(context, Constant.LINE_COLOR)

        return if (lineColor.isNotEmpty()) {
            Color.parseColor(lineColor)
        } else {
            ContextCompat.getColor(context, R.color.colorLine)
        }
    }

    fun getLineColorCode(context: Context): String {
        val lineColor = getColor(context, Constant.LINE_COLOR)

        return if (lineColor.isNotEmpty()) {
           lineColor
        } else {
            "#D8D8D8"
        }
    }

    /*
    * Color background White
    */
    fun getAppBackgroundWhiteColor(context: Context) = ContextCompat.getColor(context, R.color.colorBackgroundWhite)

    const val getAppBackgroundWhiteColorCode = "#FFFFFF"

    /*
    * Color background Gray
    */
    fun getAppBackgroundGrayColor(context: Context): Int {
        val appBackground = getColor(context, Constant.APP_BACKGROUND_COLOR)
        return if (appBackground.isNotEmpty()) {
            Color.parseColor(appBackground)
        } else {
            ContextCompat.getColor(context, R.color.colorBackgroundGray)
        }
    }

    fun getAppBackgroundGrayCode(context: Context): String {
        val appBackground = getColor(context, Constant.APP_BACKGROUND_COLOR)
        return if (appBackground.isNotEmpty()) {
          appBackground
        } else {
            "#F0F0F0"
        }
    }


    /*
    * Color background Popup
    */
    fun getBackgroundPopupColor(context: Context): Int {
        val popupBackground = getColor(context, Constant.POPUP_BACKGROUND_COLOR)
        return if (popupBackground.isNotEmpty()) {
            Color.parseColor(popupBackground)
        } else {
            ContextCompat.getColor(context, R.color.colorBackgroundPopup)
        }
    }

    fun getBackgroundPopupCode(context: Context): String {
        val popupBackground = getColor(context, Constant.POPUP_BACKGROUND_COLOR)
        return if (popupBackground.isNotEmpty()) {
           popupBackground
        } else {
            "#80000000"
        }
    }
}