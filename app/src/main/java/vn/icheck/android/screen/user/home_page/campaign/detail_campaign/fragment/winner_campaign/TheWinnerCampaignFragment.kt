package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_the_winner_fragment.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICCampaign_User_Reward
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.adapter.UserRewardCampaignAdapter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.presenter.TheWinnerCampaignPresenter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.view.ITheWinnerCampaignView
import vn.icheck.android.screen.user.wall.IckUserWallActivity

class TheWinnerCampaignFragment : BaseFragment<TheWinnerCampaignPresenter>(), ITheWinnerCampaignView {

    companion object {
        fun newInstance(data: ICDetail_Campaign): Fragment {
            val bundle = Bundle()
            bundle.putSerializable(Constant.DATA_1, data)
            val fragment = TheWinnerCampaignFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var mId: String = ""
    private var adapter: UserRewardCampaignAdapter? = null

    override val getLayoutID: Int
        get() = R.layout.fragment_the_winner_fragment

    override val getPresenter: TheWinnerCampaignPresenter
        get() = TheWinnerCampaignPresenter(this)

    override fun onInitView() {
        initRecyclerview()
        presenter.getDataObject(arguments)
    }

    override fun getDataIntentSuccess(id: String) {
        mId = id
        presenter.getUserRewardCampaign(mId)
    }

    private fun initRecyclerview() {
        adapter = UserRewardCampaignAdapter(this)
        rcvUserReward.layoutManager = LinearLayoutManager(context)
        rcvUserReward.adapter = adapter
    }

    override fun onRefresh() {
        presenter.getUserRewardCampaign(mId)
    }

    override fun onGetDataError(errorMessage: String) {
        adapter?.setErrorCode(errorMessage)
    }

    override fun onClickItem(item: ICCampaign_User_Reward) {
        IckUserWallActivity.create(item.user_id, requireActivity())
    }

    @SuppressLint("SetTextI18n")
    override fun onSetListDataUserRewardCampaignSuccess(list: MutableList<ICCampaign_User_Reward>, isLoadMore: Boolean, count: Int) {
        if (count > 0) {
            tvCountUserReward.text = "Số lượt trúng thưởng: $count"
        } else {
            tvCountUserReward.text = ""
        }

        if (isLoadMore)
            adapter?.addListData(list)
        else
            adapter?.setListData(list)
    }

    override fun onLoadMore() {
        presenter.getUserRewardCampaign(mId, true)
    }
}