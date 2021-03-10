package vn.icheck.android.network.models

data class ICFriendNofollowPage(
        val count: Int? = null,
        val invitationCount: Int? = null,
        val maxInvitationCount: Int? = null,
        val rows: MutableList<ICUser>? = null,
        var pageId: Long? = null,
        var listHideIds: Set<String>? = null
)

