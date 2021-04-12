package vn.icheck.android.ichecklibs.helper

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import java.text.ParseException
import java.util.*
import java.util.regex.Pattern

object TextHelper {
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

    fun formatMoney(value: Float): String {
        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = '.'
        symbols.groupingSeparator = '.'
        val format = DecimalFormat("###,###,###,###", symbols)
        return format.format(value.toDouble())
    }

    fun formatMoney(value: String): String {
        val clearString = value.replace("[\\,,\\.]".toRegex(), "")
        val valueNumber = parserValueMonneyFomat(clearString)
        return formatMoney(valueNumber)
    }

    fun parserValueMonneyFomat(value: String): Float {
        val format = DecimalFormat("###,###,###,###")
        return try {
            val number = format.parse(value)
            number.toFloat()
        } catch (e: ParseException) {
            0f
        }
    }

    fun unicodeToKoDauLowerCase(text: String?): String {
        if (text.isNullOrEmpty()) {
            return ""
        }

        val nfdNormalizedString = Normalizer.normalize(text.toLowerCase(Locale.getDefault()), Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("").replace("\u0111".toRegex(), "d").replace("\u0110".toRegex(), "D")
    }
}