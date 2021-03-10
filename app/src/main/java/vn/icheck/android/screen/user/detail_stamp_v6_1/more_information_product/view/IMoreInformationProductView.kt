package vn.icheck.android.screen.user.detail_stamp_v6_1.more_information_product.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_RESP_InformationProduct

/**
 * Created by PhongLH on 2/5/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IMoreInformationProductView : BaseActivityView {
    fun onGetDataIntentError(errorType: Int)
    fun onGetInforSuccess(data: IC_RESP_InformationProduct.Data)
}