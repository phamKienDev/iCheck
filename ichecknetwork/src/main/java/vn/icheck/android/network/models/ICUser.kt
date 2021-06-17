package vn.icheck.android.network.models

import android.content.Context
import android.os.Build
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.ichecklibs.util.RStringUtils
import vn.icheck.android.network.R
import java.io.Serializable

class ICUser : Serializable {
    @Expose
    var id: Long = 0

    @Expose
    var name: String? = null

    @Expose
    var email: String? = null

    @Expose
    var phone: String? = null

    @Expose
    var locale: String? = null

    @Expose
    var timezone: String? = null

    @Expose
    var avatar: String? = null

    @Expose
    var cover: String? = null

    @Expose
    var gender: String? = null

    @Expose
    var type: Int? = null

    @Expose
    var address: String? = null

    @Expose
    var city: ICProvince? = null

    @Expose
    var district: ICDistrict? = null

    @Expose
    var country: ICCountry? = null

    @Expose
    var publish_fields: Array<String>? = null

    @Expose
    var level: Int = 0

    @Expose
    var first_name: String? = null

    @Expose
    var last_name: String? = null

    @Expose
    var follower_count: Int = 0

    @Expose
    var following_count: Int = 0

    @Expose
    var user_following: Boolean = false

    @Expose
    var user_followed: Boolean = false

    @Expose
    var phone_verified: Boolean = false

    @Expose
    var email_verified: Boolean = false

    @Expose
    var blocked_reason: String? = null

    @Expose
    var birth_day: Int? = null

    @Expose
    var bd: String? = null

    @Expose
    var birth_month: Int? = null

    @Expose
    var birth_year: Int? = null

    @Expose
    var created_at: String? = null

    @Expose
    var updated_at: String? = null

    @Expose
    var city_id: Int? = null

    @Expose
    var district_id: Int? = null

    @Expose
    var country_id: Int? = null

    @Expose
    var avatar_thumbnails: ICThumbnail? = null

    @Expose
    var cover_thumbnails: ICThumbnail? = null

    @Expose
    var ward_id: Long? = null

    @Expose
    var ward: ICWard? = null

    @Expose
    var blocked: Boolean = false

    @Expose
    var linked_facebook: String? = null

    @Expose
    var device_id: String? = null

    @Expose
    var firstName: String? = null

    @Expose
    var lastName: String? = null

    @Expose
    var rank: ICRankOfUser? = null

    @SerializedName("birthday")
    @Expose
    var birthday: String? = null

    var selected: Boolean = false

    var icheckId: Long? = null

    var titleId: Long? = null

    var facebookType: Boolean = false

    var background: String? = null

    var coin: Long? = null

    var hasPassword: Boolean? = null
    var linkedFbId: String? = null

    @SerializedName("entity")
    @Expose
    val entity: String? = null
    @Expose var kycStatus: Int? = null

    val getName: String
        get() {
            val n = "${last_name ?: lastName ?: ""} ${first_name ?: firstName ?: ""}"
            return if (n.trim().isNotEmpty()) {
                n
            } else if (!phone?.trim().isNullOrEmpty()) {
                getPhoneOnly()
            } else {
                RStringUtils.rText(R.string.chua_cap_nhat)
            }
        }

    val getNamePVCombank: String
        get() {
            val n = "${last_name ?: lastName ?: ""} ${first_name ?: firstName ?: ""}"
            return if (n.trim().isNotEmpty()) {
                n
            } else if (!phone?.trim().isNullOrEmpty()) {
                getPhonePVCombank()
            } else {
                RStringUtils.rText(R.string.chua_cap_nhat)
            }
        }

    fun getPhoneAndRank(): String {
        return if (phone != null) {
            StringBuilder(phone!!).apply {
                append(" | ${getUserLevelName()}").replace(0, 2, "0").replace(7, 10, " ***")
                        .insert(4, " ")

            }.toString()
        } else {
            RStringUtils.rText(R.string.chua_cap_nhat)
        }
    }

    fun getPhoneOnly(): String {
        return if (phone != null) {
            try {
                StringBuilder(phone!!).apply {
                    replace(0, 2, "0").replace(7, length, " ***")
                            .insert(4, " ")
                }.toString()
            } catch (e: Exception) {
                RStringUtils.rText(R.string.chua_cap_nhat)
            }
        } else {
            RStringUtils.rText(R.string.chua_cap_nhat)
        }
    }

    fun getPhonePVCombank(): String {
        return if (phone != null) {
            phone.toString()
        } else {
            RStringUtils.rText(R.string.chua_cap_nhat)
        }
    }

    fun getUserLevelName(): String {
        return when (rank?.level) {
            2 -> {
                RStringUtils.rText(R.string.thanh_vien_bac)
            }
            3 -> {
                RStringUtils.rText(R.string.thanh_vien_vang)
            }
            4 -> {
                RStringUtils.rText(R.string.thanh_vien_kim_cuong)
            }
            else -> {
                RStringUtils.rText(R.string.thanh_vien_chuan)
            }
        }
    }
}