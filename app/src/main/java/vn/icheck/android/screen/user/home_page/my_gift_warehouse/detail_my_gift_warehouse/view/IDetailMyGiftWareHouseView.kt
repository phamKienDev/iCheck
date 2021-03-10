package vn.icheck.android.screen.user.home_page.my_gift_warehouse.detail_my_gift_warehouse.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICAcceptGift
import vn.icheck.android.network.models.ICDetailGift

interface IDetailMyGiftWareHouseView :BaseActivityView {
    fun onGetDetailError(type: Int)
    fun getDataDetailGifSuccess(obj: ICDetailGift)
    fun getDataByIntent(idGift: String)
    fun onAcceptDaLayQuaSuccess(obj: ICAcceptGift)
}