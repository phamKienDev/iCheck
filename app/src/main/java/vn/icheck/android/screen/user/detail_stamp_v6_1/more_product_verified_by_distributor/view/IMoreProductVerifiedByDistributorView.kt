package vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified

/**
 * Created by PhongLH on 1/21/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IMoreProductVerifiedByDistributorView : BaseActivityView {
    fun onGetDataError(errorType: Int)
    fun onGetDataMoreProductVerifiedSuccess(products: MutableList<ICObjectListMoreProductVerified>, isLoadMore: Boolean)
    fun onLoadMore()
    fun onRefresh()
    fun onClickItem(item: ICObjectListMoreProductVerified)
}