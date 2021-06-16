package vn.icheck.android.chat.icheckchat.network

import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.helper.RStringUtils
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.model.MCResponseCode

private val composite = CompositeDisposable()

private val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()

fun dispose() {
    composite.clear()
}

fun <T: MCResponseCode>requestNewApi(observable: Observable<T>, success: (obj: T) -> Unit, cancel: (MCResponseCode) -> Unit) {
    val disposable = observable
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    {
                        if (it.statusCode == "200" || it.code == 1) {
                            success(it)
                        } else {
                            it.message = it.message ?: checkErrorString(it.statusCode)
                            cancel(it)
                        }
                    },
                    { throws ->
                        val errorBody = MCResponseCode()
                        errorBody.message = RStringUtils.rText(R.string.co_loi_xay_ra_vui_long_thu_lai)

                        when (throws) {
                            is RetrofitExceptionChat -> {
                                val error = parseJson(throws.response?.errorBody()?.string(), MCResponseCode::class.java)

                                if (error != null) {
                                    error.message = checkErrorString(error.statusCode)
                                    return@subscribe
                                }
                            }
                            is HttpException -> {
                                val error = parseJson(throws.response()?.errorBody()?.string(), MCResponseCode::class.java)

                                if (error != null) {
                                    return@subscribe
                                }
                            }
                        }
                        cancel(errorBody)
                    }
            )

    composite.add(disposable)
}

fun <T> parseJson(json: String?, clazz: Class<T>): T? {
    if (json.isNullOrEmpty())
        return null

    return try {
        gson.fromJson(json, clazz)
    } catch (e: Exception) {
        null
    }
}

fun checkErrorString(code: String): String {
    return when (code) {
        "500" -> {
            RStringUtils.rText(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
        "U100" -> {
            RStringUtils.rText(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
        "400" -> {
            RStringUtils.rText(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
        "U102" -> {
            RStringUtils.rText(R.string.token_khong_hop_le_hoac_het_han)
        }
        "U103" -> {
            RStringUtils.rText(R.string.invalid_user_id_on_header)
        }
        "U104" -> {
            RStringUtils.rText(R.string.invalid_user_id_on_header)
        }
        "U105" -> {
            RStringUtils.rText(R.string.this_future_is_disable)
        }
        "U3000" -> {
            RStringUtils.rText(R.string.tai_khoan_da_ton_tai)
        }
        "U3001" -> {
            RStringUtils.rText(R.string.ma_otp_khong_chinh_xac)
        }
        "U3002" -> {
            RStringUtils.rText(R.string.vuot_qua_so_lan_nhap_sai_otp)
        }
        "U3003" -> {
            RStringUtils.rText(R.string.tai_khoan_khong_hop_le)
        }
        "U3004" -> {
            RStringUtils.rText(R.string.khong_tim_thay_tai_khoan)
        }
        "U3005" -> {
            RStringUtils.rText(R.string.mat_khau_khong_chinh_xac)
        }
        "U3006" -> {
            RStringUtils.rText(R.string.mat_khau_khong_chinh_xac)
        }
        "U3007" -> {
            RStringUtils.rText(R.string.sdt_sai_dinh_dang)
        }
        "U3009" -> {
            RStringUtils.rText(R.string.ma_otp_khong_chinh_xac)
        }
        "U3010" -> {
            RStringUtils.rText(R.string.tai_khoan_hoac_mk_khong_chinh_xac)
        }
        "U3011" -> {
            RStringUtils.rText(R.string.tai_khoan_bi_khoa)
        }
        "U3012" -> {
            RStringUtils.rText(R.string.tai_khoan_bi_khoa_tu_social)
        }
        "U3013" -> {
            RStringUtils.rText(R.string.token_facebook_khong_hop_le)
        }
        "U3014" -> {
            RStringUtils.rText(R.string.dang_nhap_lan_dau_bang_fb_vui_long_xac_thuc_sdt)
        }
        "U3015" -> {
            RStringUtils.rText(R.string.mat_khau_khong_dung_dinh_dang)
        }
        "U3016" -> {
            RStringUtils.rText(R.string.tai_khoan_fb_da_dang_ky_he_thong)
        }
        "U3017" -> {
            RStringUtils.rText(R.string.register_request_has_existed)
        }
        "U3018" -> {
            RStringUtils.rText(R.string.da_ton_tai_yeu_cau_quen_mat_khau)
        }
        "U3019" -> {
            RStringUtils.rText(R.string.tai_khoan_da_duoc_xac_thuc_sdt)
        }
        "U3020" -> {
            RStringUtils.rText(R.string.da_ton_tai_yeu_cau_nay)
        }
        "U3031" -> {
            RStringUtils.rText(R.string.sdt_da_duoc_dang_ky)
        }
        "U3032" -> {
            RStringUtils.rText(R.string.client_id_existed)
        }
        "U3033" -> {
            RStringUtils.rText(R.string.client_id_not_existed)
        }
        "U3034" -> {
            RStringUtils.rText(R.string.permission_denied)
        }
        "U3035" -> {
            RStringUtils.rText(R.string.client_id_and_user_not_matching)
        }
        "U3036" -> {
            RStringUtils.rText(R.string.tai_khoan_da_duoc_xac_thuc_email)
        }
        "U3037" -> {
            RStringUtils.rText(R.string.email_nay_da_duoc_xac_thuc_o_tai_khoan_khac)
        }
        "U3038" -> {
            RStringUtils.rText(R.string.token_khong_hop_le)
        }
        "U3041" -> {
            RStringUtils.rText(R.string.email_khong_dung_dinh_dang)
        }
        "U3042" -> {
            RStringUtils.rText(R.string.tai_khoan_da_duoc_cai_dat_mat_khau)
        }
        "U3043" -> {
            RStringUtils.rText(R.string.tai_khoan_chua_duoc_dang_ki_su_dung_voi_ung_dung_nay)
        }
        "U4000" -> {
            RStringUtils.rText(R.string.sms_format_not_found)
        }
        "U4001" -> {
            RStringUtils.rText(R.string.phone_number_max_sms)
        }
        "U4002" -> {
            RStringUtils.rText(R.string.the_minimum_time_between_message_is_not_reached)
        }
        "U4003" -> {
            RStringUtils.rText(R.string.vuot_qua_so_lan_gui_otp)
        }
        "U4004" -> {
            RStringUtils.rText(R.string.co_loi_khi_gui_otp)
        }
        "U4005" -> {
            RStringUtils.rText(R.string.co_loi_khi_gui_email)
        }
        "U5001" -> {
            RStringUtils.rText(R.string.application_or_client_secret_invalid)
        }
        "U5002" -> {
            RStringUtils.rText(R.string.application_permission_denied)
        }
        else -> {
            ""
        }
    }
}