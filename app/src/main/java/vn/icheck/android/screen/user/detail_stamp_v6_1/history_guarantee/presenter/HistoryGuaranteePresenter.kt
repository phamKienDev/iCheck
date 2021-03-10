package vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampInteractor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICHistoryGuarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.view.IHistoryGuaranteeView

/**
 * Created by PhongLH on 12/12/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class HistoryGuaranteePresenter(val view: IHistoryGuaranteeView) : BaseActivityPresenter(view) {

    private val interactor = DetailStampInteractor()

    fun getDataIntent(intent: Intent){
        var serial = try {
            intent.getStringExtra(Constant.DATA_1)
        }catch (e:Exception){
            ""
        }

        serial = serial.replace("Serial: ","").replace(" ","")

        if (!serial.isNullOrEmpty()){
            getDataHistoryGuarantee(serial)
        }else{
            view.getDataIntentError(Constant.ERROR_UNKNOW)
        }
    }

    private fun getDataHistoryGuarantee(serial: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.getDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        view.onShowLoading(true)

        interactor.getListHistoryGuarantee(serial,object : ICApiListener<ICHistoryGuarantee>{
            override fun onSuccess(obj: ICHistoryGuarantee) {
                view.onShowLoading(false)
                if (!obj.data.isNullOrEmpty()) {
                    view.onGetDataHistoryGuaranteeSuccess(obj.data!!)
                }else{
                    view.onGetDataHistoryGuaranteeFail()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }
}