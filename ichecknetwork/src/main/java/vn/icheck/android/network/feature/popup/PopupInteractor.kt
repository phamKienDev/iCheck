package vn.icheck.android.network.feature.popup

import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.APIConstants.defaultHost
import vn.icheck.android.network.base.APIConstants.socialHost
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICPopup

class PopupInteractor : BaseInteractor() {

    fun getPopup(targetId: Long?, screenCode: String, listener: ICNewApiListener<ICResponse<ICPopup>>) {
        val params = hashMapOf<String, Any>()
        if (targetId != null) {
            params["targetId"] = targetId
        }
        params["screenCode"] = screenCode
        requestNewApi(ICNetworkClient.getSocialApi().getPopupByScreen(params), listener)
    }

    fun clickPopup(targetId: Long, listener: ICNewApiListener<ICResponse<Any>>) {
        val body = hashMapOf<String, Any>()
        body["targetId"] = "targetId"

        val url = socialHost + APIConstants.Popup.CLICK_POUP_ADS.replace("{id}", targetId.toString())
        requestNewApi(ICNetworkClient.getSocialApi().clickPopupAds(url, body), listener)
    }
}