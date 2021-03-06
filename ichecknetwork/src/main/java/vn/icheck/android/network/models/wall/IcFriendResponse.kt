package vn.icheck.android.network.models.wall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.R
import vn.icheck.android.network.models.ICRankOfUser

data class IcFriendResponse(

	@field:SerializedName("data")
	val data: IcFriendData? = null
){
	var type = 1
}

data class IcFriendData(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("rows")
	val rows: MutableList<RowsItem>? = null
)

data class RowsItem(

	@field:SerializedName("icheckId")
	val icheckId: Int? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("roleId")
	val roleId: Int? = null,

	@field:SerializedName("titleId")
	val titleId: Int? = null,

	@field:SerializedName("avatar")
	val avatar: String? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("deletedAt")
	val deletedAt: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("rank")
	val rank: ICRankOfUser? = null,

	@field:SerializedName("id")
	val id: Long? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@Expose val kycStatus: Int? = null // 0= chưa gửi kyc, 1=đã gửi kyc, 2=kyc đã verify, 3=kyc bị từ chối
){
	fun getPhoneOnly(): String {
		return if (phone != null) {
			StringBuilder(phone!!).apply {
				replace(0, 2, "0").replace(7, length, " ***")
						.insert(4, " ")
			}.toString()
		} else {
			getString(R.string.chua_cap_nhat)
		}
	}

	val getName: String
		get() {
			val n = "${lastName ?: ""} ${firstName ?: ""}"
			return if (n.trim().isNotEmpty()) {
				n
			}else if (!phone?.trim().isNullOrEmpty()) {
				getPhoneOnly()
			} else {
				getString(R.string.chua_cap_nhat)
			}
		}
}
