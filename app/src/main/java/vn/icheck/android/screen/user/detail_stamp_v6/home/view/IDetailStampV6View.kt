package vn.icheck.android.screen.user.detail_stamp_v6.home.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6.ICDetailStampV6
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_Config_Error
import vn.icheck.android.network.models.v1.ICBarcodeProductV1

/**
 * Created by PhongLH on 12/30/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IDetailStampV6View : BaseActivityView {
    fun onGetDataIntentError(errorType: Int)
    fun onGetDetailStampQRMSuccess(obj: ICDetailStampV6)
    fun onGetProductBySkuSuccess(obj: ICBarcodeProductV1)
    fun onGetConfigSuccess(obj: IC_Config_Error)
    fun onItemHotlineClick(hotline: String?)
    fun onItemEmailClick(email: String?)
    fun itemPagerClick(list: String, position: Int)
}