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