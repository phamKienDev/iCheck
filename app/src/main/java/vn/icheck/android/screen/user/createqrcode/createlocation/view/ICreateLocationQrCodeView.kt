package vn.icheck.android.screen.user.createqrcode.createlocation.view

import vn.icheck.android.network.models.ICPointDetail
import vn.icheck.android.network.models.ICPoints
import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView

/**
 * Created by VuLCL on 10/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface ICreateLocationQrCodeView : IBaseCreateQrCodeView {

    fun onSearchLocationSuccess(list: MutableList<ICPoints.Predictions>)
    fun onLocationClicked(obj: ICPoints.Predictions)
    fun onGetLocationDetailSuccess(obj: ICPointDetail.Location)
}

