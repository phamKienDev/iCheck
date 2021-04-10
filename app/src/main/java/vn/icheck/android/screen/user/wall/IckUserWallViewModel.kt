package vn.icheck.android.screen.user.wall

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.WorkInfo
import com.google.firebase.database.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.model.ApiResponse
import vn.icheck.android.model.icklogin.IckLoginFacebookResponse
import vn.icheck.android.model.icklogin.IckUserInfoResponse
import vn.icheck.android.model.icklogin.RequestOtpResponse
import vn.icheck.android.model.posts.PostResponse
import vn.icheck.android.model.posts.PostViewModel
import vn.icheck.android.model.privacy.UserPrivacyResponse
import vn.icheck.android.model.profile.IckUserFriendModel
import vn.icheck.android.model.profile.IckUserProfileModel
import vn.icheck.android.model.reports.ReportUserCategoryResponse
import vn.icheck.android.model.wall.LayoutResponse
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.wall.IcFriendResponse
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICMyFriendIdUser
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginRepository
import vn.icheck.android.screen.user.wall.option_edit_my_information.TAKE_AVATAR
import vn.icheck.android.screen.user.wall.report_user.ReportUserViewModel
import vn.icheck.android.util.ick.logError
import java.io.File
import java.util.*

class IckUserWallViewModel @ViewModelInject constructor(
        private val ickUserWallRepository: IckUserWallRepository,
        @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _listViewModel = arrayListOf<ICViewModel>()
    val listViewModel = MutableLiveData<List<ICViewModel>>()
    var userPrivacyResponse: UserPrivacyResponse? = null
    var id = -1L
    var privacy = hashMapOf<Int, Int>()
    var initActivity = false
    var userInfo: IckUserInfoResponse? = null
        set(value) {
            savedStateHandle.set("userinfo", value)
            field = value
        }
        get() = savedStateHandle.get("userinfo")
    val editUserInfo = hashMapOf<String, Any?>()
    var avatar: File? = null
    var wall: File? = null

    // Check user want to update avatar or wall background
    var photoType = TAKE_AVATAR

    // Check if user updated phone yet
    var changedPhone = false

    // Check when to show the bottom navigation or not
    var showBottomBar = MutableLiveData<Boolean>()

    // Check if user updated avatar or wall background yet
    fun hasImages(): Boolean = avatar != null || wall != null

    val listData = MutableLiveData<List<ApiResponse<*>>>()

    // Limit offset for load more posts
    var currentLimitPost = 10
    var currentOffsetPost = 10

    // live data emit posts
    val postsLiveData = MutableLiveData<ApiResponse<PostResponse>>()
    val arrPost = arrayListOf<ICViewModel>()
    var updatePost = 0
    var totalPost = 0

    val mErr = ickUserWallRepository.mErr
    val privacySettings = hashMapOf<String, Any>()

    val reportUserCategory = MutableLiveData<ReportUserCategoryResponse>()
    var reportCategory: ReportUserCategoryResponse? = null
    val arrReport = arrayListOf<ReportUserViewModel>()
    var showSuccessReport = MutableLiveData<Int>()
    var calendar = Calendar.getInstance()
    val currentDate = Calendar.getInstance().time

    var updateUser = MutableLiveData<Int>()

    var totalFollowing = 0L
    var totalFollowed = 0L
    var posCreatePost = 0
    var reachedEnd = false
    var totalFriend = 0
    var updatePostAtPos = -1
    var inAction = false

    fun clearList() {
        _listViewModel.clear()
    }

    fun addView(icViewModel: ICViewModel) {
        _listViewModel.add(icViewModel)
    }

    fun getPostAt(position: Int): PostViewModel? {
        return try {
            _listViewModel[position] as PostViewModel
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    fun updatePost(icViewModel: ICViewModel) {
        _listViewModel.set(updatePostAtPos, icViewModel)
    }

    fun getPostDetail(): LiveData<ICResponse<ICPost>?> {
        return liveData {
            try {
                val res = ickUserWallRepository.getPostDetail(getPostAt(updatePostAtPos)?.postData?.id
                        ?: 0L)
                emit(res)
            } catch (e: Exception) {
            }
        }

    }

    fun linkFacebook(facebookToken: String): LiveData<ICResponse<*>?> {
        return liveData {
            emit(ickUserWallRepository.linkFacebook(facebookToken))
        }
    }

    fun addView(position: Int, icViewModel: ICViewModel) {
        if (position <= _listViewModel.lastIndex) {
            _listViewModel.add(position, icViewModel)
        } else {
            _listViewModel.add(icViewModel)
        }
    }

    fun finishAddView() {
        listViewModel.postValue(_listViewModel)
    }

    fun getLayout(): LiveData<LayoutResponse?> {
        return liveData {
            emit(ickUserWallRepository.getWallLayout(id))
        }
    }

    fun updateFriendList() {
        _listViewModel.firstOrNull {
            it is IckUserFriendModel
        }?.let {
            it as IckUserFriendModel
            for (item in it.listFriend.data?.rows ?: arrayListOf()) {
                if (!RelationshipManager.checkFriend(item.id ?: -1)) {
                    it.listFriend.data?.rows?.remove(item)
                }
            }
        }
    }

    fun initLayout(layoutResponse: LayoutResponse) {
        viewModelScope.launch {
            if (!layoutResponse.layout.isNullOrEmpty()) {
                clearList()
                val arrJob = arrayListOf<Deferred<Any?>>()
                val arrResponse = arrayListOf<Any?>()
                for (layout in layoutResponse.layout) {
                    when (layout?.id.toString()) {
                        "user-private-info-1" -> {
                            arrJob.add(async {
                                val profile = ickUserWallRepository.getPrivateInfo(layout?.request?.getSocialUrl())
                                if (profile != null) {
                                    userInfo = profile
                                    if (profile.data?.wall?.followedUserCount == null) {
                                        profile.data?.totalFollowed = totalFollowed
                                    }
                                    if (profile.data?.wall?.followingUserCount == null) {
                                        profile.data?.totalFollow = totalFollowing
                                    }
                                    arrResponse.add(profile)
                                }
                            })
                        }
                        "user-public-info-1" -> {
                            arrJob.add(async {
                                val profile = ickUserWallRepository.getPublicInfo(layout?.request?.getSocialUrl())
                                if (profile != null) {
                                    userInfo = profile
                                    arrResponse.add(profile)
                                }
                            })
                        }
                        "general-friend-list-1" -> {
                            arrJob.add(async {
                                val friendList = ickUserWallRepository.getPublicFriend(layout?.request?.getSocialUrl())
                                friendList?.type = 2
                                if (friendList != null) {
                                    arrResponse.add(friendList)
                                }
                            })
                        }
                        "user-friend-list-1" -> {
                            arrJob.add(async {
                                val friendList = ickUserWallRepository.getPublicFriend(layout?.request?.getSocialUrl())
                                if (friendList != null) {
                                    arrResponse.add(friendList)
                                }
                            })
                        }
                        "friend-invitation-list-1" -> {
                            arrJob.add(async {
                                val invitation = ickUserWallRepository.getFriendInvitation(layout?.request?.getSocialUrl())
                                if (invitation != null) {
                                    arrResponse.add(invitation)
                                }
                            })
                        }
                        "friend-suggestion-1" -> {
                            arrJob.add(async {
                                val invitation = ickUserWallRepository.getFriendSuggestion(layout?.request?.getSocialUrl())
                                if (invitation != null) {
                                    arrResponse.add(invitation)
                                }
                            })
                        }
                        "user-post-list-1" -> {
                            arrJob.add(async {
                                try {
                                    val userPosts = ickUserWallRepository.getUserPosts(layout?.request?.getSocialUrl())
                                    totalPost = userPosts?.data?.count!!
                                    arrResponse.add(userPosts)
                                } catch (e: Exception) {
                                    logError(e)
                                }
                            })
                        }
                    }
                }
//                val invitation = ickUserWallRepository.getFriendInvitation(layoutResponse.layout.firstOrNull {
//                    it?.id == "friend-invitation-list-1" || it?.id == "friend-suggestion-1"
//                }?.request?.getSocialUrl())
//                if (invitation != null) {
//                    arrResponse.add(invitation)
//                }
                arrJob.awaitAll()
                for (layout in layoutResponse.layout) {
                    when (layout?.id.toString()) {
                        "user-private-info-1" -> {
                            arrResponse.firstOrNull {
                                it is IckUserInfoResponse
                            }?.let {
                                addView(IckUserProfileModel(it as IckUserInfoResponse).apply {
                                    this.id = this@IckUserWallViewModel.id
                                    if (id != SessionManager.session.user?.id) {
                                        showBottomBar.postValue(false)
                                    }
                                    this.sendInvitation = AppDatabase.getDatabase().friendInvitationMeUserIdDao().getUserByID(this.id) != null
                                })
                            }
                        }
                        "user-public-info-1" -> {
                            arrResponse.firstOrNull {
                                it is IckUserInfoResponse
                            }?.let {
                                addView(IckUserProfileModel(it as IckUserInfoResponse).apply {
                                    this.id = this@IckUserWallViewModel.id
                                    if (id != SessionManager.session.user?.id) {
                                        showBottomBar.postValue(false)
                                    }
                                    this.sendInvitation = AppDatabase.getDatabase().friendInvitationMeUserIdDao().getUserByID(this.id) != null
                                })
                            }
                        }
                        "general-friend-list-1" -> {
                            arrResponse.filterIsInstance<IcFriendResponse>().let { list ->
                                for (item in list.filter { it.type == 2 }) {
                                    if ((item as IcFriendResponse).data?.count ?: 0 > 0) {
                                        totalFriend = (item as IcFriendResponse).data?.count ?: 0
                                        addView(IckUserFriendModel(item as IcFriendResponse).apply {
                                            type = 2
                                            this.wallUserId = userInfo?.data?.id
                                        })
                                    }
                                }
                            }
                        }
                        "user-friend-list-1" -> {
                            arrResponse.filterIsInstance<IcFriendResponse>().let { list ->
                                for (item in list.filter { it.type == 1 }) {
                                    if ((item as IcFriendResponse).data?.count ?: 0 > 0) {
                                        totalFriend = (item as IcFriendResponse).data?.count ?: 0
                                        addView(IckUserFriendModel(item as IcFriendResponse).apply {
                                            type = 1
                                            this.wallUserId = userInfo?.data?.id
                                        })
                                    }
                                }
                            }
                        }
                        "friend-invitation-list-1" -> {
                            arrResponse.firstOrNull {
                                it is ICResponse<*>
                            }?.let {
                                if (!(it as ICResponse<ICListResponse<ICSearchUser>>).data?.rows.isNullOrEmpty()) {
                                    addView(ICWallModel(it.data!!, ICViewTypes.FRIEND_INVITATION_TYPE))
                                }
                            }
                        }
                        "friend-suggestion-1" -> {
                            arrResponse.firstOrNull {
                                it is ICResponse<*>
                            }?.let {
                                if (!(it as ICResponse<ICListResponse<ICUser>>).data?.rows.isNullOrEmpty()) {
                                    addView(ICWallModel(it.data!!, ICViewTypes.FRIEND_SUGGESTION_TYPE))
                                }
                            }
                        }
                    }
                }
//                arrResponse.firstOrNull {
//                    it is IckUserInfoResponse
//                }?.let {
//                    addView(IckUserProfileModel(it as IckUserInfoResponse).apply {
//                        this.id = this@IckUserWallViewModel.id
//                        if (id != SessionManager.session.user?.id) {
//                            showBottomBar.postValue(false)
//                        }
//                        this.sendInvitation = AppDatabase.getDatabase().friendInvitationMeUserIdDao().getUserByID(this.id) != null
//                    })
//                }
//                arrResponse.filterIsInstance<IcFriendResponse>().let { list ->
//                    for(item in list.sortedBy { it.type }) {
//                        if ((item as IcFriendResponse).data?.count ?: 0 > 0) {
//                            totalFriend = (item as IcFriendResponse).data?.count ?: 0
//                            addView(IckUserFriendModel(item as IcFriendResponse).apply {
//                                type = item.type
//                                this.wallUserId = userInfo?.data?.id
//                            })
//                        }
//                    }
//                }
//                arrResponse.firstOrNull {
//                    it is ICResponse<*>
//                }?.let {
//                    if (!(it as ICResponse<ICListResponse<ICSearchUser>>).data?.rows.isNullOrEmpty()) {
//                        addView(ICWallModel(it.data!!, ICViewTypes.FRIEND_INVITATION_TYPE))
//                    }
//                }
                arrResponse.firstOrNull {
                    it is PostResponse
                }?.let {
                    if (id == SessionManager.session.user?.id) {
                        addView(object : ICViewModel {
                            override fun getTag(): String {
                                return ""
                            }

                            override fun getViewType(): Int {
                                return ICViewTypes.ITEM_CREATE_POST
                            }
                        })
                        posCreatePost = _listViewModel.lastIndex
                    }

                    for (item in (it as PostResponse).data?.rows ?: arrayListOf()) {
                        val icViewModel = PostViewModel(item)
                        addView(icViewModel)
                    }
                }
                finishAddView()
                getPosts()
            }

        }
    }

    fun clearPost() {
        val it = _listViewModel.iterator()
        var index = 0
        do {
            if (index < _listViewModel.size && _listViewModel[index] is PostViewModel) {
                it.remove()
            }
            it.next()
            index++
        } while (it.hasNext())
    }

    fun getNewPosts() {
        viewModelScope.launch {
            arrPost.clear()
            updatePost = 1
            val res = ickUserWallRepository.getListPost(id, currentLimitPost, 0)
            postsLiveData.postValue(res)
        }
    }

    fun getPosts() {
        arrPost.clear()
        arrPost.addAll(_listViewModel.filterIsInstance<PostViewModel>())
        if (arrPost.size < totalPost && !reachedEnd) {
            updatePost = 1
            viewModelScope.launch {
                val res = ickUserWallRepository.getListPost(id, currentLimitPost, currentOffsetPost)
                postsLiveData.postValue(res)
            }
        }
    }

    fun getUserPrivacy(): LiveData<ApiResponse<UserPrivacyResponse>> {
        return liveData {
            emit(ickUserWallRepository.getUserPrivacy())
        }
    }

    fun putUserPrivacy(): LiveData<ApiResponse<ResponseBody>> {
        return liveData {

            privacySettings["privacyElementIdList"] = privacy.values.toIntArray()
            emit(ickUserWallRepository.putUserPrivacy(privacySettings))
        }
    }

    fun updatePassword(oldPw: String, newPw: String): LiveData<ApiResponse<RequestOtpResponse>> {
        return liveData {
            emit(ickUserWallRepository.updatePassword(oldPw, newPw))
        }
    }

    fun firstPassword(newPw: String): LiveData<ApiResponse<ICResponse<*>>> {
        return liveData {
            emit(ickUserWallRepository.firstPassword(newPw))
        }
    }

    fun uploadImages(): LiveData<List<WorkInfo>> {
        val arr = hashMapOf<String, File?>()
        if (avatar != null) {
            arr.put("avatar", avatar)
        }
        if (wall != null) {
            arr.put("wall", wall)
        }
        return ickUserWallRepository.uploadListImage(arr)
    }

    fun updateProfile(): LiveData<ApiResponse<RequestOtpResponse?>> {
        return liveData {
            emit(ickUserWallRepository.updateUserInfo(editUserInfo))
        }
    }

    // Get ick user information to set session
    fun getUserInfo(): LiveData<IckUserInfoResponse?> {
        return liveData {
            try {
                val res = ickUserWallRepository.getUserInfo()
                emit(res)
            } catch (e: Exception) {
                emit(null)
            }
        }
    }

    fun getReportUserCategory(): LiveData<ReportUserCategoryResponse?> {
        return liveData {
            emit(ickUserWallRepository.getReportUserCategory())
        }
    }

    fun sendReportUser(): LiveData<ResponseBody?> {
        return liveData {
            val request = hashMapOf<String, Any?>()
            request["userId"] = userInfo?.data?.id
            val arrayId = arrayListOf<Int?>()
            for (item in arrReport) {
                if (item.checked) {
                    arrayId.add(item.data?.id)
                }
                if (item.content.isNotEmpty()) {
                    request["description"] = item.content
                }
            }
            request["reportElementIdList"] = arrayId
            emit(ickUserWallRepository.postReportUser(request))
        }
    }

    fun addFriend(): LiveData<ResponseBody?> {
        return liveData {
            val res = ickUserWallRepository.sendFriendRequest(userInfo?.data?.id)
            emit(res)
        }
    }

    fun removeFriendRequest(): LiveData<ResponseBody?> {
        return liveData {
            val res = ickUserWallRepository.removeFriendRequest(userInfo?.data?.id)
            emit(res)
        }
    }

    private val followingValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    private val followedValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    fun getFacebook(): LiveData<String?> {
        return liveData {
            val value = ickUserWallRepository.getFacebook()
            emit(value)
        }
    }

    fun unFriendUser(): LiveData<ICResponse<*>?> {
        return liveData {
            try {
                val res = ickUserWallRepository.unFriendUser(hashMapOf("userId" to userInfo?.data?.id))
                AppDatabase.getDatabase().myFriendIdDao().deleteUserById(userInfo?.data?.id!!)
                emit(res)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun inviteFriend() {

    }

    fun unFollowUser(): LiveData<ICResponse<*>?> {
        return liveData {
            try {
                val res = ickUserWallRepository.unFollowUser(hashMapOf("targetUserId" to userInfo?.data?.id, "status" to 0))
                emit(res)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun followUser(): LiveData<ICResponse<*>?> {
        return liveData {
            try {
                val res = ickUserWallRepository.unFollowUser(hashMapOf("targetUserId" to userInfo?.data?.id, "status" to 1))
                emit(res)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun deletePost(id: Long): LiveData<ICResponse<ICCommentPost>?> {
        return liveData {
            try {
                val res = ickUserWallRepository.deletePost(id)
                emit(res)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        userPrivacyResponse = null
    }
}