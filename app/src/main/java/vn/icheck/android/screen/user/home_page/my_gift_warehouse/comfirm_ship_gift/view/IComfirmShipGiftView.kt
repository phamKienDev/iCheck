package vn.icheck.android.screen.user.home_page.my_gift_warehouse.comfirm_ship_gift.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICAcceptGift
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.network.models.ICDetailGift
import vn.icheck.android.network.models.ICDetailGiftStore

interface IComfirmShipGiftView : BaseActivityView {
    fun onGetDetailError(errorType: Int, message: String?)
    fun onGetDataIntentSuccess(obj1: ICDetailGift?, obj2: ICDetailGiftStore?, pointRemaining: Long?, pointExchange: Long?, type: Int?)
    fun onNoAddress()
    fun onChangeUserAddress(address: ICAddress)
    fun onGetAddressDefaultSuccess(address: ICAddress)
    fun onShipGiftSuccess(obj: ICAcceptGift)
    fun onShipGiftExchangeStoreSuccess(obj: ICAcceptGift)
    fun onShipGiftFail(message: String?)
    fun onSetAddressIdDefaul()
}