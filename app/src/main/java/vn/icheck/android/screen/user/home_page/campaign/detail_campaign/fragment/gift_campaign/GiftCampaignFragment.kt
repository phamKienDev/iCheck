package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_gift_campaign.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICCampaign_Reward
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.adapter.RewardCampaignAdapter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.presenter.GiftCampaignPresenter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.view.IGiftCampaignView

class GiftCampaignFragment : BaseFragment<GiftCampaignPresenter>(), IGiftCampaignView {

    companion object {
        fun newInstance(data: ICDetail_Campaign): Fragment {
            val bundle = Bundle()
            bundle.putSerializable(Constant.DATA_1, data)
            val fragment = GiftCampaignFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var adapter: RewardCampaignAdapter? = null

    private var mId: String = ""

    override val getLayoutID: Int
        get() = R.layout.fragment_gift_campaign

    override val getPresenter: GiftCampaignPresenter
        get() = GiftCampaignPresenter(this)

    override fun onInitView() {
        initRecylerview()
        presenter.getDataObject(arguments)
    }

    private fun initRecylerview() {
        adapter = RewardCampaignAdapter(this)
        rcvGiftCampaign.layoutManager = LinearLayoutManager(context)
        rcvGiftCampaign.adapter = adapter
    }

    override fun onSetListDataCampaignRewardSuccess(data: MutableList<ICCampaign_Reward>, loadMore: Boolean, id: String) {
        mId = id

        if (loadMore)
            adapter?.addListData(data)
        else
            adapter?.setListData(data)
    }

    override fun onLoadMore() {
        presenter.getGiftCampaign(mId,true)
    }

    override fun onGetDataError(errorMessage: String) {
        adapter?.setErrorCode(errorMessage)
    }
}