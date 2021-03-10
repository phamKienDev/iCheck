package vn.icheck.android.helper

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.*
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.*
import vn.icheck.android.util.kotlin.ToastUtils

object RelationshipHelper {

    fun saveData(obj: ICRelationshipsInformation) {
        ICheckApplication.getInstance().applicationContext?.let {
            val pageFollowDao = AppDatabase.getDatabase(it).pageFollowsDao()
            pageFollowDao.deleteAll()
            if (!obj.myFollowingPageIdList.isNullOrEmpty()) {
                for (item in obj.myFollowingPageIdList!!) {
                    pageFollowDao.insertPageFollow(ICMyFollowingPage(item))
                }
            }

            val ownerPage = AppDatabase.getDatabase(it).ownerPageDao()
            ownerPage.deleteAll()
            if (!obj.myOwnerPageIdList.isNullOrEmpty()) {
                for (item in obj.myOwnerPageIdList!!) {
                    ownerPage.insertPageFollow((ICOwnerPage(item)))
                }
            }

            val meFollowUserDao = AppDatabase.getDatabase(it).meFollowUserDao()
            meFollowUserDao.deleteAll()
            if (!obj.myFollowingUserIdList.isNullOrEmpty()) {
                for (item in obj.myFollowingUserIdList!!) {
                    meFollowUserDao.insertMeFollowUser(ICMeFollowUser(item))
                }
            }
//            obj.myFriendIdList?.let { friendList ->
//                ICheckApplication.getInstance().setFriendList(friendList)
//            }

            val myFriendIdList = AppDatabase.getDatabase(it).myFriendIdDao()
            myFriendIdList.deleteAll()
            if (!obj.myFriendIdList.isNullOrEmpty()) {
                for (item in obj.myFriendIdList!!) {
                    myFriendIdList.insertMyFriendIDUser(ICMyFriendIdUser(item))
                }
            }

            val myFriendInvitationUserIdList = AppDatabase.getDatabase(it).myFriendInvitationUserIdDao()
            myFriendInvitationUserIdList.deleteAll()
            if (!obj.myFriendInvitationUserIdList.isNullOrEmpty()) {
                for (item in obj.myFriendInvitationUserIdList!!) {
                    myFriendInvitationUserIdList.insertMyFriendInvitationUserID(ICMyFriendInvitationUserId(item))
                }
            }

            val friendInvitationMeUserIdList = AppDatabase.getDatabase(it).friendInvitationMeUserIdDao()
            friendInvitationMeUserIdList.deleteAll()
            if (!obj.friendInvitationMeUserIdList.isNullOrEmpty()) {
                for (item in obj.friendInvitationMeUserIdList!!) {
                    friendInvitationMeUserIdList.insertFriendInvitationMeUserID(ICFriendInvitationMeUserId(item))
                }
            }
        }
    }

    fun clearTableMyFollowingPage() {
        ICheckApplication.getInstance().applicationContext?.let {
            val pageFollowDao = AppDatabase.getDatabase(it).pageFollowsDao()
            pageFollowDao.deleteAll()
        }
    }

    fun postFollowPage(pageID: Long, listener: ClickFollowPage) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (!SessionManager.isLoggedAnyType) {
                DialogHelper.showLoginPopup(activity)
                return
            }

            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            PageRepository().followPage(pageID, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    listener.onClickFollowPage()
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, error?.message ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }

    private fun checkFollow(tvFollow: LinearLayout?,tvUnFollow: LinearLayout?, isFollow: Boolean) {
        // Text follow
            if (isFollow) {
                tvFollow?.visibility = View.GONE
                tvUnFollow?.visibility = View.VISIBLE
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_FOLLOW_PAGE, true))
            } else {
                tvFollow?.visibility = View.VISIBLE
                tvUnFollow?.visibility = View.GONE
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_FOLLOW_PAGE, false))
            }
    }

    private fun followPage(tvFollow: LinearLayout?,tvUnFollow: LinearLayout?, pageID: Long, isFollow: Boolean, obj: ICPageOverview) {
        postFollowPage(pageID, object : ClickFollowPage {
            override fun onClickFollowPage() {
                obj.isFollow = !isFollow
                checkFollow(tvFollow,tvUnFollow, !isFollow)
            }
        })
    }

    fun initListener(tvFollow: LinearLayout?,tvUnFollow:LinearLayout?, obj: ICPageOverview) {
        if (obj.isFollow) {
            ICheckApplication.currentActivity()?.let { activity ->
                DialogHelper.showConfirm(activity,
                        activity.getString(R.string.bo_theo_doi_trang),
                        activity.getString(R.string.ban_chac_chan_bo_theo_doi_trang_xxx_chu, obj.name),
                        object : ConfirmDialogListener {
                            override fun onDisagree() {}

                            override fun onAgree() {
                                followPage(tvFollow,tvUnFollow, obj.id!!, obj.isFollow, obj)
                            }
                        })
            }
        } else {
            followPage(tvFollow,tvUnFollow, obj.id!!, obj.isFollow, obj)
        }
    }

    fun isFollowPage(obj: ICPageOverview, context: Context) {
        val pageDao = AppDatabase.getDatabase(context).pageFollowsDao()

        val idDB = obj.id?.let { pageDao.getPageFollowByID(it) }

        obj.isFollow = idDB != null
    }

    fun checkFollowPage(obj: ICPageOverview, pageID: Long) {
        val pageDao = AppDatabase.getDatabase().pageFollowsDao()
        obj.isFollow = pageDao.getPageFollowByID(pageID) != null
    }

    fun isFollowPage(list: MutableList<ICRelatedPage>) {
        val pageDao = AppDatabase.getDatabase(ICheckApplication.getInstance()).pageFollowsDao()

        for (obj in list) {
            obj.isFollow = pageDao.getPageFollowByID(obj.id) != null
        }
    }

    fun isFollowBrand(list: MutableList<ICPageTrend>) {
        val pageDao = AppDatabase.getDatabase(ICheckApplication.getInstance()).pageFollowsDao()

        for (obj in list) {
            obj.isFollow = pageDao.getPageFollowByID(obj.id) != null
        }
    }

    interface ClickFollowPage {
        fun onClickFollowPage()
    }
}