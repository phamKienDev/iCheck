package vn.icheck.android.screen.user.profile.myprofile.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.screen.user.campaign.calback.IFollowingListener
import java.io.File

/**
 * Created by VuLCL on 11/11/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IMyProfileView : BaseFragmentView, IFollowingListener {

    fun onNotLogged()
    fun onShowLoading()
    fun onCloseLoading()

    fun onSendOtpConfirmPhoneSuccess(phone: String)

    fun onGetUserProfileError(error: String)
    fun onGetUserProfileSuccess(user: ICUser)
    fun onShowFollowing(totalCount: Int, list: MutableList<ICUserFollowing>)

    fun onPublishSuccess(isPublish: Boolean)
    fun onUpdateAvatarOrCoverSuccess(file: File, type: Int)
}