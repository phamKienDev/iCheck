package vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5.presenter

import android.content.Intent
import android.util.Log
import okhttp3.RequestBody
import okhttp3.ResponseBody
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v5.DetailStampV5Interactor
import vn.icheck.android.network.feature.detail_stamp_v6.DetailStampV6Interactor
import vn.icheck.android.network.models.detail_stamp_v6.ICListHistoryGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICHistoryGuarantee
import vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5.view.IHistoryGuaranteeV5View
import vn.icheck.android.screen.user.detail_stamp_v6.history_guarantee_v6.view.IHistoryGuaranteeV6View

/**
 * Created by PhongLH on 1/3/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class HistoryGuaranteeV5Presenter(val view: IHistoryGuaranteeV5View) : BaseActivityPresenter(view) {

    private val interactor = DetailStampV5Interactor()

    fun getDataIntent(intent: Intent) {
        val qrm = intent.getStringExtra(Constant.DATA_1)
        if (!qrm.isNullOrEmpty()) {
            onGetHistoryGuarantee(qrm)
        } else {
            view.getDataIntentError(Constant.ERROR_EMPTY)
        }
    }

    private fun onGetHistoryGuarantee(qrm: String?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.getDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        if (qrm.isNullOrEmpty()) {
            view.getDataIntentError(Constant.ERROR_EMPTY)
            return
        }

        view.onShowLoading(true)

        interactor.getHistoryGuaranteeV5(qrm, object : ICApiListener<ICListHistoryGuaranteeV6> {
            override fun onSuccess(obj: ICListHistoryGuaranteeV6) {
                view.onShowLoading(false)
                if (obj.status == 200){
                    view.onSetDataSuccess(obj.data)
                }else{
                    view.getDataIntentError(Constant.ERROR_EMPTY)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                view.getDataIntentError(Constant.ERROR_EMPTY)
            }
        })
    }
}