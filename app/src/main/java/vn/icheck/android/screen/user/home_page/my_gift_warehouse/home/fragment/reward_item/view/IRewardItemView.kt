package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICItemReward

interface IRewardItemView :BaseFragmentView {
    fun onRefresh()
    fun onGetDataError(errorMessage: String)
    fun onSetListDataSuccess(list: MutableList<ICItemReward>,count:Int, isLoadMore: Boolean)
    fun onLoadMore()
    fun onClickItem(item: ICItemReward)
}