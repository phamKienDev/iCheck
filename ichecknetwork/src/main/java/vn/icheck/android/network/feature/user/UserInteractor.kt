package vn.icheck.android.network.feature.user

import okhttp3.ResponseBody
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.model.kyc.KycResponse
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.network.models.wall.ICUserFollowWall
import vn.icheck.android.network.models.wall.ICUserPublicInfor

/**
 * Created by VuLCL on 28/10/2019.
 */
public class UserInteractor : BaseInteractor() {

    fun sendOtpConfirmPhoneStamp(phone: String, listener: ICApiListener<ICStatus>) {
        val body = hashMapOf<String, String>()
        body["phone"] = phone

//        val host = APIConstants.DEFAULT_HOST + APIConstants.USERSENDOTPCONFIRMPHONESTAMP()
        val host = APIConstants.defaultHost + APIConstants.USERSENDOTPCONFIRMPHONESTAMP()
        requestApi(ICNetworkClient.getApiClient().sendOtpConfirmPhone(host, body), listener)
    }

    fun confirmOtpStamp(phone: String, otp: String, listener: ICApiListener<ICStatus>) {
        val body = hashMapOf<String, String>()
        body["phone"] = phone
        body["otp"] = otp

//        val host = APIConstants.DEFAULT_HOST + APIConstants.USERCONFIRMPHONESTAMP()
        val host = APIConstants.defaultHost + APIConstants.USERCONFIRMPHONESTAMP()
        requestApi(ICNetworkClient.getApiClient().confirmPhone(host, body), listener)
    }

    fun getUserMe(listener: ICApiListener<ICUser>) {
        requestApi(ICNetworkClient.getApiClient().userMe, listener)
    }

    fun getUserMeDelay(listener: ICApiListener<ICUser>) {
        requestApiDelay(ICNetworkClient.getApiClient().userMe, listener)
    }

    fun getUserProfile(accountID: Long, listener: ICApiListener<ICUser>) {
        requestApi(ICNetworkClient.getApiClient().getUserProfile(accountID), listener)
    }

    fun getUserProfileDelay(accountID: Long, listener: ICApiListener<ICUser>) {
        requestApi(ICNetworkClient.getApiClient().getUserProfile(accountID), listener)
    }

    fun getListUserAddress(listener: ICApiListener<ICListResponse<ICAddress>>) {
        requestApi(ICNetworkClient.getApiClient().userAddress, listener)
    }

    fun getUserAddressDetail(addressID: Long, listener: ICApiListener<ICAddress>) {
        requestApi(ICNetworkClient.getApiClient().getUserAddressDetail(addressID), listener)
    }

    fun createUserAddress(body: ICAddress, listener: ICApiListener<ICAddress>) {
        requestApi(ICNetworkClient.getApiClient().createUserAddress(body), listener)
    }

    fun deleteUserAddress(id: Long, listener: ICApiListener<ICRespID>) {
        requestApi(ICNetworkClient.getSimpleApiClient().deleteUserAddress(id), listener)
    }

    fun getVnShopLink(listener: ICApiListener<ICLink>) {
        val body = hashMapOf<String, String>()
        SessionManager.session.token?.let {
            body["token"] = it
        }
        requestApi(ICNetworkClient.getApiClient().getVnShopLink(body), listener)
    }

    fun getMyID(listener: ICNewApiListener<ICResponse<ICMyID>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getMyID(), listener)
    }

    suspend fun searchUser(offset: Int, limit: Int, filterString: String?, cityID: MutableList<Long>?, districtId: Int?, wardId: Int?, gender: MutableList<Int>?): ICResponse<ICListResponse<ICSearchUser>> {
        val query = hashMapOf<String, Any>()
        query["offset"] = offset
        query["limit"] = limit

        if (!filterString.isNullOrEmpty()) {
            query["filterString"] = filterString
        }

        if (!cityID.isNullOrEmpty())
            query["cityIdList"] = cityID

        if (districtId != null)
            query["districtIdList"] = districtId

        if (wardId != null)
            query["wardIdList"] = wardId

        if (!gender.isNullOrEmpty())
            query["genders"] = gender

        return ICNetworkClient.getSocialApi().searchUsers(query)
    }

    fun getUserPublicInfor(listener: ICNewApiListener<ICResponse<ICListResponse<ICUserPublicInfor>>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getUserPublicInfor(), listener)
    }

    fun updateInforPublic(privacyElementId: Int?, checked: Boolean, listener: ICNewApiListener<ICResponseCode>) {
        val body = hashMapOf<String, Any>()
        if (privacyElementId != null) {
            body["privacyElementId"] = privacyElementId
        }
        body["selected"] = checked
        requestNewApi(ICNetworkClient.getNewSocialApi().updateInforPublic(body), listener)
    }

    fun getListUserFollow(key: String?, page: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICUserFollowWall>>>) {
        val params = hashMapOf<String, Any>()
        if (!key.isNullOrEmpty()) {
            params["filterString"] = key
        }
        params["offset"] = page
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getListUserFollow(params), listener)
    }

    fun getListUserWatching(key: String?, page: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICUserFollowWall>>>) {
        val params = hashMapOf<String, Any>()
        if (!key.isNullOrEmpty()) {
            params["filterString"] = key
        }
        params["offset"] = page
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getListUserWatching(params), listener)
    }

    fun getListFriendOfUser(id: Long, key: String?, page: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICUserFollowWall>>>) {
        val params = hashMapOf<String, Any>()
        if (!key.isNullOrEmpty()) {
            params["filterString"] = key
        }
        params["offset"] = page
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getListFriendOfUser(id, params), listener)
    }

    fun putUnFriend(id: Long, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        body["userId"] = id
//        body["status"] = -1
        requestNewApi(ICNetworkClient.getNewSocialApi().putUnFriend(body), listener)
    }

    fun putAddFriend(id: Long, status: Int?, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        body["userId"] = id

        if (status != null) {
            body["status"] = status
        }

        requestNewApi(ICNetworkClient.getNewSocialApi().putUnOrAddFriend(body), listener)
    }

    fun followUser(id: Long, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        body["targetUserId"] = id
        requestNewApi(ICNetworkClient.getNewSocialApi().followOrUnFollow(body), listener)
    }

    fun getListReportUser(listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getListReportUser(), listener)
    }

    fun sendReportUser(idUser: Long, listReason: MutableList<Int>, message: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        val body = hashMapOf<String, Any>()
        body["userId"] = idUser
        body["reportElementIdList"] = listReason
        body["description"] = message
        requestNewApi(ICNetworkClient.getNewSocialApi().postReportUser(body), listener)
    }

    fun getRankOfUser(listener: ICNewApiListener<ICResponse<ICRankOfUser>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getRankOfUser(), listener)
    }

    fun postKyc(body: ICPostKyc, listener: ICNewApiListener<ICResponse<String>>) {
        val url = APIConstants.socialHost + "api/user/kyc/request"
        requestNewApi(ICNetworkClient.getNewSocialApi().postKyc(url, body), listener)
    }

//    fun createUserKyc(body: HashMap<String, Any?>, listener: ICNewApiListener<ResponseBody>) {
//        val url = APIConstants.socialHost + "social/api/users/kyc-request"
//        requestNewApi(ICNetworkClient.getNewSocialApi().createUserKyc(url, body), listener)
//    }

    fun getUserKyc(listener: ICNewApiListener<ICResponse<ListResponse<KycResponse>>>) {
        val url = APIConstants.socialHost + "social/api/users/kyc-request"
        requestNewApi(ICNetworkClient.getNewSocialApi().getUserKyc(url), listener)
    }
}