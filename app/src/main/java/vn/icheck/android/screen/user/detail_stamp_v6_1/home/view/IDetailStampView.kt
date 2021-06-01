package vn.icheck.android.screen.user.detail_stamp_v6_1.home.view

import vn.icheck.android.network.models.detail_stamp_v6_1.ICDetailStampV6_1
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified

interface IDetailStampView {
    fun onGetDetailStampSuccess(obj: ICDetailStampV6_1)
//    fun onGetConfigSuccess(obj: IC_Config_Error)
    fun onItemClick(item: ICObjectListMoreProductVerified)
    fun onAddToCartSuccess(type: Int)
    fun itemPagerClickToVideo(urlVideo: String?)
    fun onGetDataRequireLogin()
}