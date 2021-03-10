package vn.icheck.android.screen.user.detail_stamp_v6_1.select_variant

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1

/**
 * Created by PhongLH on 6/5/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface ISelectVariantView : BaseActivityView {
    fun onGetDataVariantSuccess(products: MutableList<ICVariantProductStampV6_1.ICVariant.ICObjectVariant>, productId: Long, loadMore: Boolean)
    fun onGetDataVariantFail(string: String)
    fun onGetDataError(errorType: Int)
    fun onLoadMore()
    fun onClickItem(item: ICVariantProductStampV6_1.ICVariant.ICObjectVariant)
    fun onRefresh()
}