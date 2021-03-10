package vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6

/**
 * Created by PhongLH on 2/17/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IDetaiHistoryGuaranteeV6View : BaseActivityView {
    fun getDataIntentSuccess(item: RESP_Log_History_v6)

}