package vn.icheck.android.screen.user.detail_stamp_v5.more_business_v5.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectBusinessV6
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectOtherProductV6
import vn.icheck.android.network.models.v1.ICBarcodeProductV1

/**
 * Created by PhongLH on 1/3/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IMoreBusinessV5View : BaseActivityView {
    fun onGetDataIntentDistributorSuccess(item: ICObjectBusinessV6, otherProduct: MutableList<ICObjectOtherProductV6>?)
    fun onGetDataIntentVendorSuccess(item: ICBarcodeProductV1.VendorPage)
    fun onClickItem(item: ICObjectOtherProductV6)
}