package vn.icheck.android.loyalty.repository

import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.network.BaseRepository
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.network.ICNetworkClient
import vn.icheck.android.loyalty.network.SessionManager

internal class CampaignRepository : BaseRepository() {

    /**
     * Api Scan Voucher
     */
    fun scanVoucher(voucher: String, listener: ICApiListener<ICKResponse<ICKScanVoucher>>) {
        val body = hashMapOf<String, Any>()
        body["voucher"] = voucher

        val url = APIConstants.LOYALTY_HOST + "loyalty/cms/voucher/scan"
        requestApi(ICNetworkClient.getApiClientLoyalty().scanVoucher(url, body), listener)
    }

    fun usedVoucher(voucher: String,
                    note: String?,
                    phone: String,
                    name: String?,
                    email: String?,
                    address: String?,
                    city_id: Int?,
                    district_id: Int?,
                    ward_id: Int?,
                    listener: ICApiListener<ICKResponse<ICKNone>>) {
        val body = hashMapOf<String, Any>()
        body["voucher_code"] = voucher
        body["phone"] = phone

        if (!name.isNullOrEmpty()) {
            body["name"] = name
        }

        if (!email.isNullOrEmpty()) {
            body["email"] = email
        }

        if (!address.isNullOrEmpty()) {
            body["address"] = address
        }

        if (city_id != null) {
            body["city_id"] = city_id
        }

        if (district_id != null) {
            body["city_id"] = district_id
        }

        if (ward_id != null) {
            body["city_id"] = ward_id
        }

        if (!note.isNullOrEmpty()) {
            body["note"] = note
        }

        val url = APIConstants.LOYALTY_HOST + "loyalty/cms/voucher/mark-use"
        requestApi(ICNetworkClient.getApiClientLoyalty().usedVoucher(url, body), listener)
    }

    fun getCampaign(barcode: String, listener: ICApiListener<ICKResponse<ICKLoyalty>>) {
        val url = APIConstants.LOYALTY_HOST + "loyalty/campaign/get-campaign"
        requestApi(ICNetworkClient.getApiClientLoyalty().getCampaign(url, barcode), listener)
    }

    fun postCancelShipGift(gift_id: Long, listener: ICApiListener<ICKResponse<ICKWinner>>) {
        val body = hashMapOf<String, Any>()
        body["status"] = "refused_gift"

        val url = APIConstants.LOYALTY_HOST + "loyalty/customer/winner/$gift_id/refuse-gift"
        requestApi(ICNetworkClient.getApiClientLoyalty().postRefuseGift(url, body), listener)
    }

    fun getDetailGiftWinner(winnerId: Long, listener: ICApiListener<ICKResponse<ICKGift>>) {
        val url = APIConstants.LOYALTY_HOST + "loyalty/winner/gift/$winnerId"

        requestApi(ICNetworkClient.getApiClientLoyalty().getDetailGiftWinner(url), listener)
    }

    fun postReceiveGift(barcode: String, code: String?, listener: ICApiListener<ICKResponse<ICKReceiveGift>>) {
        val params = hashMapOf<String, Any>()
        val user = SessionManager.session.user

        params["target"] = barcode

        if (!code.isNullOrEmpty()) {
            params["code"] = code
        }

        if (!user?.name.isNullOrEmpty()) {
            params["name"] = user?.name!!
        }

        if (!user?.phone.isNullOrEmpty()) {
            params["phone"] = user?.phone!!
        }

        if (!user?.email.isNullOrEmpty()) {
            params["email"] = user?.email!!
        }

        if (!user?.city?.name.isNullOrEmpty()) {
            params["city_name"] = user?.city?.name!!
        }

        if (!user?.district?.name.isNullOrEmpty()) {
            params["district_name"] = user?.district?.name!!
        }

        if (!user?.ward?.name.isNullOrEmpty()) {
            params["ward_name"] = user?.ward?.name!!
        }

        if (!user?.address.isNullOrEmpty()) {
            params["address"] = user?.address!!
        }

        if (!user?.avatar.isNullOrEmpty()) {
            params["avatar"] = user?.avatar!!
        }

        if (user?.city_id != null) {
            params["city_id"] = user.city_id!!
        }

        if (user?.district_id != null) {
            params["district_id"] = user.district_id!!
        }

        if (user?.ward_id != null) {
            params["ward_id"] = user.ward_id!!
        }

        val url = APIConstants.LOYALTY_HOST + "loyalty/campaign/get-gift"
        requestApi(ICNetworkClient.getApiClientLoyalty().postReceiveGift(url, params), listener)
    }

    fun postAccumulatePoint(campaignId: Long, code: String?, target: String?, listener: ICApiListener<ICKResponse<ICKAccumulatePoint>>) {
        val params = hashMapOf<String, Any>()

        val user = SessionManager.session.user

        params["campaign_id"] = campaignId
        if (!code.isNullOrEmpty()) {
            params["code"] = code
        }
        if (!target.isNullOrEmpty()) {
            params["target"] = target
        }
        if (!user?.name.isNullOrEmpty()) {
            params["name"] = user?.name!!
        }
        if (!user?.phone.isNullOrEmpty()) {
            params["phone"] = user?.phone!!
        }
        if (!user?.email.isNullOrEmpty()) {
            params["email"] = user?.email!!
        }
        if (user?.city_id != null) {
            params["city_id"] = user.city_id!!
        }
        if (user?.district_id != null) {
            params["district_id"] = user.district_id!!
        }
        if (user?.ward_id != null) {
            params["ward_id"] = user.ward_id!!
        }
        if (!user?.address.isNullOrEmpty()) {
            params["address"] = user?.address!!
        }
        if (!user?.city?.name.isNullOrEmpty()) {
            params["city_name"] = user?.city?.name!!
        }
        if (!user?.district?.name.isNullOrEmpty()) {
            params["district_name"] = user?.district?.name!!
        }
        if (!user?.ward?.name.isNullOrEmpty()) {
            params["ward_name"] = user?.ward?.name!!
        }
        if (!user?.avatar.isNullOrEmpty()) {
            params["avatar"] = user?.avatar!!
        }

        val host = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/accumulate-point"
        requestApi(ICNetworkClient.getApiClientLoyalty().postNhapMaTichDiem(host, params), listener)
    }

    fun postGameGift(campaignId: Long, barcode: String?, code: String?, listener: ICApiListener<ICKResponse<DataReceiveGameResp>>) {
        val params = hashMapOf<String, Any>()
        val user = SessionManager.session.user

        params["campaign_id"] = campaignId

        if (!barcode.isNullOrEmpty()) {
            params["target"] = barcode
        }
        if (!code.isNullOrEmpty()) {
            params["code"] = code
        }

        if (!user?.name.isNullOrEmpty()) {
            params["name"] = user?.name!!
        }

        if (!user?.phone.isNullOrEmpty()) {
            params["phone"] = user?.phone!!
        }

        if (!user?.email.isNullOrEmpty()) {
            params["email"] = user?.email!!
        }

        if (!user?.city?.name.isNullOrEmpty()) {
            params["city_name"] = user?.city?.name!!
        }

        if (!user?.district?.name.isNullOrEmpty()) {
            params["district_name"] = user?.district?.name!!
        }

        if (!user?.ward?.name.isNullOrEmpty()) {
            params["ward_name"] = user?.ward?.name!!
        }

        if (!user?.address.isNullOrEmpty()) {
            params["address"] = user?.address!!
        }

        if (!user?.avatar.isNullOrEmpty()) {
            params["avatar"] = user?.avatar!!
        }

        if (user?.city_id != null) {
            params["city_id"] = user.city_id!!
        }

        if (user?.district_id != null) {
            params["district_id"] = user.district_id!!
        }

        if (user?.ward_id != null) {
            params["ward_id"] = user.ward_id!!
        }

        val url = APIConstants.LOYALTY_HOST + "loyalty/customer/campaign/game"
        requestApi(ICNetworkClient.getApiClientLoyalty().postGameGift(url, params), listener)
    }

    fun getGiftDetail(id: Long, listener: ICApiListener<ICKResponse<ICKLoyalty>>) {
        val url = APIConstants.LOYALTY_HOST + "loyalty/campaign/$id"
        requestApi(ICNetworkClient.getApiClientLoyalty().getGiftDetail(url), listener)
    }
}