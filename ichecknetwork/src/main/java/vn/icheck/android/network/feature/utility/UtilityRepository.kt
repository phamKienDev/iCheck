package vn.icheck.android.network.feature.utility

import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICTheme

class UtilityRepository : BaseInteractor() {

    fun getAllUtilities(listener: ICNewApiListener<ICResponse<MutableList<ICTheme>>>) {
        val url = APIConstants.socialHost + APIConstants.allUtilities()
        requestNewApi(ICNetworkClient.getSocialApi().getAllUtility(url), listener)
    }

    fun getHomeFunc(url: String, listener: ICNewApiListener<ICResponse<ICTheme>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getHomeFunc(url), listener)
    }
}