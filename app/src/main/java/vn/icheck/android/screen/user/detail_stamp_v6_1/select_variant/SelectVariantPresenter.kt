package vn.icheck.android.screen.user.detail_stamp_v6_1.select_variant

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampInteractor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1

/**
 * Created by PhongLH on 6/5/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class SelectVariantPresenter(val view: ISelectVariantView) : BaseActivityPresenter(view) {

    private val interactor = DetailStampInteractor()

    private var page = 0

    fun getDataIntent(intent: Intent?) {
        val id = try {
            intent?.getLongExtra(Constant.DATA_1, 0L)
        } catch (e: Exception) {
            0L
        }
        getListVariant(id)
    }

    fun getListVariant(id: Long?, isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(Constant.ERROR_INTERNET)
            return
        }

        if (id != null && id == 0L) {
            view.onGetDataVariantFail(view.mContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        if (!isLoadMore)
            page = 0

        view.onShowLoading(true)

        interactor.getVariantProduct(id!!, page, object : ICApiListener<ICVariantProductStampV6_1> {
            override fun onSuccess(obj: ICVariantProductStampV6_1) {
                view.onShowLoading(false)
                if (obj.data != null) {
                    if (!obj.data?.products.isNullOrEmpty()) {
                        page += APIConstants.LIMIT
                        view.onGetDataVariantSuccess(obj.data?.products!!, id, isLoadMore)
                    } else {
                        view.onGetDataVariantFail(view.mContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                } else {
                    view.onGetDataVariantFail(view.mContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                error?.message?.let {
                    view.onGetDataVariantFail(it)
                }
            }
        })
    }


}