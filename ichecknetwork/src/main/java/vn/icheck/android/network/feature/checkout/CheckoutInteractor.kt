package vn.icheck.android.network.feature.checkout

import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICCheckout
import vn.icheck.android.network.models.ICReqCheckout
import vn.icheck.android.network.models.ICRespCheckoutCart

class CheckoutInteractor : BaseInteractor() {

    fun createCheckout(body: ICReqCheckout, listener: ICApiListener<ICCheckout>) {
        requestApi(ICNetworkClient.getApiClient().createCheckout(body), listener)
    }

    fun completeCheckout(body: ICReqCheckout, listener: ICApiListener<ICRespCheckoutCart>) {
        requestApi(ICNetworkClient.getApiClient().completeCheckout(body), listener)
    }
}