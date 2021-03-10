package vn.icheck.android.screen.user.selectuseraddress.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.network.models.ICRespID
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.selectuseraddress.view.ISelectUserAddressView

/**
 * Created by VuLCL on 12/27/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SelectUserAddressPresenter(val view: ISelectUserAddressView) : BaseActivityPresenter(view) {
    private val userInteraction = UserInteractor()

    private var defaultAddressID: Long? = null

    fun getData(intent: Intent?) {
        val addressID = try {
            intent?.getLongExtra(Constant.DATA_2, -1L)
        } catch (e: Throwable) {
            null
        }

        defaultAddressID = addressID
        view.onSetAddressID(defaultAddressID)
    }

    fun getListAddress() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetAddressError(R.drawable.ic_no_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        userInteraction.getListUserAddress(object : ICApiListener<ICListResponse<ICAddress>> {
            override fun onSuccess(obj: ICListResponse<ICAddress>) {
                view.onSetAddress(obj.rows)
            }

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetAddressError(R.drawable.ic_error_request, message)
            }
        })
    }

    fun getCreatedAddress(intent: Intent?) {
        val address = try {
            JsonHelper.parseJson(intent?.getStringExtra(Constant.DATA_1), ICAddress::class.java)
        } catch (e: Exception) {
            null
        }

        if (address != null) {
            view.onAddAddressSuccess(address)
        }
    }

    fun deleteAddress(id: Long) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.onShowLoading()

        userInteraction.deleteUserAddress(id, object : ICApiListener<ICRespID> {
            override fun onSuccess(obj: ICRespID) {
                if (id == defaultAddressID) {
                    defaultAddressID = null
                }

                view.onCloseLoading()
                view.onDeleteAddressSuccess(id)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }

    val getDefaultAddressID: Long?
        get() {
            return defaultAddressID
        }
}