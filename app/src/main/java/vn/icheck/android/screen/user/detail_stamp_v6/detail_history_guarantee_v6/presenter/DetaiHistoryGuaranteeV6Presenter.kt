package vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6
import vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.view.IDetaiHistoryGuaranteeV6View

/**
 * Created by PhongLH on 2/17/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class DetaiHistoryGuaranteeV6Presenter(val view: IDetaiHistoryGuaranteeV6View) : BaseActivityPresenter(view) {
    fun getDataIntent(intent: Intent?) {
        val item = intent?.getSerializableExtra(Constant.DATA_1) as RESP_Log_History_v6
        view.getDataIntentSuccess(item)
    }
}