package vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee

interface IHistoryGuaranteeView : BaseActivityView {
    fun getDataIntentError(errorType: Int)
    fun onGetDataHistoryGuaranteeSuccess(data: MutableList<ICListHistoryGuarantee>)
    fun onGetDataHistoryGuaranteeFail()
    fun setOnItemClick(item: ICListHistoryGuarantee)
}