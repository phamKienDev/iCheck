package vn.icheck.android.network.models.pvcombank

import com.google.gson.annotations.Expose

data class ICInfoPVCard(
		@Expose val code: Int? = null,
		@Expose val message: String? = null,
//		@Expose val verification: ICVerificationPVBank? = null,
//		@Expose val card: Card? = null,
		@Expose val fullCard: String? = null
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

