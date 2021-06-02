package vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import vn.icheck.android.network.models.detail_stamp_v6_1.ICMoreProductVerified
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectDistributor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectVendor
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.view.IMoreBusinessView

/**
 * Created by PhongLH on 12/12/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class MoreBusinessPresenter(val view: IMoreBusinessView) : BaseActivityPresenter(view) {

    private val interactor = DetailStampRepository()

    private var offset = 0

    fun getDataIntent(intent: Intent) {
        val type = intent.getIntExtra(Constant.DATA_1, 0)

        if (type == 1) {
            val itemVendor = intent.getSerializableExtra(Constant.DATA_2) as ICObjectVendor
            view.onGetDataIntentVendorSuccess(itemVendor)
            onGetMoreDataVendor(itemVendor.id)
        } else {
            val itemDistributor = intent.getSerializableExtra(Constant.DATA_2) as ICObjectDistributor
            view.onGetDataIntentDistributorSuccess(itemDistributor)
            onGetMoreDataDistributor(itemDistributor.id)
        }
    }

    var itemType = 1

    fun onGetMoreDataVendor(vendorId: Long?, isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        if (!isLoadMore) {
            itemType = 1
            offset = 0
        }

        interactor.getListMoreProductVerifiedVendor(vendorId, offset, object : ICApiListener<ICMoreProductVerified> {
            override fun onSuccess(obj: ICMoreProductVerified) {

                if (!obj.data?.products.isNullOrEmpty()) {

                    for (i in 0 until obj.data?.products!!.size) {
                        if (itemType == 5) {
                            itemType = 3
                        }

                        obj.data?.products!![i].item_type = itemType
                        itemType++
                    }
                }

                if (!obj.data?.products.isNullOrEmpty()) {
                    offset += APIConstants.LIMIT
                    view.onGetDataProductVerifiedDistributorSuccess(obj.data?.products!!, isLoadMore)
                } else {
                    if (isLoadMore) {
                        view.onGetDataProductVerifiedDistributorSuccess(mutableListOf(), isLoadMore)
                    } else {
                        view.onGetDataIntentError(Constant.ERROR_EMPTY)
                    }
                }
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun onGetMoreDataDistributor(distributorId: Long?, isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        if (!isLoadMore) {
            offset = 0
            itemType = 1
        }

        interactor.getListMoreProductVerifiedDistributorSeccond(distributorId, offset, object : ICApiListener<ICMoreProductVerified> {
            override fun onSuccess(obj: ICMoreProductVerified) {

                if (!obj.data?.products.isNullOrEmpty()) {
                    for (i in 0 until obj.data?.products!!.size) {
                        if (itemType == 5) {
                            itemType = 3
                        }

                        obj.data?.products!![i].item_type = itemType
                        itemType++
                    }
                }

                if (!obj.data?.products.isNullOrEmpty()) {
                    offset += APIConstants.LIMIT
                    view.onGetDataProductVerifiedDistributorSuccess(obj.data?.products!!, isLoadMore)
                } else {
                    if (isLoadMore) {
                        view.onGetDataProductVerifiedDistributorSuccess(mutableListOf(), isLoadMore)
                    } else {
                        view.onGetDataIntentError(Constant.ERROR_EMPTY)
                    }
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