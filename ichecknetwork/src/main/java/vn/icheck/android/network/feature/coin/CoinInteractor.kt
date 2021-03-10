package vn.icheck.android.network.feature.coin

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICCoinHistory

class CoinInteractor : BaseInteractor() {

    fun getCointHistory(offset: Int, limit: Int, type: Int, beginAt: String?, endAt: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICCoinHistory>>>) {
        val params = hashMapOf<String, Any?>()
        params["offset"] = offset
        params["limit"] = limit
        params["type"] = type
        if (!beginAt.isNullOrEmpty()) {
            params["beginTime"] = beginAt
        }
        if (!endAt.isNullOrEmpty()) {
            params["endTime"] = endAt
        }
        requestNewApi(ICNetworkClient.getSocialApi().getCointHistory(params), listener)
    }

}