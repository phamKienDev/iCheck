package vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee
import vn.icheck.android.network.models.detail_stamp_v6_1.ICResp_Note_Guarantee

/**
 * Created by PhongLH on 12/12/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IDetaiHistoryGuaranteeView : BaseActivityView {
    fun getObjectIntentSuccess(item: ICListHistoryGuarantee, list: MutableList<ICResp_Note_Guarantee.ObjectLog.ObjectChildLog.ICItemNote>?)
    fun getDataError(errorInternet: Int)

}