package vn.icheck.android.network.feature.location

import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICPointDetail
import vn.icheck.android.network.models.ICPoints

class LocationInteraction : BaseInteractor() {

    /**
     * tìm kiếm địa chỉ trên Google Map
     *
     * @param key: tiếng Việt ko dấu
     */
    fun searchLocation(input: String, limit: Int, listener: ICNewApiListener<ICResponse<ICPoints>>) {
        val url = APIConstants.socialHost + APIConstants.Location.SEARCH

        val params = hashMapOf<String, Any>()
        params["address"] = input

        requestNewApi(ICNetworkClient.getNewSocialApi().searchLocation(url, params), listener)
    }

    fun getLocationDetail(address: String, listener: ICNewApiListener<ICResponse<ICPointDetail>>) {
        val url = APIConstants.socialHost + APIConstants.Location.DETAIL

        val params = hashMapOf<String, Any>()
        params["address"] = address

        requestNewApi(ICNetworkClient.getNewSocialApi().getLocationDetail(url, params), listener)
    }
}