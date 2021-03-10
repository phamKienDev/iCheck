package vn.icheck.android.network.models

data class ICPVCombankCard(
	val code: String? = null,
	val data: Data? = null,
	val message: String? = null,
	val verification: Verification? = null,
	val statusCode: Int? = null
)

data class Data(
	val cardMasking: String? = null,
	val cardId: String? = null,
	val exceedLimit: String? = null,
	val embossName: String? = null,
	val avlBalance: String? = null,
	val cardStatus: String? = null,
	val expDate: String? = null
)

data class Verification(
	val bypass: String? = null,
	val otpTransId: String? = null,
	val requestId: String? = null
)

