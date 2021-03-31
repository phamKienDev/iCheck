package vn.icheck.android.model.icklogin

import android.os.Build
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.icheck.android.network.models.*


@Parcelize
data class IckUserInfoResponse(

        @field:SerializedName("data")
        val data: IckUserInfoData? = null,

        @field:SerializedName("statusCode")
        val statusCode: String? = null
) : Parcelable

@Parcelize
data class UserPrivacyConfig(

        @field:SerializedName("WHO_VIEW_YOUR_INFO")
        val whoViewYourInfo: String? = null,

        @field:SerializedName("WHO_INVITE_FRIEND")
        val whoInviteFriend: String? = null,

        @field:SerializedName("WHO_VIEW_YOUR_POST")
        val whoViewYourPost: String? = null,

        @field:SerializedName("WHO_COMMENT_YOUR_POST")
        val whoCommentYourPost: String? = null,

        @field:SerializedName("WHO_POST_YOUR_WALL")
        val whoPostYourWall: String? = null
) : Parcelable

@Parcelize
data class Wall(

        @field:SerializedName("cover")
        val cover: String? = null,

        @field:SerializedName("verifyStatus")
        val verifyStatus: Int? = null,

        @field:SerializedName("followedUserCount")
        var followedUserCount: Long? = null,

        @field:SerializedName("followingUserCount")
        var followingUserCount: Long? = null,

        @field:SerializedName("followingPageCount")
        val followingPageCount: Int? = null,

        @field:SerializedName("id")
        val id: Int? = null
) : Parcelable

@Parcelize
data class Title(

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null
) : Parcelable

data class Role(

        @field:SerializedName("code")
        val code: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null
)

@Parcelize
data class Rank(

        @field:SerializedName("score")
        val score: Int? = null,

        @field:SerializedName("level")
        val level: Int? = null
) : Parcelable

@Parcelize
data class IckUserInfoData @JvmOverloads constructor(

        @field:SerializedName("lastName")
        val lastName: String? = null,

        @field:SerializedName("infoPrivacyConfig")
        val infoPrivacyConfig: InfoPrivacyConfig? = null,

//        @field:SerializedName("role")
//        val role: Role? = null,

        @field:SerializedName("gender")
        val gender: Int? = null,

        @field:SerializedName("city")
        val city: City? = null,

        @field:SerializedName("titleId")
        val titleId: Int? = null,

        @field:SerializedName("cityId")
        val cityId: Int? = null,

        @field:SerializedName("ward")
        val ward: Ward? = null,

        @field:SerializedName("title")
        val title: Title? = null,

        @field:SerializedName("createdAt")
        val createdAt: String? = null,

        @field:SerializedName("rank")
        val rank: ICRankOfUser? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("updatedAt")
        val updatedAt: String? = null,

        @field:SerializedName("icheckId")
        val icheckId: Int? = null,

        @field:SerializedName("birthday")
        val birthDay: String? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("roleId")
        val roleId: Int? = null,

        @field:SerializedName("phoneVerified")
        val phoneVerified: Int? = null,

        @field:SerializedName("accountType")
        val accountType: Int? = null,

        @field:SerializedName("type")
        var type: Int? = null,

        @field:SerializedName("avatar")
        val avatar: String? = null,

        @field:SerializedName("wardId")
        val wardId: Int? = null,

        @field:SerializedName("firstName")
        val firstName: String? = null,

        @field:SerializedName("emailVerified")
        val emailVerified: Int? = null,

        @field:SerializedName("districtId")
        val districtId: Int? = null,

        @field:SerializedName("icheckStatus")
        val icheckStatus: Int? = null,

        @field:SerializedName("userPrivacyConfig")
        val userPrivacyConfig: UserPrivacyConfig? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("background")
        val background: String? = null,

        @field:SerializedName("district")
        val district: District? = null,

        @field:SerializedName("wall")
        val wall: Wall? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("hasPassword")
        val hasPassword:Boolean? = null,

        @field:SerializedName("linkedFbId")
        val linkedFbId:String? = null,

        @field:SerializedName("myFollowingUserCount")
        val myFollowingUserCount:Int? = null,

        @field:SerializedName("userFollowingMeCount")
        val userFollowingMeCount:Int? = null,

        @Expose var kycStatus: Int? = null // 0= chưa gửi kyc, 1=đã gửi kyc, 2=kyc đã verify, 3=kyc bị từ chối

) : Parcelable {

    var totalFollow = 0L
    var totalFollowed = 0L

    fun getName(): String {
        val replace = ("${lastName ?: ""} ${firstName ?: ""}")
        return if (!replace.trim().isEmpty()) {
            replace
        } else {
            getPhoneOnly()
        }
    }

    fun getPhoneOnly(): String {
        return if (phone != null) {
            try {
                StringBuilder(phone).apply {
                    replace(0, 2, "0").replace(7, length, " ***")
                            .insert(4, " ")
                }.toString()
            } catch (e: Exception) {
                Build.MODEL
            }
        } else {
            Build.MODEL
        }
    }

    fun createICUser(): ICUser {
        return ICUser().also { icUser ->
            if (id != null) {
                icUser.id = id
            }
            icUser.name = lastName + firstName
            icUser.first_name = firstName
            icUser.address = address
            icUser.last_name = lastName
            icUser.phone = phone
            icUser.avatar = avatar
            icUser.email = email
            icUser.bd = birthDay
            icUser.city = ICProvince()
            icUser.gender = when (gender) {
                1 -> "Nam"
                2 -> "Nữ"
                3 -> "Khác"
                else -> null
            }
            if (city?.name != null) {
                icUser.city?.name = city.name
            }
            if (city?.id != null) {
                icUser.city?.id = city.id.toLong()
            }

            icUser.district = ICDistrict()
            if (district?.name != null) {
                icUser.district?.name = district.name
            }
            if (district?.id != null) {
                icUser.district?.id = district.id.toInt()
            }
            icUser.ward = ICWard()
            if (ward?.name != null) {
                icUser.ward?.name = ward.name
            }
            if (ward?.id != null) {
                icUser.ward?.id = ward.id
            }
            icUser.rank = this.rank
            icUser.type = this.type ?: 1
            icUser.background = this.background
            icUser.hasPassword = this.hasPassword
            icUser.linkedFbId = this.linkedFbId
        }
    }
}

@Parcelize
data class Ward(
        @Expose
        val id: Int?,
        @Expose
        val name: String?
) : Parcelable

@Parcelize
data class District(
        @Expose
        val id: Int?,
        @Expose
        val name: String?
) : Parcelable

@Parcelize
data class City(
        @Expose
        val id: Int?,
        @Expose
        val name: String?
) : Parcelable

@Parcelize
data class InfoPrivacyConfig(

        @field:SerializedName("CITY")
        val city: Boolean? = null,

        @field:SerializedName("PHONE")
        val phone: Boolean? = null,

        @field:SerializedName("GENDER")
        val gender: Boolean? = null,

        @field:SerializedName("EMAIL")
        val email: Boolean? = null,

        @field:SerializedName("BIRTHDAY")
        val birthday: Boolean? = null
) : Parcelable
