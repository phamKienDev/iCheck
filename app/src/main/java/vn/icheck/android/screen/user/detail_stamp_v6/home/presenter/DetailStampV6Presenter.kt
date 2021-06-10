package vn.icheck.android.screen.user.detail_stamp_v6.home.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.detail_stamp_v6.DetailStampV6Interactor
import vn.icheck.android.network.models.detail_stamp_v6.ICDetailStampV6
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_Config_Error
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_v6.home.view.IDetailStampV6View
import java.net.URL

/**
 * Created by PhongLH on 12/30/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class DetailStampV6Presenter(val view: IDetailStampV6View) : BaseActivityPresenter(view) {

    private var interactor = DetailStampV6Interactor()
    private var mId: String? = null
    var code: String? = null

    fun onGetDataIntent(intent: Intent?) {
        val data = try {
            intent?.getStringExtra("data")
        } catch (e: Exception) {
            ""
        }

        if (!data.isNullOrEmpty()) {
            var path = URL(data).path

            if (path.isNotEmpty() && path.first() == '/') {
                path = path.removeRange(0, 1)
            }

            when {
                path.contains("1.0") -> {
                    code = path
                    onGetDataDetailStampQRMV6(path.split(".").last())
                }
                path.contains("1.1") -> {
                    code = path
                    onGetDataDetailStampQRIV6(path.split(".").last())
                }
                else -> {
                    view.onGetDataIntentError(Constant.ERROR_UNKNOW)
                }
            }
        } else {
            view.onGetDataIntentError(Constant.ERROR_UNKNOW)
        }
    }

    private fun onGetDataDetailStampQRMV6(code: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        val id = SessionManager.session.user?.id
        if (id != null) {
            mId = "i-$id"
        }

        view.onShowLoading(true)

        interactor.onGetDataStampQRMV6(code, mId, object : ICApiListener<ICDetailStampV6> {
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

    private fun onGetDataDetailStampQRIV6(code: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        val id = SessionManager.session.user?.id
        if (id != null) {
            mId = "i-$id"
        }

        view.onShowLoading(true)

        interactor.onGetDataStampQRIV6(code,mId, object : ICApiListener<ICDetailStampV6> {
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