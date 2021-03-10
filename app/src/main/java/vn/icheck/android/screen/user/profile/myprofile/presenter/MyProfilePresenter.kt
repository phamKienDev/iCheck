package vn.icheck.android.screen.user.profile.myprofile.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.follow.FollowInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.profile.myprofile.view.IMyProfileView
import java.io.File

/**
 * Created by VuLCL on 11/11/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class MyProfilePresenter(val view: IMyProfileView) : BaseFragmentPresenter(view) {
    private val userInteraction = UserInteractor()
    private val followInteraction = FollowInteractor()

    private val userID = SessionManager.session.user?.id ?: -1L
    private lateinit var user: ICUser

    fun getUserProfile(isShowLoading: Boolean, isDelay: Boolean) {
        if (userID == -1L) {
            view.onNotLogged()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetUserProfileError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (isShowLoading)
            view.onShowLoading()

        if (isDelay) {
            userInteraction.getUserMeDelay(object : ICApiListener<ICUser> {
                override fun onSuccess(obj: ICUser) {
                    user = obj
                    view.onGetUserProfileSuccess(user)
                    getUserFollowings()
                    SessionManager.updateUser(obj)
                }

                override fun onError(error: ICBaseResponse?) {
                    view.onCloseLoading()
                    val errorMessage = error?.message
                            ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    view.onGetUserProfileError(errorMessage)
                }
            })
        } else {
            userInteraction.getUserMe(object : ICApiListener<ICUser> {
                override fun onSuccess(obj: ICUser) {
                    user = obj
                    view.onGetUserProfileSuccess(user)
                    getUserFollowings()
                    SessionManager.updateUser(obj)
                }

                override fun onError(error: ICBaseResponse?) {
                    view.onCloseLoading()
                    val errorMessage = error?.message
                            ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    view.onGetUserProfileError(errorMessage)
                }
            })
        }
    }

    private fun getUserFollowings() {
        followInteraction.getUserFollowing(userID, "user", 0, 6, object : ICApiListener<ICListResponse<ICUserFollowing>> {
            override fun onSuccess(obj: ICListResponse<ICUserFollowing>) {
                view.onCloseLoading()
                view.onShowFollowing(obj.count, obj.rows)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetUserProfileError(errorMessage)
            }
        })
    }

    val getUserID: Long
        get() {
            return userID
        }

    val getUser: ICUser
        get() {
            return user
        }

    val isPublish: Boolean
        get() {
            return !user.publish_fields.isNullOrEmpty()
        }

    val getListImage: String?
        get() {
            val list = mutableListOf<ICThumbnail>()

            if (!user.avatar_thumbnails?.original.isNullOrEmpty())
                list.add(user.avatar_thumbnails!!)

            if (!user.cover_thumbnails?.original.isNullOrEmpty())
                list.add(user.cover_thumbnails!!)

            return if (list.isNotEmpty())
                JsonHelper.toJson(list)
            else
                null
        }

    fun changeAvatarOrCover(file: File, type: Int) {
        view.onShowLoading()

        ImageHelper.uploadMedia( file, object : ICApiListener<UploadResponse> {
            override fun onSuccess(obj: UploadResponse) {
                val reqUpdateUser = ICReqUpdateUser()

                if (type == 1) {
                    reqUpdateUser.avatar = obj.fileId
                } else if (type == 2) {
                    reqUpdateUser.cover = obj.fileId
                }

                changeAvatarOrCover(reqUpdateUser, file, type)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }

    private fun changeAvatarOrCover(obj: ICReqUpdateUser, file: File, type: Int) {
        userInteraction.updateUser(userID, obj, object : ICApiListener<ICUser> {
            override fun onSuccess(obj: ICUser) {
//                view.onCloseLoading()
                view.onUpdateAvatarOrCoverSuccess(file, type)
                getUserProfile(false, false)
                SessionManager.updateUser(obj)
                InsiderHelper.setUserAttributes()
                when (type) {
                    1 -> {
                        user.avatar = obj.avatar
                    }
                    2 -> {
                        user.cover = obj.cover
                    }
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }

    fun publishProfile(isPublish: Boolean) {
        val obj = ICReqUpdateUser()

        obj.publish_fields = if (isPublish) {
            arrayOf("email", "phone")
        } else {
            arrayOf()
        }

        view.onShowLoading()

        userInteraction.updateUser(userID, obj, object : ICApiListener<ICUser> {
            override fun onSuccess(obj: ICUser) {
                view.onCloseLoading()
                view.onPublishSuccess(isPublish)
                user.publish_fields = obj.publish_fields
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }

    fun sendOtpConfirmPhone() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        if (user.phone.isNullOrEmpty()) {
            showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
            return
        }

        view.onShowLoading()

        userInteraction.sendOtpConfirmPhone(user.phone!!, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onCloseLoading()

                if (obj.status == true) {
                    view.onSendOtpConfirmPhoneSuccess(user.phone!!)
                } else {
                    showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }
}