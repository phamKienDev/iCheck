package vn.icheck.android.helper

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import vn.icheck.android.R
import vn.icheck.android.util.ick.showSimpleErrorToast
import vn.icheck.android.util.ick.showSimpleSuccessToast
import vn.icheck.android.util.kotlin.ToastUtils
import java.util.regex.Pattern

object ValidHelper {
    fun validNameNetWork(context: Context?, text: String?): String? {
        if (context == null) {
            return ""
        }

        val vietnameseDiacriticCharacters = "àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ"
        val pattern = Pattern.compile("[a-zA-Z $vietnameseDiacriticCharacters]+")

        return if (text.isNullOrEmpty()) {
            context.getString(R.string.vui_long_nhap_du_lieu)
        } else if (pattern.matcher(text).matches()) {
            null
        } else if (text.contains(" ")) {
            context.getString(R.string.ten_mang_khong_dung_dinh_dang)
        } else {
            null
        }
    }

    fun validName(context: Context?, text: String?): String? {
        if (context == null) {
            return ""
        }

        val vietnameseDiacriticCharacters = "àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ"
        val pattern = Pattern.compile("[a-zA-Z $vietnameseDiacriticCharacters]+")

        return if (text.isNullOrEmpty()) {
            context.getString(R.string.ten_khong_duoc_de_trong)
        } else if (pattern.matcher(text).matches()) {
            null
        } else if (text.contains(" ")) {
            context.getString(R.string.ten_khong_dung_dinh_dang)
        } else {
            null
        }
    }

    fun validFirstName(context: Context?, text: String?): String? {
        if (context == null) {
            return ""
        }

        val vietnameseDiacriticCharacters = "àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ"
        val pattern = Pattern.compile("[a-zA-Z $vietnameseDiacriticCharacters]+")

        return if (text.isNullOrEmpty()) {
            context.getString(R.string.vui_long_nhap_du_lieu)
        } else if (pattern.matcher(text).matches()) {
            null
        } else if (text.contains(" ")) {
            context.getString(R.string.ho_khong_dung_dinh_dang)
        } else {
            null
        }
    }

    fun validMiddleName(context: Context?, text: String?): String? {
        if (context == null) {
            return ""
        }

        val vietnameseDiacriticCharacters = "àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ"
        val pattern = Pattern.compile("[a-zA-Z $vietnameseDiacriticCharacters]+")

        return if (text.isNullOrEmpty()) {
            context.getString(R.string.vui_long_nhap_du_lieu)
        } else if (pattern.matcher(text).matches()) {
            null
        } else if (text.contains(" ")) {
            context.getString(R.string.ten_dem_khong_dung_dinh_dang)
        } else {
            null
        }
    }

    fun validLastName(context: Context?, text: String?): String? {
        if (context == null) {
            return ""
        }

        val vietnameseDiacriticCharacters = "àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ"
        val pattern = Pattern.compile("[a-zA-Z $vietnameseDiacriticCharacters]+")

        return if (text.isNullOrEmpty()) {
            context.getString(R.string.vui_long_nhap_du_lieu)
        } else if (pattern.matcher(text).matches()) {
            null
        } else {
            context.getString(R.string.ten_khong_hop_le)
        }
    }

    fun validAddress(context: Context?, text: String?): String? {
        if (context == null) {
            return ""
        }

        val vietnameseDiacriticCharacters = "àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ,./"
        val pattern = Pattern.compile("[a-zA-Z0-9 $vietnameseDiacriticCharacters]+")

        return if (text.isNullOrEmpty()) {
            context.getString(R.string.vui_long_nhap_du_lieu)
        } else if (pattern.matcher(text).matches()) {
            null
        } else if (text.contains(" ")) {
            context.getString(R.string.dia_chi_khong_dung_dinh_dang)
        } else {
            context.getString(R.string.dia_chi_khong_dung_dinh_dang)
        }
    }

    fun validContent(context: Context?, text: String?): String? {
        if (context == null) {
            return ""
        }

        return when {
            text.isNullOrEmpty() -> {
                context.getString(R.string.vui_long_nhap_du_lieu)
            }
            text.startsWith(" ") -> {
                context.getString(R.string.noi_dung_khong_dung_dinh_dang)
            }
            else -> {
                null
            }
        }
    }

    fun validLink(context: Context?, link: String?): String? {
        if (context == null) {
            return ""
        }

        return when {
            link.isNullOrEmpty() -> {
                context.getString(R.string.vui_long_nhap_du_lieu)
            }
            link.contains(" ") -> {
                context.getString(R.string.duong_dan_khong_dung_dinh_dang)
            }
            !Patterns.WEB_URL.matcher(link).matches() -> {
                context.getString(R.string.duong_dan_khong_dung_dinh_dang)
            }
            else -> {
                null
            }
        }
    }

    fun validAllPhoneNumber(context: Context?, phoneNumber: String): String? {
        if (context == null) {
            return ""
        }

        return when {
            phoneNumber.isEmpty() -> {
                context.getString(R.string.vui_long_nhap_du_lieu)
            }
            phoneNumber.contains(" ") -> {
                context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
            }
            !Patterns.PHONE.matcher(phoneNumber).matches() -> {
                context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
            }
            phoneNumber.subSequence(0, 1).equals("0") -> {
                if (phoneNumber.length == 10) {
                    null
                } else {
                    context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
                }
            }
            phoneNumber.subSequence(0, 3).equals("+84") -> {
                if (phoneNumber.length > 12 || phoneNumber.length < 12) {
                    context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
                } else {
                    null
                }
            }
            phoneNumber.subSequence(0, 2).equals("84") -> {
                if (phoneNumber.length == 11) {
                    null
                } else {
                    context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
                }
            }
            phoneNumber.startsWith("0") -> {
                if (phoneNumber.length == 10) {
                    null
                } else {
                    context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
                }
            }
            !phoneNumber.startsWith("0") -> {
                context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang)
            }
            else -> {
                null
            }
        }
    }

    fun validEmail(context: Context?, email: String): String? {
        if (context == null) {
            return ""
        }

        return when {
            email.isEmpty() -> {
                context.getString(R.string.vui_long_nhap_du_lieu)
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                context.getString(R.string.email_khong_dung_dinh_dang)
            }
            email.contains(" ") -> {
                context.getString(R.string.email_khong_dung_dinh_dang)
            }
            else -> {
                null
            }
        }
    }

    fun validOtp(context: Context?, otp: String): String? {
        if (context == null) {
            return ""
        }

        return when {
            otp.isEmpty() -> {
                context.getString(R.string.ma_xac_nhan_khong_duoc_de_trong)
            }
            otp.length != 6 -> {
                context.getString(R.string.ma_xac_nhan_khong_hop_le)
            }
            else -> {
                null
            }
        }
    }

    /**
     * Kiểm tra số điện thoại có đúng định dạng
     *
     * @param context
     * @param phoneNumber
     * @return thông báo lỗi nếu có
     */
    fun validInternationalPhoneNumber(context: Context?, phoneNumber: String): String? {
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
            else -> {
                null
            }
        }
    }

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

    fun checkValidatePhoneNumber(context: Context, phoneNumber: String): Boolean {
        return if (phoneNumber == "") {
            ToastUtils.showShortError(context, context.getString(R.string.so_dien_thoai_khong_duoc_de_trong))
            false
        } else {
            if (phoneNumber.length < 10) {
                ToastUtils.showShortError(context, context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang))
                false
            } else {
                if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                    ToastUtils.showShortError(context, context.getString(R.string.so_dien_thoai_co_chua_ky_tu_dac_biet))
                    false
                } else {
                    if (phoneNumber.subSequence(0, 3) == "+84") {
                        if (phoneNumber.length == 12) {
                            true
                        } else {
                            ToastUtils.showShortError(context, context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang))
                            false
                        }
                    } else {
                        if (phoneNumber.subSequence(0, 2) == "84") {
                            if (phoneNumber.length == 11) {
                                true
                            } else {
                                ToastUtils.showShortError(context, context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang))
                                false
                            }
                        } else {
                            if (phoneNumber.length == 10) {
                                true
                            } else {
                                ToastUtils.showShortError(context, context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang))
                                false
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkValidatePhoneNumberBuyCard(context: Context, phoneNumber: String): Boolean {
        return if (phoneNumber == "") {
            context.showSimpleErrorToast(context.getString(R.string.vui_long_nhap_sdt_ban_muon_nap_the))
            false
        } else {
            if (phoneNumber.length < 10) {
                context.showSimpleErrorToast(context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang))
                false
            } else {
                if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                    context.showSimpleErrorToast(context.getString(R.string.so_dien_thoai_co_chua_ky_tu_dac_biet))
                    false
                } else {
                    if (phoneNumber.subSequence(0, 3) == "+84") {
                        if (phoneNumber.length == 12) {
                            true
                        } else {
                            context.showSimpleErrorToast(context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang))
                            false
                        }
                    } else {
                        if (phoneNumber.subSequence(0, 2) == "84") {
                            if (phoneNumber.length == 11) {
                                true
                            } else {
                                context.showSimpleErrorToast(context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang))
                                false
                            }
                        } else {
                            if (phoneNumber.length == 10) {
                                true
                            } else {
                                context.showSimpleErrorToast(context.getString(R.string.so_dien_thoai_khong_dung_dinh_dang))
                                false
                            }
                        }
                    }
                }
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


    /**
     * Lấy số điện thoại được loại bỏ phone code
     *
     * @param phoneNumber Số điện thoại
     * @return số điện thoại đã được bỏ phone code
     */
    fun getPhoneNumberWithoutCode(phoneNumber: String): String {
        val mPhone = phoneNumber.replace(" ".toRegex(), "")

        return when {
            isPhoneNumberWith0(phoneNumber) -> {
                mPhone.substring(1, phoneNumber.length)
            }
            isPhoneNumberWith84(phoneNumber) -> {
                mPhone.substring(2, phoneNumber.length)
            }
            isPhoneNumberWith840(phoneNumber) -> {
                mPhone.substring(3, phoneNumber.length)
            }
            isPhoneNumberWith_84(phoneNumber) -> {
                mPhone.substring(3, phoneNumber.length)
            }
            isPhoneNumberWith_840(phoneNumber) -> {
                mPhone.substring(3, phoneNumber.length)
            }
            else -> {
                mPhone
            }
        }
    }


    /**
     * Kiểm tra mật khẩu có đúng định dạng
     *
     * @param context
     * @param password
     * @return thông báo lỗi nếu có
     */
    fun validPassword(context: Context?, password: String): String? {
        if (context == null) {
            return ""
        }

        return when {
            password.isEmpty() -> {
                context.getString(R.string.mat_khau_khong_duoc_de_trong)
            }
            password.contains(" ") -> {
                context.getString(R.string.mat_khau_khong_hop_le)
            }
            password.length < 6 -> {
                context.getString(R.string.mat_khau_toi_thieu_la_6_ky_tu)
            }
//            password.length > 20 -> {
//                context.getString(R.string.mat_khau_toi_da_la_20_ky_tu)
//            }
            else -> {
                null
            }
        }
    }

    fun validRePassword(context: Context?, password: String, rePassword: String): String? {
        if (context == null) {
            return ""
        }

        return when {
            rePassword.isEmpty() -> {
                context.getString(R.string.xac_nhan_mat_khau_khong_duoc_de_trong)
            }
            rePassword.contains(" ") -> {
                context.getString(R.string.mat_khau_khong_hop_le)
            }
            !validPassword(rePassword) -> {
                context.getString(R.string.mat_khau_khong_hop_le)
            }
            rePassword.length < 6 -> {
                context.getString(R.string.mat_khau_toi_thieu_la_6_ky_tu)
            }
//            rePassword.length > 20 -> {
//                context.getString(R.string.mat_khau_toi_da_la_20_ky_tu)
//            }
            password != rePassword -> {
                context.getString(R.string.xac_nhan_mat_khau_khong_khop)
            }
            else -> {
                null
            }
        }
    }

    private fun validPassword(password: String): Boolean {
        // ^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).$
        val passwordPattern = Pattern.compile("^[a-zA-Z0-9]*$")
        return !TextUtils.isEmpty(password) && passwordPattern.matcher(password).matches()
    }

    fun validNumber(text: String): Boolean {
        return Pattern.compile("^[0-9]+").matcher(text).matches()
    }
}