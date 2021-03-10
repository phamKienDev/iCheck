package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICRelationshipsInformation {
    @Expose
    val userId: String? = null

    @Expose
    val myOwnerPageIdList: List<Long>? = null

    @Expose
    val myEditorPageIdList: List<Long>? = null

    @Expose
    val myMemberPageIdList: List<Long>? = null

    @Expose
    val myFollowingPageIdList: List<Long>? = null

    @Expose
    val myFriendIdList: List<Long>? = null

    @Expose
    val myFollowingUserIdList: List<Long>? = null

    @Expose
    val userFollowingMeIdList: List<Long>? = null

    @Expose
    val myFriendInvitationUserIdList: List<Long>? = null

    @Expose
    val friendInvitationMeUserIdList: List<Long>? = null
}