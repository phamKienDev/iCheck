package vn.icheck.android.network.feature.order

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product.report.ICReportForm

class OrderInteractor : BaseInteractor() {

    fun getListOrders(status: Int, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICOrderHistoryV2>>>) {
        val params = hashMapOf<String, Any>()
        params["status"] = status
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        requestNewApi(ICNetworkClient.getSocialApi().getListOrders(params), listener)
    }

    fun getListReportOrder(listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getListReportOrder(), listener)
    }

    fun postReportOrder(id: Long, listIdReason: MutableList<Int>, message: String, listener: ICNewApiListener<ICResponse<Any>>) {
        val params = hashMapOf<String, Any>()
        if (!listIdReason.isNullOrEmpty()) {
            params["reportItemId"] = listIdReason
        }
        if (message.isNotEmpty()) {
            params["otherReason"] = message
        }
        requestNewApi(ICNetworkClient.getSocialApi().putReportOrder(id, params), listener)
    }

    fun getOrderDetail(orderID: Long, listener: ICApiListener<ICOrderDetail>) {
        requestApiDelay(ICNetworkClient.getApiClient().getOrderDetail(orderID), listener)
    }

    fun cancelOrder(orderID: Long, listener: ICNewApiListener<ICResponse<ICRespID>>) {
        val body = hashMapOf<String, Any>()
        body["status"] = 6 // cancel

        requestNewApi(ICNetworkClient.getApiClient().updateStatusOrder(orderID, body), listener)
    }

    fun updateStatusOrder(orderID: Long, status: Int, listener: ICNewApiListener<ICResponse<ICRespID>>) {
        val body = hashMapOf<String, Any>()
        body["status"] = status

        requestNewApi(ICNetworkClient.getSocialApi().updateStatusOrder(orderID, body), listener)
    }

    fun payOrder(orderID: Long, listener: ICApiListener<ICRespCheckoutCart>) {
        requestApi(ICNetworkClient.getApiClient().payOrder(orderID), listener)
    }

    fun completeOrder(orderID: Long, listener: ICApiListener<ICOrderHistory>) {
        requestApi(ICNetworkClient.getApiClient().completeOrder(orderID), listener)
    }
}