package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_infor_campaign.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter.ConditionsAdapter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter.SponsorsAdapter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter.UserRankAdapter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.presenter.InforCampaignPresenter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.view.IInforCampaignView
import vn.icheck.android.util.kotlin.GlideImageGetter
import vn.icheck.android.util.kotlin.WidgetUtils

class InforCampaignFragment : BaseFragment<InforCampaignPresenter>(), IInforCampaignView, View.OnClickListener {

    companion object {
        fun newInstance(data: ICDetail_Campaign): Fragment {
            val bundle = Bundle()
            bundle.putSerializable(Constant.DATA_1, data)
            val fragment = InforCampaignFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var mId: String = ""

    private var adapterCondition: ConditionsAdapter? = null
    private var adapterRank: UserRankAdapter? = null
    private var adapterSponsors: SponsorsAdapter? = null

    override val getLayoutID: Int
        get() = R.layout.fragment_infor_campaign

    override val getPresenter: InforCampaignPresenter
        get() = InforCampaignPresenter(this)

    override fun onInitView() {
        initRecylerview()
        presenter.getDataObject(arguments)
        WidgetUtils.setClickListener(this, btnJoinCampaign)
    }

    private fun initRecylerview() {
//        adapterCondition = ConditionsAdapter(context)
        rcvConditions.layoutManager = LinearLayoutManager(context)
        rcvConditions.adapter = adapterCondition

        adapterRank = UserRankAdapter(context)
        rcvRank.layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
        rcvRank.adapter = adapterRank

//        adapterSponsors = SponsorsAdapter(this)
        val layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        rcvSponsSors.layoutManager = layoutManager
        rcvSponsSors.adapter = adapterSponsors

//        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(p0: Int): Int {
//                return if (adapterSponsors?.listCondition!!.isEmpty())
//                    3
//                else
//                    1
//            }
//        }
    }

    override fun getDataSuccess(obj: ICDetail_Campaign) {
        if (obj.id != null) {
            mId = obj.id!!
        }

        if (obj.information.isNullOrEmpty()) {
            layoutInfor.visibility = View.GONE
        } else {
            var start = -1
            var end = -1
            var textResult = ""
            obj.information!!.indexOf("<style").let {
                start = it
            }
            obj.information!!.indexOf("</style>").let {
                end = it + 8
            }

            textResult = if (start != -1 && end != -1) {
                obj.information!!.replace(obj.information!!.substring(start, end), "")
            } else {
                obj.information!!
            }

            val imageGetter = GlideImageGetter(tvInfoCampaign)
            val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(textResult, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
            } else {
                Html.fromHtml(textResult, imageGetter, null)
            }
            tvInfoCampaign.text = html
//        tvInfoCampaign.post {
//            val lineCount = tvInfoCampaign.lineCount
//            if (lineCount < 5) {
//                toggle_expand.visibility = View.GONE
//            } else {
//                toggle_expand.visibility = View.VISIBLE
//            }
//        }
        }

        if (obj.guide.isNullOrEmpty()) {
            layoutGuide.visibility = View.GONE
        } else {
            var start = -1
            var end = -1
            var textResult = ""
            obj.guide!!.indexOf("<style").let {
                start = it
            }
            obj.guide!!.indexOf("</style>").let {
                end = it + 8
            }

            textResult = if (start != -1 && end != -1) {
                obj.guide!!.replace(obj.guide!!.substring(start, end), "")
            } else {
                obj.guide!!
            }


            val imageGetter = GlideImageGetter(tvGuide)
            val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(textResult, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
            } else {
                Html.fromHtml(textResult, imageGetter, null)
            }
            tvGuide.text = html

        }

        if (!obj.conditions.isNullOrEmpty()) {
            adapterCondition?.setListData(obj.conditions)
        } else {
            layoutConditions.visibility = View.GONE
        }

//        if (!obj.user_ranks.isNullOrEmpty()) {
//            adapterRank?.setListData(obj.user_ranks)
//        } else {
//            layoutRank.visibility = View.GONE
//        }

        if (!obj.sponsors.isNullOrEmpty()) {
            adapterSponsors?.setListData(obj.sponsors)
        } else {
            layoutTaiTro.visibility = View.GONE
        }

        when (obj.state) {
            0 -> {
                btnJoinCampaign.visibility = View.VISIBLE
            }
            1 -> {
                btnJoinCampaign.visibility = View.VISIBLE
            }
            2 -> {
                btnJoinCampaign.visibility = View.GONE
            }
            3 -> {
                btnJoinCampaign.visibility = View.GONE
            }
        }
    }

    override fun onCickSponsor(item: ICDetail_Campaign.ListSponsors) {
    }

    override fun onGetDataError(message: String) {
        showShortError(message)
    }

    override fun onJoinCampaignSuccess(message: String) {
        showShortSuccess(message)
        btnJoinCampaign.visibility = View.GONE
    }

    override fun showLoading() {

    }

    override fun closeLoading() {

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnJoinCampaign -> {
                presenter.joinCampaign(mId)
            }
        }
    }
}