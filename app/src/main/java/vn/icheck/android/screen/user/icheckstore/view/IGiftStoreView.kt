package vn.icheck.android.screen.user.icheckstore.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.network.models.ICListGifExchange
import vn.icheck.android.network.models.ICStoreiCheck

/**
 * Created by PhongLH on 12/18/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IGiftStoreView  {
    fun onClickItem(item: ICStoreiCheck)
    fun onExchangeGift(item: ICStoreiCheck)
    fun onLogin()
}