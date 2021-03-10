package vn.icheck.android.network.feature.recharge_phone

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICBuyEpin
import vn.icheck.android.network.models.ICNone
import vn.icheck.android.network.models.ICRechargeThePhone
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.network.models.recharge_phone.IC_RESP_Buy_Recharge_Phone
import vn.icheck.android.network.models.recharge_phone.IC_RESP_HistoryBuyTopup

class RechargePhoneInteractor : BaseInteractor() {
    fun getDataTopup(listener: ICApiListener<ICListResponse<ICRechargePhone>>) {
        val params = hashMapOf<String, Any>()
        params["type"] = "PHONE_TOPUP"
        requestApi(ICNetworkClient.getApiClient().getListTopupService(params), listener)
    }

    fun getDataTopupV2(listener: ICNewApiListener<ICResponse<ICRechargeThePhone>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getListTopupServiceV2(), listener)
    }

    fun getListPaymentType(listener: ICNewApiListener<ICResponse<ICListResponse<ICRechargePhone>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getListPaymentType(), listener)
    }

    fun getDetailCard(orderId: Long, listener: ICNewApiListener<ICResponse<ICRechargePhone>>) {
        val params = hashMapOf<String, Any>()
        params["orderId"] = orderId
        requestNewApi(ICNetworkClient.getSocialApi().getDetailCard(params), listener)
    }

    fun vnpayCard(payType: String, returnUrl: String, amount: Long, phone: String?, serviceId: Long, orderType: String, listener: ICNewApiListener<ICResponse<ICRechargePhone>>) {
        val params = hashMapOf<String, Any>()
        params["returnUrl"] = returnUrl
        params["amount"] = amount
        params["payType"] = payType
        params["orderType"] = orderType
        params["serviceId"] = serviceId
        if (!phone.isNullOrEmpty()) {
            params["phone"] = phone
        }
        requestNewApi(ICNetworkClient.getSocialApi().vnpayCard(params), listener)
    }

    fun buyCard(serviceId: Long, amount: Long, payType: String, listener: ICNewApiListener<ICResponse<ICRechargePhone>>) {
        val params = hashMapOf<String, Any>()
        params["serviceId"] = serviceId
        params["amount"] = amount
        params["payType"] = payType
        requestNewApi(ICNetworkClient.getSocialApi().buyCard(params), listener)
    }

    fun rechargeCard(serviceId: Long, amount: Long, payType: String, phone: String, listener: ICNewApiListener<ICResponse<ICRechargePhone>>) {
        val params = hashMapOf<String, Any>()
        params["serviceId"] = serviceId
        params["amount"] = amount
        params["payType"] = payType
        params["phone"] = phone
        requestNewApi(ICNetworkClient.getSocialApi().rechargeCard(params), listener)
    }

    fun getDataBuyCard(listener: ICApiListener<ICListResponse<ICRechargePhone>>) {
        requestApi(ICNetworkClient.getApiClient().getListBuyCardService("EPIN"), listener)
    }

    fun buyTopup(mId: Long, mValue: Long, phoneNumber: String, listener: ICApiListener<IC_RESP_Buy_Recharge_Phone>) {
        val body = hashMapOf<String, Any>()
        body["service_id"] = mId
        body["service_type"] = "PREPAID"
        body["denomination"] = mValue.toString()
        body["phone"] = phoneNumber
        requestApi(ICNetworkClient.getApiClient().buyTopupService(body), listener)
    }

    fun buyMobileCard(mId: Long, mValue: Long, listener: ICApiListener<ICBuyEpin>) {
        val requestBody = hashMapOf<String, Any>()
        requestBody.put("service_id", mId)
        requestBody.put("denomination", mValue.toString())
        requestApi(ICNetworkClient.getApiClient().postBuyEpin(requestBody), listener)
    }

    fun getHistoryBuyTopup(offset: Int, listener: ICApiListener<ICListResponse<IC_RESP_HistoryBuyTopup>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["type"] = "EPIN"
        requestApi(ICNetworkClient.getApiClient().getHistoryBuyTopup(params), listener)
    }

    fun getHistoryBuyTopupV2(offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICRechargePhone>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["serviceType"] = "PHONE_CARD"
        requestNewApi(ICNetworkClient.getSocialApi().getHistoryBuyTopupV2(params), listener)
    }

    fun getHistoryLoadedTopupV2(offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICRechargePhone>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["serviceType"] = "PHONE_TOPUP"
        requestNewApi(ICNetworkClient.getSocialApi().getHistoryBuyTopupV2(params), listener)
    }

    fun getHistoryLoadedTopup(offset: Int, listener: ICApiListener<ICListResponse<IC_RESP_HistoryBuyTopup>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["type"] = "PHONE_TOPUP"
        requestApi(ICNetworkClient.getApiClient().getHistoryBuyTopup(params), listener)
    }

    fun onTickUseTopup(id: String, listener: ICApiListener<IC_RESP_HistoryBuyTopup>) {
        requestApi(ICNetworkClient.getApiClient().onTickUseTopup(id), listener)
    }

    fun onTickUseTopup(id: Long, listener: ICNewApiListener<ICResponse<ICNone>>) {
        val query = hashMapOf<String, Any>()
        query["historyTopupId"] = id
        query["isUsed"] = true
        requestNewApi(ICNetworkClient.getSocialApi().onTickUseTopup(query), listener)
    }
}