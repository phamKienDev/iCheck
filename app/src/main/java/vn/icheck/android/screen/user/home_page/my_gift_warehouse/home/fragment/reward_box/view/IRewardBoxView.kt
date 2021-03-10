package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_box.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICBoxReward

interface IRewardBoxView :BaseFragmentView{
    fun onRefresh()
    fun onGetDataError(errorMessage: String)
    fun onSetListDataSuccess(list: MutableList<ICBoxReward>,total:Int?, isLoadMore: Boolean)
    fun onLoadMore()
    fun onClickItem(item: ICBoxReward)
}