package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.adapter.view_holder

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_infor_campaign.*
import kotlinx.android.synthetic.main.guide_detail_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.util.kotlin.GlideImageGetter

class GuideDetailCampaignHolder (parent: ViewGroup) : BaseViewHolder<ICDetail_Campaign>(LayoutInflater.from(parent.context).inflate(R.layout.guide_detail_campaign, parent, false)) {

    override fun bind(obj: ICDetail_Campaign) {
//        var start = -1
//        var end = -1
//        var textResult = ""
//        obj.guide!!.indexOf("<style").let {
//            start = it
//        }
//        obj.guide!!.indexOf("</style>").let {
//            end = it + 8
//        }
//
//        textResult = if (start != -1 && end != -1) {
//            obj.guide!!.replace(obj.guide!!.substring(start, end), "")
//        } else {
//            obj.guide!!
//        }


//        val imageGetter = GlideImageGetter(itemView.tvGuide)
//        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Html.fromHtml(textResult, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
//        } else {
//            Html.fromHtml(textResult, imageGetter, null)
//        }
        itemView.tvGuide.text = obj.guide

        itemView.btnMore.setOnClickListener {
            itemView.tvGuide.maxLines = 200
            itemView.btnMore.visibility = View.GONE
        }
    }
}