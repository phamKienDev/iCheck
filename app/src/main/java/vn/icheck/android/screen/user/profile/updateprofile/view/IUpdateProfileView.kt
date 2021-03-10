package vn.icheck.android.screen.user.profile.updateprofile.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICUser
import java.io.File

/**
 * Created by VuLCL on 9/15/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IUpdateProfileView : BaseActivityView {

    fun onShowLoading()
    fun onCloseLoading()

    fun onGetUserDetailError(errorMessage: String)
    fun onGetDataSuccess(user: ICUser)
    fun onSetProvinceName(name: String)
    fun onSetDistrictName(name: String)
    fun onSetWardName(name: String)

    fun onUploadImageSuccess(file: File, type: Int)

    fun onMappingFacebookSuccess()
    fun onConfirmNewPhone(phone: String, json: String?)
    fun onUpdateUserSuccess()
}