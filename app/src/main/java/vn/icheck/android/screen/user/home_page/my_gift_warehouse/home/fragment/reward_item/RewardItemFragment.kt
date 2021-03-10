package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_item_reward.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.detail_my_gift_warehouse.DetailMyGiftWareHouseActivity
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item.adapter.ItemRewardAdapter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item.presenter.RewardItemPresenter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item.view.IRewardItemView

class RewardItemFragment : BaseFragment<RewardItemPresenter>(), IRewardItemView {

    private var adapter: ItemRewardAdapter? = null

    override val getLayoutID: Int
        get() = R.layout.fragment_item_reward

    override val getPresenter: RewardItemPresenter
        get() = RewardItemPresenter(this)

    override fun onInitView() {
        shimmer_view_container.startShimmer()
        initRecyclerview()
        presenter.getDataItemReward()
    }

    private fun initRecyclerview() {
        adapter = ItemRewardAdapter(this)
        rcvItemReward.layoutManager = LinearLayoutManager(context)
        rcvItemReward.adapter = adapter
    }

    override fun onRefresh() {
        presenter.getDataItemReward()
    }

    override fun onGetDataError(errorMessage: String) {
        adapter?.setErrorCode(errorMessage)
    }

    @SuppressLint("SetTextI18n")
    override fun onSetListDataSuccess(list: MutableList<ICItemReward>, count: Int, isLoadMore: Boolean) {
        shimmer_view_container.stopShimmer()
        shimmer_view_container.visibility = View.GONE
        rcvItemReward.visibility = View.VISIBLE

        if (isLoadMore)
            adapter?.addListData(list)
        else
            adapter?.setListData(list)

        if (!list.isNullOrEmpty()) {
            tvCountItemReward?.visibility = View.VISIBLE
            tvCountItemReward?.text = "Sản phẩm quà: $count"
        }
    }

    override fun onLoadMore() {
        presenter.getDataItemReward(true)
    }

    override fun onClickItem(item: ICItemReward) {
        val intent = Intent(context, DetailMyGiftWareHouseActivity::class.java)
        intent.putExtra(Constant.DATA_1, item.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        presenter.getDataItemReward()
    }
}