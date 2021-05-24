package vn.icheck.android.network.feature.popup

import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICPopup

class PopupInteractor :BaseInteractor(){

    fun getPopup(targetId: Long?, screenCode: String,listener:ICNewApiListener<ICResponse<ICPopup>>) {
        val params = hashMapOf<String, Any>()
        if (targetId != null) {
            params["targetId"] = targetId
        }
        params["screenCode"] = screenCode
        requestNewApi(ICNetworkClient.getSocialApi().getPopupByScreen(params),listener)
    }
}