package vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee
import vn.icheck.android.network.models.detail_stamp_v6_1.ICResp_Note_Guarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.view.IDetaiHistoryGuaranteeView

/**
 * Created by PhongLH on 12/12/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class DetailHistoryGuaranteePresenter(val view: IDetaiHistoryGuaranteeView) : BaseActivityPresenter(view) {
    private val interactor = DetailStampRepository()

    fun getObjectIntent(intent: Intent) {
        val item = intent.getSerializableExtra(Constant.DATA_1) as ICListHistoryGuarantee
        getNoteHistoryGuarantee(item)
    }

    private fun getNoteHistoryGuarantee(item: ICListHistoryGuarantee) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.getDataError(Constant.ERROR_INTERNET)
            return
        }

        view.onShowLoading(true)

        if (item._id.isNullOrEmpty()){
            return
        }

        interactor.getListNoteHistoryGuarantee(item._id!!, object : ICApiListener<ICResp_Note_Guarantee> {
            override fun onSuccess(obj: ICResp_Note_Guarantee) {
                view.onShowLoading(false)
                if (!obj.data?.logs?.list.isNullOrEmpty()) {
                    view.getObjectIntentSuccess(item, obj.data?.logs?.list)
                } else {
                    view.getObjectIntentSuccess(item, null)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                view.getObjectIntentSuccess(item, null)
            }
        })
    }

}