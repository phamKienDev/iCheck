package vn.icheck.android.helper

import android.graphics.Color
import android.graphics.Typeface
import android.text.Html
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_search_review.*
import kotlinx.android.synthetic.main.activity_search_users.*
import kotlinx.android.synthetic.main.item_product_search_result.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICCountry
import vn.icheck.android.network.models.ICDistrict
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.network.models.ICWard
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import java.text.ParseException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object TextHelper {

    fun convertMtoKm(distance: Long, textView: AppCompatTextView, key: String? = null) {
        if (distance > 999) {
            val value = distance / 1000
            if (!key.isNullOrEmpty()) {
                textView.text = "${key} " + value.toString() + "km"
            } else {
                textView.text = value.toString() + "km"
            }
        } else {
            if (!key.isNullOrEmpty()) {
                textView.text = "${key} " + distance.toString() + "m"
            } else {
                textView.text = distance.toString() + "m"
            }
        }
    }

    fun getFullAddress(address: String?, ward: ICWard?, district: ICDistrict?, province: ICProvince?, country: ICCountry?): String {
        return if (address != null) {
            var mAddress = address

            if (ward != null) {
                mAddress += ", " + ward.name
            }

            if (district != null) {
                mAddress += ", " + district.name
            }

            if (province != null) {
                mAddress += ", " + province.name
            }

            if (country != null) {
                mAddress += ", " + country.name
            }

            mAddress
        } else {
            ""
        }
    }


    /**
     * Bỏ dấu tiếng việt
     * @param text tiếng Việt có dấu
     * @return tiếng Việt không dấu
     */
    fun unicodeToKoDau(text: String?): String {
        if (text.isNullOrEmpty()) {
            return ""
        }

        val nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")

        return pattern.matcher(nfdNormalizedString).replaceAll("").replace("\u0111".toRegex(), "d").replace("\u0110".toRegex(), "D")
    }

    /**
     * Bỏ dấu tiếng việt + chuyển text nhỏ
     * @param text tiếng Việt có dấu
     * @return tiếng Việt không dấu
     */
    fun unicodeToKoDauLowerCase(text: String?): String {
        if (text.isNullOrEmpty()) {
            return ""
        }

        val nfdNormalizedString = Normalizer.normalize(text.toLowerCase(Locale.getDefault()), Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("").replace("\u0111".toRegex(), "d").replace("\u0110".toRegex(), "D")
    }

    fun formatCount(count: Int): String {
        return if (count < 100000) {
            String.format("%,d", count)
        } else if (count < 1000000) {
            String.format("%.1f", count.toDouble() / 1000) + "K"
        } else if (count < 1000000000) {
            String.format("%.1f", count.toDouble() / 1000000) + "TR"
        } else {
            String.format("%.2f", count.toDouble() / 1000000000) + "Tỷ"
        }
    }

    /**
     * Chuyển giá trị sang định dạng tiền dấu ,
     * @param value Float
     * @return String
     */
    fun formatMoneyComma(value: Int): String {
        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','
        symbols.groupingSeparator = ','
        val format = DecimalFormat("###,###,###,###", symbols)
        return format.format(value.toDouble())
    }

    /**
     * Chuyển giá trị sang định dạng tièn
     * @param value Float
     * @return String
     */
    fun formatMoney(value: Float): String {
        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = '.'
        symbols.groupingSeparator = '.'
        val format = DecimalFormat("###,###,###,###", symbols)
        return format.format(value.toDouble())
    }

    fun formatMoneyComma(value: Long): String {
        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','
        symbols.groupingSeparator = ','
        val format = DecimalFormat("###,###,###,###", symbols)
        return format.format(value.toDouble())
    }


    /**
     * Chuyển giá trị sang định dạng tièn
     * @param value Int
     * @return String
     */
    fun formatMoney(value: Int?): String {
        return try {
            val symbols = DecimalFormatSymbols()
            symbols.decimalSeparator = '.'
            symbols.groupingSeparator = '.'
            val format = DecimalFormat("###,###,###,###", symbols)
            format.format(value)
        } catch (e: Exception) {
            "0"
        }
    }

    fun formatMoney(value: Long?): String {
        return try {
            val symbols = DecimalFormatSymbols()
            symbols.decimalSeparator = '.'
            symbols.groupingSeparator = '.'
            val format = DecimalFormat("###,###,###,###", symbols)
            format.format(value)
        } catch (e: Exception) {
            "0"
        }
    }

    fun formatMoneyPhay(value: Long?): String {
        return try {
            val symbols = DecimalFormatSymbols()
            symbols.decimalSeparator = ','
            symbols.groupingSeparator = ','
            val format = DecimalFormat("###,###,###,###", symbols)
            format.format(value)
        } catch (e: Exception) {
            "0"
        }
    }

    fun formatMoneyPhay(value: Int?): String {
        return try {
            val symbols = DecimalFormatSymbols()
            symbols.decimalSeparator = ','
            symbols.groupingSeparator = ','
            val format = DecimalFormat("###,###,###,###", symbols)
            format.format(value)
        } catch (e: Exception) {
            "0"
        }
    }

    /**
     * Chuyển giá trị sang định dạng tiền
     * @param value String
     * @return String
     */
    fun formatMoney(value: String?): String {
        return if (!value.isNullOrEmpty()) {
            val clearString = value.replace("[\\,,\\.]".toRegex(), "")
            formatMoney(parserValueMonneyFomat(clearString))
        } else {
            ""
        }

    }

    /**
     * Chuyển định dạng tiền sang giá trị
     * @param value String
     * @return Float
     */
    fun parserValueMonneyFomat(value: String): Float {
        val format = DecimalFormat("###,###,###,###")
        return try {
            format.parse(value).toFloat()
        } catch (e: ParseException) {
            0f
        }
    }

    fun formatDurationVideo(value: Long): String {
        val string = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(value), TimeUnit.MILLISECONDS.toSeconds(value) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(value)))
        return string
    }

    private fun getDistance(distance: Double): String {
        return if (distance >= 1000) {
            "${String.format("%.1f", (distance / 1000))} km"
        } else {
            "${distance.toInt()} m"
        }
    }

    fun checkStringWithValue(text: String?, value: String): String {
        return if (text.isNullOrEmpty()) {
            value
        } else {
            text
        }
    }

    fun getTextAfterDot(value: Double?): String? {
        val df = DecimalFormat("#.##")
        val format = df.format(value)
        return format.toString()
    }

    fun AppCompatTextView.setTextNameProduct(name: String?) {
        if (name.isNullOrEmpty()) {
            text = ICheckApplication.getInstance().getString(R.string.ten_dang_cap_nhat)
            typeface = ViewHelper.createTypeface(ICheckApplication.getInstance(), R.font.barlow_semi_bold_italic)
            setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.darkGray2))
        } else {
            setText(name)
            typeface = ViewHelper.createTypeface(ICheckApplication.getInstance(), R.font.barlow_medium)
            setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.black_21_v2))
        }
    }

    fun AppCompatTextView.setTextNameProductInPost(name: String?) {
        if (name.isNullOrEmpty()) {
            setText(Html.fromHtml(ICheckApplication.getInstance().getString(R.string.ten_dang_cap_nhat_i)))
            textSize = 14f
            typeface = ViewHelper.createTypeface(ICheckApplication.getInstance(), R.font.barlow_semi_bold_italic)
            setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.darkGray2))
        } else {
            setText(name)
            textSize = 16f
            typeface = ViewHelper.createTypeface(ICheckApplication.getInstance(), R.font.barlow_semi_bold)
            setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.darkGray1))
        }
    }

    fun AppCompatTextView.setTextPriceProduct(price: Long?) {
        if (price == null) {
            typeface = ViewHelper.createTypeface(ICheckApplication.getInstance(), R.font.barlow_semi_bold_italic)
            text = ICheckApplication.getInstance().getString(R.string.gia_dang_cap_nhat)
            setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.darkGray2))
        } else {
            typeface = ViewHelper.createTypeface(ICheckApplication.getInstance(), R.font.barlow_semi_bold)
            setText(ICheckApplication.getInstance().getString(R.string.xxx__d, formatMoneyPhay(price)))
            setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.lightBlue))
        }
    }


    fun AppCompatTextView.setTextEmpitySearch(text: Int) {
        background = ContextCompat.getDrawable(ICheckApplication.getInstance(), R.drawable.bg_corner_gray_4)
        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_bottom_filter_8dp, 0)
        setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.collection_product_name))
        setText(text)
    }

    fun AppCompatTextView.setTextDataSearch(text: String) {
        background = ContextCompat.getDrawable(ICheckApplication.getInstance(), R.drawable.bg_corners_4_light_blue_solid)
        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_filter_white_8dp, 0)
        setTextColor(Color.WHITE)
        setText(text)
    }

    fun AppCompatTextView.setTextChooseSearch(choose: Boolean) {
        if (choose) {
            background = ContextCompat.getDrawable(ICheckApplication.getInstance(), R.drawable.bg_corners_4_light_blue_solid)
            setTextColor(Color.WHITE)
        } else {
            background = ContextCompat.getDrawable(ICheckApplication.getInstance(), R.drawable.bg_corner_gray_4)
            setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.collection_product_name))
        }
    }

}