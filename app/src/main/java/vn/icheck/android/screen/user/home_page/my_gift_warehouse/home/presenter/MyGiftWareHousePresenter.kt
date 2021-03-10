package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.presenter

import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_box.RewardBoxFragment
import vn.icheck.android.screen.user.mygift.fragment.reward_item_v2.MyGiftsFragment
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.view.IMyGiftWareHouseView

class MyGiftWareHousePresenter(val view: IMyGiftWareHouseView) : BaseActivityPresenter(view) {
    val listFragment: MutableList<ICFragment>
        get() {
            val list = mutableListOf<ICFragment>()

            list.add(ICFragment(view.mContext.getString(R.string.hop_qua), RewardBoxFragment()))
            list.add(ICFragment(view.mContext.getString(R.string.san_pham_qua), MyGiftsFragment()))

            return list
        }
}