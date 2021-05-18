package vn.icheck.android.screen.user.detail_stamp_v6_1.home.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.network.models.detail_stamp_v6_1.*

interface IDetailStampView : BaseActivityView {
    fun onGetDataIntentError(errorType: Int)
    fun onGetDataMoreProductVerifiedError(errorType: Int)
    fun onGetDetailStampSuccess(obj: ICDetailStampV6_1)
    fun onGetBannerSuccess(list: MutableList<ICAds>)
    fun onGetPopupSuccess(list: ICAds)
    fun onGetDataMoreProductVerifiedSuccess(products: MutableList<ICObjectListMoreProductVerified>)
    fun onGetShopVariantSuccess(obj: ICListResponse<ICShopVariantStamp>)
    fun onGetShopVariantFail()
    fun onGetConfigSuccess(obj: IC_Config_Error)
    fun onItemHotlineClick(hotline: String?)
    fun onItemEmailClick(email: String?)
    fun itemPagerClick(list: String, position: Int)
    fun onItemClick(item: ICObjectListMoreProductVerified)
    fun onAddToCartSuccess(type: Int)
    fun onTryAgain()
    fun itemPagerClickToVideo(urlVideo: String?)
    fun onGetDataRequireLogin()
}