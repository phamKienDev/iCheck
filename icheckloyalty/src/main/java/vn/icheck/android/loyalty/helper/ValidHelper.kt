package vn.icheck.android.loyalty.helper

import android.content.Context
import android.util.Patterns
import vn.icheck.android.loyalty.R

object ValidHelper {

    fun validPhoneNumber(context: Context?, phoneNumber: String): String? {
        if (context == null) {
            return ""
        }

        return when {
            phoneNumber.isEmpty() -> {
                context.getString(R.string.so_dien_thoai_khong_duoc_de_trong)
            }
            !Patterns.PHONE.matcher(phoneNumber).matches() -> {
                context.getString(R.string.so_dien_thoai_co_chua_ky_tu_dac_biet)
            }
            !isPhoneNumber(phoneNumber) -> {
                context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
            }
            else -> {
                null
            }
        }
    }

    private fun isPhoneNumber(phoneNumber: String): Boolean {
        return if (phoneNumber.length < 10 || phoneNumber.contains(" ")) {
            false
        } else {
            isPhoneNumberWith0(phoneNumber) ||
                    isPhoneNumberWith84(phoneNumber) ||
                    isPhoneNumberWith840(phoneNumber) ||
                    isPhoneNumberWith_84(phoneNumber) ||
                    isPhoneNumberWith_840(phoneNumber)
        }
    }

    private fun isPhoneNumberWith0(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && phoneNumber.subSequence(0, 1) == "0"
    }

    private fun isPhoneNumberWith84(phoneNumber: String): Boolean {
        return (phoneNumber.length == 11 && phoneNumber.subSequence(0, 2) == "84")
    }

    private fun isPhoneNumberWith840(phoneNumber: String): Boolean {
        return (phoneNumber.length == 12 && phoneNumber.subSequence(0, 3) == "840")
    }

    private fun isPhoneNumberWith_84(phoneNumber: String): Boolean {
        return (phoneNumber.length == 12 && phoneNumber.subSequence(0, 3) == "+84")
    }

    private fun isPhoneNumberWith_840(phoneNumber: String): Boolean {
        return (phoneNumber.length == 13 && phoneNumber.subSequence(0, 4) == "+840")
    }
}