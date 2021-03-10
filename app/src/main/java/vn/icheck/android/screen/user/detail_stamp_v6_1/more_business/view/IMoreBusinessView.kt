package vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectDistributor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectVendor

/**
 * Created by PhongLH on 12/12/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IMoreBusinessView : BaseActivityView {
    fun onGetDataIntentVendorSuccess(itemVendor: ICObjectVendor)
    fun onGetDataIntentDistributorSuccess(itemDistributor: ICObjectDistributor)
    fun onGetDataIntentError(typeError: Int)
    fun onGetDataProductVerifiedDistributorSuccess(products: MutableList<ICObjectListMoreProductVerified>, loadMore: Boolean)
    fun onItemClick(item: ICObjectListMoreProductVerified)
    fun onClickTryAgain()
    fun onLoadMore()
    fun onClickPhone(phone: String)
    fun onClickEmail(email: String)
    fun onClickWebsite(website: String)
}