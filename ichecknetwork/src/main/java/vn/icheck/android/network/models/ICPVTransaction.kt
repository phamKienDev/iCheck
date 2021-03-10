package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICPVTransaction (
    var tranId: String? = null,
    var tranTime: String? = null,
    var amount: String? = null,
    var cardMasking: String? = null,
    var cardId: String? = null,
    var tranDescription: String? = null,
    var currency: String? = null,
    var conAmount: String? = null,
    var tranType: String? = null,
    var tranStatus: String? = null
)