package vn.icheck.android.network.feature.utility

import vn.icheck.android.network.BuildConfig
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICTheme

class UtilityRepository : BaseInteractor() {

    fun getAllUtilities(listener: ICNewApiListener<ICResponse<MutableList<ICTheme>>>) {
        val url = APIConstants.adsSocialHost + if (BuildConfig.FLAVOR.contentEquals("dev")) {
            APIConstants.Utility.ALL_UTILITY
        } else {
            APIConstants.PATH_CDN + APIConstants.Utility.ALL_UTILITY
        }

        requestNewApi(ICNetworkClient.getSocialApi().getAllUtility(url), listener)
    }

    fun getHomeFunc(url: String, listener: ICNewApiListener<ICResponse<ICTheme>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getHomeFunc(url), listener)
    }
}