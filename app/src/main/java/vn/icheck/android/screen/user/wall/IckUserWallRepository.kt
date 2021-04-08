package vn.icheck.android.screen.user.wall

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import okhttp3.ResponseBody
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.ICK_URI
import vn.icheck.android.model.ApiResponse
import vn.icheck.android.model.icklogin.IckUserInfoResponse
import vn.icheck.android.model.icklogin.RequestOtpResponse
import vn.icheck.android.model.posts.PostResponse
import vn.icheck.android.model.privacy.UserPrivacyResponse
import vn.icheck.android.model.reports.ReportUserCategoryResponse
import vn.icheck.android.model.wall.LayoutResponse
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.util.makeICRequest
import vn.icheck.android.util.makeSimpleRequest
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.wall.IcFriendResponse
import vn.icheck.android.screen.user.contribute_product.UPLOAD_LIST_IMAGE
import vn.icheck.android.worker.UploadImageWorker
import java.io.File
import javax.inject.Inject

class IckUserWallRepository @Inject constructor(
        private val ickApi: ICKApi,
        private val workManager: WorkManager,
        private val sharedPreferences: SharedPreferences
) {

    val mErr = MutableLiveData<String?>()

    suspend fun getWallLayout(id: Long): LayoutResponse? {
        return makeICRequest({
            if (id != SessionManager.session.user?.id)
                ickApi.getLayout("public-wall", "$id")
            else
                ickApi.getLayout("private-wall", "$id")
        }, {
            mErr.postValue(it?.message)
        })
    }

    fun getFacebook(): String? {
        return sharedPreferences.getString("FACEBOOK_USERNAME", "Chưa liên kết")
    }

    suspend fun getUserInfo(): IckUserInfoResponse? {
        return try {
            ickApi.getUserInfo()
        } catch (e: Exception) {
            mErr.postValue(e.message)
            null
        }
    }

    suspend fun getPrivateInfo(path: String?): IckUserInfoResponse? {
        return makeICRequest({
            ickApi.getPrivateInfo(path)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun getPublicInfo(path: String?): IckUserInfoResponse? {
        return makeICRequest({
            ickApi.getPublicInfo(path)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun getPublicFriend(path: String?): IcFriendResponse? {
        return makeICRequest({
            ickApi.getPublicFriendList(path)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun getGeneralFriend(path: String?): IcFriendResponse? {
        return makeICRequest({
            ickApi.getPublicFriendList(path)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun getFriendInvitation(path: String?): ICResponse<ICListResponse<ICSearchUser>>? {
        return makeICRequest({
            ickApi.getFriendInvitation(path)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun getFriendSuggestion(path: String?): ICResponse<ICListResponse<ICUser>>? {
        return makeICRequest({
            ickApi.getListFriendSuggestion(path)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun getUserProfile(id: Long): ApiResponse<IckUserInfoResponse?> {
        val apiResponse: ApiResponse<IckUserInfoResponse?>
        apiResponse = try {
//            val response = if(id != SessionManager.session.user?.id )
//                ickApi.getProfile(id) else ickApi.getUserInfo()
            val response = if (id != SessionManager.session.user?.id) makeSimpleRequest { ickApi.getProfile(id) }
            else makeSimpleRequest { ickApi.getUserInfo() }
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
        return apiResponse
    }

    suspend fun getUserFriendList(id: Long): IcFriendResponse? {
        return try {
            ickApi.getUserFriendList(id, 10, 0)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFriendRequest(id: Long): ApiResponse<ICResponse<ICListResponse<ICSearchUser>>> {
        val apiResponse: ApiResponse<ICResponse<ICListResponse<ICSearchUser>>>
        apiResponse = try {
            val response = ickApi.getFriendsRequest(0, 10)
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
        return apiResponse
    }

    suspend fun getUserPosts(request: String?): PostResponse? {
        return makeICRequest({
            ickApi.getUserPosts(request)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun getPostDetail(id: Long): ICResponse<ICPost>? {
        return makeICRequest({
            ickApi.getDetailPost(id)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun getListPost(userId: Long, limit: Int, offset: Int): ApiResponse<PostResponse> {
        val apiResponse: ApiResponse<PostResponse>
        apiResponse = try {
            val response = ickApi.getUserPosts(userId, limit, offset)
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
        return apiResponse
    }

    suspend fun getUserPrivacy(): ApiResponse<UserPrivacyResponse> {
        val apiResponse: ApiResponse<UserPrivacyResponse>
        apiResponse = try {
            val response = ickApi.getUserPrivacy()
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
        return apiResponse
    }

    suspend fun putUserPrivacy(body: HashMap<String, Any>): ApiResponse<ResponseBody> {
        val apiResponse: ApiResponse<ResponseBody>
        apiResponse = try {
            val response = ickApi.putUserPrivacy(body)
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
        return apiResponse
    }

    suspend fun updatePassword(oldPw: String, password: String): ApiResponse<RequestOtpResponse> {
        val apiResponse: ApiResponse<RequestOtpResponse>
        apiResponse = try {
            val requestBody = hashMapOf<String, Any>()
            requestBody["oldPassword"] = oldPw
            requestBody["newPassword"] = password
            requestBody["confirmPassword"] = password
            val response = ickApi.updatePassword(requestBody)
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
        return apiResponse
    }

    suspend fun firstPassword(password: String): ApiResponse<ICResponse<*>> {
        val apiResponse: ApiResponse<ICResponse<*>>
        apiResponse = try {
            val requestBody = hashMapOf<String, Any>()
            requestBody["password"] = password
            val response = ickApi.updateFirstPassword(requestBody)
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
        return apiResponse
    }


    suspend fun updateUserInfo(requestBody: HashMap<String, Any?>): ApiResponse<RequestOtpResponse?> {
        val apiResponse: ApiResponse<RequestOtpResponse?>
        apiResponse = try {
            val response = ickApi.updateUser(requestBody)
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
        return apiResponse
    }

    suspend fun linkFacebook(facebookToken: String?): ICResponse<*>? {
        return try {
            val response = ickApi.linkFacebook(facebookToken)
            response
        } catch (e: Exception) {
            null
        }
    }

    fun uploadListImage(listFiles: List<File?>): LiveData<List<WorkInfo>> {
        val listWork = arrayListOf<OneTimeWorkRequest>()
        for (item in listFiles) {
            val worker = OneTimeWorkRequestBuilder<UploadImageWorker>()
            worker.setInputData(workDataOf(ICK_URI to item?.absolutePath))
            listWork.add(worker.build())
        }
        workManager.beginUniqueWork(UPLOAD_LIST_IMAGE, ExistingWorkPolicy.REPLACE, listWork)
                .enqueue()
        return workManager.getWorkInfosForUniqueWorkLiveData(UPLOAD_LIST_IMAGE)
    }

    fun uploadListImage(listFiles: HashMap<String, File?>): LiveData<List<WorkInfo>> {
        val listWork = arrayListOf<OneTimeWorkRequest>()
        for (item in listFiles.entries) {
            val worker = OneTimeWorkRequestBuilder<UploadImageWorker>()
            worker.setInputData(workDataOf(ICK_URI to item.value?.absolutePath, "key" to item.key))
            listWork.add(worker.build())
        }
        workManager.beginUniqueWork(UPLOAD_LIST_IMAGE, ExistingWorkPolicy.REPLACE, listWork)
                .enqueue()
        return workManager.getWorkInfosForUniqueWorkLiveData(UPLOAD_LIST_IMAGE)
    }

    suspend fun getReportUserCategory(): ReportUserCategoryResponse? {
        return makeICRequest({
            ickApi.getReportUserCategory()
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun postReportUser(request: HashMap<String, Any?>): ResponseBody? {
        return makeICRequest({
            ickApi.postReportUser(request)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun sendFriendRequest(userId: Long?): ResponseBody? {
        return makeICRequest({
            val request = hashMapOf<String, Any?>()
            request["userId"] = userId
            ickApi.addFriend(request)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun removeFriendRequest(userId: Long?): ResponseBody? {
        return makeICRequest({
            val request = hashMapOf<String, Any?>()
            request["userId"] = userId
            request["status"] = -1
            ickApi.addFriend(request)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun unFriendUser(request: HashMap<String, Any?>): ICResponse<*>? {
        return makeICRequest({
            ickApi.unFriendUser(request)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun unFollowUser(request: HashMap<String, Any?>): ICResponse<*>? {
        return makeICRequest({
            ickApi.unFollowUser(request)
        }, {
            mErr.postValue(it?.message)
        })
    }

    suspend fun deletePost(id: Long): ICResponse<ICCommentPost>? {
        val url = APIConstants.socialHost + APIConstants.Product.DELETE_POST.replace("{id}", id.toString())
        val body = hashMapOf<String, Any>()
        body[""] = ""

        return makeICRequest({
            ickApi.deletePost(url, body)
        }, {
            mErr.postValue(if (it?.message.isNullOrEmpty()) {
                ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
            } else {
                it?.message
            })
        })
    }
}