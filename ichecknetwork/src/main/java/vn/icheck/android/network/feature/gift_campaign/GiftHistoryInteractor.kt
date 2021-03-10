package vn.icheck.android.network.feature.gift_campaign

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICGiftReceived

class GiftHistoryInteractor:BaseInteractor() {

    fun getGiftReceived(offset: Int, limit: Int,listener:ICNewApiListener<ICResponse<ICListResponse<ICGiftReceived>>>) {
        val body = hashMapOf<String, Any>()
        body["limit"] = limit
        body["offset"] = offset

        requestNewApi(ICNetworkClient.getSocialApi().getGiftReceived(body),listener)
    }
}