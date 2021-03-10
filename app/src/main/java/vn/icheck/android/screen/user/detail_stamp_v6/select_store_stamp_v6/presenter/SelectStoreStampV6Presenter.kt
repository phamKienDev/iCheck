package vn.icheck.android.screen.user.detail_stamp_v6.select_store_stamp_v6.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6.DetailStampV6Interactor
import vn.icheck.android.network.models.detail_stamp_v6.ICStoreStampV6
import vn.icheck.android.screen.user.detail_stamp_v6.select_store_stamp_v6.view.ISelectStoreStampV6View

/**
 * Created by PhongLH on 1/4/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class SelectStoreStampV6Presenter(val view: ISelectStoreStampV6View) : BaseActivityPresenter(view) {

    private var interactor = DetailStampV6Interactor()

    fun getDataIntent(intent: Intent?) {
        val id = intent?.getStringExtra(Constant.DATA_1)
        if (!id.isNullOrEmpty()) {
            onGetListStoreStamp(id)
        }else{
            view.onGetDataError(Constant.ERROR_EMPTY)
        }
    }

    private fun onGetListStoreStamp(id: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(Constant.ERROR_INTERNET)
            return
        }

        view.onShowLoading(true)

        interactor.onGetListStoreStampV6(id,object : ICApiListener<ICStoreStampV6> {
            override fun onSuccess(obj: ICStoreStampV6) {
                view.onShowLoading(false)
                view.onGetListStoreSuccess(obj)
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