package vn.icheck.android.network.feature.additives

import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICAdditives

class AdditivesInteractor : BaseInteractor() {

    fun searchAdditives(keySearch: String, offset: Int, listener: ICApiListener<ICListResponse<ICAdditives>>) {
        val params = hashMapOf<String, Any>()
        params["search"] = keySearch
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        requestApi(ICNetworkClient.getApiClient().searchAdditives(params), listener)
    }

    fun getAdditivesDetail(id: Long, listener: ICApiListener<ICAdditives>) {
        requestApi(ICNetworkClient.getApiClient().getAdditivesDetail(id), listener)
    }
}