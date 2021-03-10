package vn.icheck.android.screen.account.verifyfacebookphone.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICStatus
import vn.icheck.android.screen.account.verifyfacebookphone.view.IVerifyFacebookPhoneView

/**
 * Created by VuLCL on 9/18/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class VerifyFacebookPhonePresenter(val view: IVerifyFacebookPhoneView) : BaseActivityPresenter(view) {
    private val interaction = UserInteractor()

    private var facebookToken: String? = null

    fun getData(intent: Intent?) {
        facebookToken = try {
            intent?.getStringExtra(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }

        val name = try {
            intent?.getStringExtra(Constant.DATA_2)
        } catch (e: Exception) {
            null
        }

        val avatar = try {
            intent?.getStringExtra(Constant.DATA_3)
        } catch (e: Exception) {
            null
        }

        view.onSetUserInfo(name, avatar)
    }

    fun validPhone(phone: String) {
        val validPhone = ValidHelper.validPhoneNumber(view.mContext, phone)

        if (validPhone != null) {
            view.onErrorPhone(validPhone)
            return
        } else {
            view.onErrorPhone("")
        }

        view.onShowLoading()

        interaction.sendOtpConfirmPhone(phone, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onCloseLoading()

                if (obj.status == true) {
                    view.onRegisterFacebookPhone(facebookToken)
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