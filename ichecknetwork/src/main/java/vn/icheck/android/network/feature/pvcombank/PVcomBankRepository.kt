package vn.icheck.android.network.feature.pvcombank

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICKyc
import vn.icheck.android.network.models.pvcombank.*

class PVcomBankRepository : BaseInteractor() {

    suspend fun checkHasCard() = ICNetworkClient.getNewSocialApi().checkHasCard(APIConstants.socialHost + APIConstants.PVCombank.HAS_CARD)

    fun getFormLogin(listener: ICNewApiListener<ICResponse<ICAuthenPVCard>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getLinkFormAuth(), listener)
    }

    suspend fun getFormAuth() = ICNetworkClient.getNewSocialApi().getLinkFormAuthV2()

    fun getMyListCard(listener: ICNewApiListener<ICResponse<ICListResponse<ICListCardPVBank>>>) {
        val fullName = SessionManager.session.user?.getNamePVCombank ?: ""
        requestNewApi(ICNetworkClient.getNewSocialApi().getListCardPVComBank(fullName), listener)
    }

    suspend fun getMyListCards(): ICResponse<ICListResponse<ICListCardPVBank>> {
        val url = APIConstants.socialHost + APIConstants.PVCombank.LIST_CARD
        val fullName = SessionManager.session.user?.getNamePVCombank ?: ""
        return ICNetworkClient.getNewSocialApi().getListCardPVComBankV2(url, fullName)
    }

    suspend fun getKyc() = ICNetworkClient.getNewSocialApi().getKyc(APIConstants.socialHost + APIConstants.PVCombank.GET_KYC)

    fun getSpecialOffer(offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICSpecialOfferCardPVBank>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
    }

    fun verifyPin(pin: String, listener: ICNewApiListener<ICResponse<ICLockCard>>) {

    }

    fun lockCard(cardId: String, listener: ICNewApiListener<ICResponse<ICLockCard>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().lockCard(cardId), listener)
    }

    fun verifyOtp(requestId: String, otp: String, otptranid: String, listener: ICNewApiListener<ICResponse<ICLockCard>>) {
        val body = hashMapOf<String, Any>()
        body["requestId"] = requestId
        body["otpcode"] = otp
        body["otptranid"] = otptranid
        requestNewApi(ICNetworkClient.getNewSocialApi().verifyOtpUnlockCard(body, requestId), listener)
    }

    fun unlockCard(cardId: String, listener: ICNewApiListener<ICResponse<ICLockCard>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().unlockCard(cardId), listener)
    }

    fun getTransaction(cardId: String, listener: ICNewApiListener<ICResponse<ICTransactionPVCard>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getTransactionCard(cardId), listener)
    }

    fun setDefaultCard(cardId: String, listener: ICNewApiListener<ICResponse<Boolean>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().setDefaultCard(cardId), listener)
    }

    fun getFullCard(cardId: String, listener: ICNewApiListener<ICResponse<ICLockCard>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getFullCard(cardId), listener)
    }

}