package vn.icheck.android.screen.user.updateaddress.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.room.entity.ICDistrict
import vn.icheck.android.room.entity.ICProvince
import vn.icheck.android.room.entity.ICWard
import java.util.*

/**
 * Created by VuLCL on 12/28/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IUpdateUserAddressView : BaseActivityView {

    fun onShowLoading()
    fun onCloseLoading()

    fun onGetIDError()
    fun onGetAddressError(error: String)
    fun onGetAddressSuccess(address: ICAddress)

    fun onSetProvince(name: String)
    fun onSetDistrict(name: String)
    fun onSetWard(name: String)

    fun onErrorLastName(error: String)
    fun onErrorFirstName(error: String)
    fun onErrorPhone(error: String)
    fun onErrorProvince(error: String)
    fun onErrorDistrict(error: String)
    fun onErrorWard(error: String)
    fun onErrorAddress(error: String)

    fun onCreateAddressSuccess(json: String)
}