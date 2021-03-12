package vn.icheck.android.network.models.pvcombank

data class ICInfoPVCard(
		val code: Int? = null,
		val message: String? = null,
		val verification: ICVerificationPVBank? = null,
		val card: Card? = null
){
	data class Card(
			val cardMasking: String? = null,
			val cardId: String? = null,
			val exceedLimit: Int? = null,
			val embossName: String? = null,
			val avlBalance: Int? = null,
			val cardStatus: String? = null,
			val expDate: String? = null
	)
}

