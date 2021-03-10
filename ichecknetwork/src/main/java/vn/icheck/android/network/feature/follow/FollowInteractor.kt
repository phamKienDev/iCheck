package vn.icheck.android.network.feature.follow

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICUserFollower
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.network.models.ICRespDelete

/**
 * Created by VuLCL on 28/10/2019.
 */
class FollowInteractor : BaseInteractor() {

    fun getUserFollowing(accountID: Long, objectType: String, offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICUserFollowing>>) {
        val hashMap = hashMapOf<String, Any>()
        hashMap["account_id"] = accountID
        hashMap["object_type"] = objectType
        hashMap["offset"] = offset
        hashMap["limit"] = limit

        requestApi(ICNetworkClient.getApiClient().getUserFollowings(hashMap), listener)
    }

    fun getUserFollowers(accountID: Long, objectType: String, offset: Int, listener: ICApiListener<ICListResponse<ICUserFollower>>) {
        val hashMap = hashMapOf<String, Any>()
        hashMap["object_id"] = accountID
        hashMap["object_type"] = objectType
        hashMap["offset"] = offset
        hashMap["limit"] = APIConstants.LIMIT

        requestApi(ICNetworkClient.getApiClient().getUserFollowers(hashMap), listener)
    }

    fun addFollowUser(userID: Long, type: String, listener: ICApiListener<ICUserFollowing>) {
        val body = hashMapOf<String, Any>()
        body["object_id"] = userID
        body["object_type"] = type

        requestApi(ICNetworkClient.getApiClient().addFollow(body), listener)
    }

    fun deleteFollowUser(userID: Long, type: String, listener: ICApiListener<ICRespDelete>) {
        val body = hashMapOf<String, Any>()
        body["object_id"] = userID
        body["object_type"] = type

        requestApi(ICNetworkClient.getApiClient().deleteFollow(body), object : ICTokenListener<ICRespDelete> {
            override fun onRefreshTokenSuccess() {
                deleteFollowUser(userID, type, listener)
            }

            override fun onRefreshTokenError() {

            }

            override fun onSuccess(obj: ICRespDelete) {
                listener.onSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                listener.onError(error)
            }
        })
    }
}