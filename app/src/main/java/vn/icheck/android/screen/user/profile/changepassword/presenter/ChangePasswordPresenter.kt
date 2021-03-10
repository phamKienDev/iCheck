package vn.icheck.android.screen.user.profile.changepassword.presenter

import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.auth.AuthInteractor
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.user.profile.changepassword.view.ChangePasswordView

/**
 * Created by VuLCL on 11/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ChangePasswordPresenter(val view: ChangePasswordView) : BaseActivityPresenter(view) {

    fun changePassword(oldPassword: String, password: String, rePassword: String) {
        val validOldPassword = ValidHelper.validPassword(view.mContext, oldPassword)

        var isValidSuccess = true

        if (validOldPassword != null) {
            isValidSuccess = false
            view.onErrorOldPassword(validOldPassword)
        } else {
            view.onErrorOldPassword("")
        }

        val validPassword = ValidHelper.validPassword(view.mContext, password)

        if (validPassword != null) {
            isValidSuccess = false
            view.onErrorPassword(validPassword)
        } else {
            view.onErrorPassword("")
        }

        val validRePassword = ValidHelper.validRePassword(view.mContext, password, rePassword)

        if (validRePassword != null) {
            isValidSuccess = false
            view.onErrorRePassword(validRePassword)
        } else {
            view.onErrorRePassword("")
        }

        if (!isValidSuccess) {
            return
        }

        view.onShowLoading()

        AuthInteractor().changePassword(oldPassword, password, object : ICApiListener<ICUser> {
            override fun onSuccess(obj: ICUser) {
                view.onCloseLoading()
                view.onChangePasswordSuccess()
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()

                val errorMessage = if (error?.statusCode == 400) {
                    getString(R.string.mat_khau_hien_tai_khong_dung)
                } else {
                    getString(R.string.doi_mat_khau_khong_thanh_cong_vui_long_thu_lai)
                }

                showError(errorMessage)
            }
        })
    }
}