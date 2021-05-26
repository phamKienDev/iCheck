package vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import vn.icheck.android.network.models.detail_stamp_v6_1.ICMoreProductVerified
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.view.IMoreProductVerifiedByDistributorView

/**
 * Created by PhongLH on 1/21/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class MoreProductVerifiedByDistributorPresenter(val view: IMoreProductVerifiedByDistributorView) : BaseActivityPresenter(view) {

    private val interactor = DetailStampRepository()

    var id:Long? = null
    private var offset = 0

    fun getDataIntent(intent: Intent?) {
        id = try {
            intent?.getLongExtra(Constant.DATA_1,-1)
        }catch (e:Exception){
            -1
        }

        if (id == null || id == -1L){
            view.onGetDataError(Constant.ERROR_UNKNOW)
        }else{
            onGetProductVerified(id)
        }
    }

    fun onGetProductVerified(id: Long?,isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(Constant.ERROR_INTERNET)
            return
        }

        if (!isLoadMore)
            offset = 0

        interactor.getListMoreProductVerifiedDistributorSeccond(id,offset, object : ICApiListener<ICMoreProductVerified> {
            override fun onSuccess(obj: ICMoreProductVerified) {
                if (obj.data?.products != null && !obj.data?.products.isNullOrEmpty()) {
                    offset += APIConstants.LIMIT
                    view.onGetDataMoreProductVerifiedSuccess(obj.data?.products!!,isLoadMore)
                }else{
                    view.onGetDataError(Constant.ERROR_EMPTY)
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