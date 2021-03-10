package vn.icheck.android.network.feature.facebook

import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICRespMappingFacebook

class FacebookInteractor : BaseInteractor() {

    fun mappingFacebook(userID: Long, facebookToken: String, listener: ICApiListener<ICRespMappingFacebook>) {
        val hashMap = hashMapOf<String, Any>()
        hashMap["id"] = userID
        hashMap["facebook_token"] = facebookToken

        requestApi(ICNetworkClient.getApiClient().mappingFacebook(hashMap), listener)
    }
}