package vn.icheck.android.network.models.pvcombank

data class ICTransactionPVCard(
        val transactions: MutableList<ICItemTransaction>? = null
) {

    data class ICItemTransaction(
            val tranId: String? = null,
            val tranTime: String? = null,
            val amount: String? = null,
            val cardMasking: String? = null,
            val cardId: String? = null,
            val tranDescription: String? = null,
            val currency: String? = null,
            val conAmount: String? = null,
            val tranType: String? = null,
            val tranStatus: String? = null
    )
}

