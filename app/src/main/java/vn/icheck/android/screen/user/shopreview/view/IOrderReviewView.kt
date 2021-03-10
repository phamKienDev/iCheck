package vn.icheck.android.screen.user.shopreview.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICCriteriaShop
import vn.icheck.android.screen.user.shopreview.entity.ShopReviewImage

/**
 * Created by VuLCL on 2/2/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IOrderReviewView : BaseActivityView {

    fun onGetShopIDError()
    fun onGetCriteriaError(error: String)
    fun onGetCriteriaSuccess(list: MutableList<ICCriteriaShop>)

    fun onShowLoading()
    fun onCloseLoading()

    fun onAddImage()
    fun onAddImageSuccess(obj: ShopReviewImage)
    fun onDeleteImage(position: Int)

    fun onReviewSuccess()
}