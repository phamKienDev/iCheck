package vn.icheck.android.screen.user.detail_stamp_v5.home.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.detail_stamp_v5.DetailStampV5Interactor
import vn.icheck.android.network.feature.detail_stamp_v6.DetailStampV6Interactor
import vn.icheck.android.network.models.ICBarcodeProductV2
import vn.icheck.android.network.models.detail_stamp_v6.ICDetailStampV6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICDetailStampV6_1
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_Config_Error
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_v5.home.view.IDetailStampV5View

/**
 * Created by PhongLH on 12/30/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class DetailStampV5Presenter(val view: IDetailStampV5View) : BaseActivityPresenter(view) {

    private var interactor = DetailStampV5Interactor()
    private var mId: String? = null
    var mCode: String? = null

    fun onGetDataIntent(intent: Intent?) {
        val data = try {
            intent?.getStringExtra("data")
        } catch (e: Exception) {
            ""
        }

        if (!data.isNullOrEmpty()) {
            val separated: List<String> = data.split("/")
            mCode = separated[3]
            onGetDataDetailStampV5(separated[3])
        } else {
            val code = intent?.getStringExtra(Constant.DATA_1)

            if (code.isNullOrEmpty()) {
                view.onGetDataIntentError(Constant.ERROR_UNKNOW)
            } else {
                mCode = code
                onGetDataDetailStampV5(code)
            }
        }
    }

    private fun onGetDataDetailStampV5(code: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        val id = SessionManager.session.user?.id
        if (id != null) {
            mId = "i-$id"
        }

        view.onShowLoading(true)

        interactor.onGetDataStampV6(code, mId, object : ICApiListener<ICDetailStampV6> {
            override fun onSuccess(obj: ICDetailStampV6) {
                view.onShowLoading(false)
                view.onGetDetailStampQRMSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun getProductBySku(sku: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        interactor.getProductBySku(sku, object : ICApiListener<ICBarcodeProductV1> {
            override fun onSuccess(obj: ICBarcodeProductV1) {
                view.onGetProductBySkuSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun getConfigError() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        interactor.getConfigError(object : ICApiListener<IC_Config_Error> {
            override fun onSuccess(obj: IC_Config_Error) {
                if (obj.data != null) {
                    view.onGetConfigSuccess(obj)
                } else {
                    view.onGetDataIntentError(Constant.ERROR_UNKNOW)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }
}