package vn.icheck.android.screen.user.detail_stamp_v6.history_guarantee_v6.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6.ICListHistoryGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6.ObjectLogHistoryV6
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee

/**
 * Created by PhongLH on 1/3/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IHistoryGuaranteeV6View : BaseActivityView {
    fun getDataIntentError(errorType: Int)
    fun setOnItemClick(item: RESP_Log_History_v6)
    fun onSetDataSuccess(data: ObjectLogHistoryV6?)
}