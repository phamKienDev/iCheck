package vn.icheck.android.screen.user.selectuseraddress.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICAddress

/**
 * Created by VuLCL on 12/27/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface ISelectUserAddressView : BaseActivityView {

    fun onSetAddressID(addressID: Long?)

    fun onGetAddressError(icon: Int, error: String)
    fun onSetAddress(list: MutableList<ICAddress>)

    fun onShowLoading()
    fun onCloseLoading()

    fun onMessageClicked()
    fun onAddUserAddress()

    fun onDeleteAddress(id: Long)
    fun onDeleteAddressSuccess(id: Long)
    fun onAddAddressSuccess(obj: ICAddress)
}