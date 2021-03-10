package vn.icheck.android.screen.user.detail_stamp_v6_1.more_information_product.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampInteractor
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_RESP_InformationProduct
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_information_product.view.IMoreInformationProductView

/**
 * Created by PhongLH on 2/5/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class MoreInformationProductPresenter(val view: IMoreInformationProductView) : BaseActivityPresenter(view) {

    private val interactor = DetailStampInteractor()

    fun getDataIntent(intent: Intent?) {
        val id = intent?.getLongExtra(Constant.DATA_1, 0)

        if (id != null && id != 0L) {
            onGetInforProduct(id)
        } else {
            view.onGetDataIntentError(Constant.ERROR_EMPTY)
        }
    }

    private fun onGetInforProduct(id: Long) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        view.onShowLoading(true)

        interactor.getInforProductById(id, object : ICApiListener<IC_RESP_InformationProduct> {
            override fun onSuccess(obj: IC_RESP_InformationProduct) {
                view.onShowLoading(false)
                if (obj.status == 200) {
                    if (obj.data != null) {
                        view.onGetInforSuccess(obj.data!!)
                    } else{
                        view.onGetDataIntentError(Constant.ERROR_EMPTY)
                    }
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