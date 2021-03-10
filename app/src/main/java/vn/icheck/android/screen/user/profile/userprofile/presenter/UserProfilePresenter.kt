package vn.icheck.android.screen.user.profile.userprofile.presenter

import android.os.Bundle
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.follow.FollowInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICRespDelete
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.profile.userprofile.view.IUserProfileView

/**
 * Created by VuLCL on 11/11/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class UserProfilePresenter(val view: IUserProfileView) : BaseFragmentPresenter(view) {
    private val interaction = UserInteractor()
    private val followInteraction = FollowInteractor()

    private var userID = -1L
    private lateinit var user: ICUser

    fun getUserID(bundle: Bundle?) {
        userID = try {
            bundle?.getLong(Constant.DATA_1, -1L) ?: -1L
        } catch (e: Exception) {
            -1L
        }

        if (userID != -1L) {
            getUserProfile(true)
        } else {
            view.onNotLogged()
        }
    }

    fun getUserProfile(isDelay: Boolean) {
        if (userID == -1L) {
            view.onNotLogged()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetUserProfileError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        view.onShowLoading()

        if (isDelay) {
            interaction.getUserProfileDelay(userID, object : ICApiListener<ICUser> {
                override fun onSuccess(obj: ICUser) {
                    user = obj
                    view.onGetUserProfileSuccess(user)
                    getUserFollowings()
                }

                override fun onError(error: ICBaseResponse?) {
                    view.onCloseLoading()
                    val errorMessage = error?.message
                            ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    view.onGetUserProfileError(errorMessage)
                }
            })
        } else {
            interaction.getUserProfile(userID, object : ICApiListener<ICUser> {
                override fun onSuccess(obj: ICUser) {
                    user = obj
                    view.onGetUserProfileSuccess(user)
                    getUserFollowings()
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

    val isFollow: Boolean
        get() {
            return user.user_followed
        }

    val getListImage: String?
        get() {
            val list = mutableListOf<ICThumbnail>()

            if (user.avatar_thumbnails != null) {
                list.add(user.avatar_thumbnails!!)
            } else {
                if (user.avatar != null) {
                    val image = ImageHelper.getImageUrl(user.avatar, ImageHelper.originalSize)
                    list.add(ICThumbnail(image, image, image, image, image))
                }
            }

            if (user.cover_thumbnails != null) {
                list.add(user.cover_thumbnails!!)
            } else {
                if (user.cover != null) {
                    val image = ImageHelper.getImageUrl(user.cover, ImageHelper.originalSize)
                    list.add(ICThumbnail(image, image, image, image, image))
                }
            }

            return if (list.isNotEmpty())
                JsonHelper.toJson(list)
            else
                null
        }

    fun addFollowUser() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.onShowLoading()

        followInteraction.addFollowUser(userID, "user", object : ICApiListener<ICUserFollowing> {
            override fun onSuccess(obj: ICUserFollowing) {
                view.onCloseLoading()
                view.onSetFollow(true)
                user.user_followed = true
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }

    fun deleteFollowUser() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.onShowLoading()

        followInteraction.deleteFollowUser(userID, "user", object : ICApiListener<ICRespDelete> {
            override fun onSuccess(obj: ICRespDelete) {
                view.onCloseLoading()
                view.onSetFollow(false)
                user.user_followed = false
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }
}