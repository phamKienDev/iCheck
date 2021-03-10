package vn.icheck.android.component.sponsor_feed

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_base_sponsor_feed.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.util.kotlin.WidgetUtils

class SponsorFeedHolder(view: View) : BaseViewHolder<ICSponsorFeed>(view) {

    companion object {
        fun create(view: ViewGroup): SponsorFeedHolder {
            return SponsorFeedHolder(LayoutInflater.from(view.context).inflate(R.layout.item_base_sponsor_feed, view, false))
        }
    }


    override fun bind(obj: ICSponsorFeed) {
        itemView.tvNameEnterprise.text = if (obj.name != null) {
            obj.name
        } else {
            ""
        }

        itemView.imgVerified.visibility = if (obj.verified) {
            View.VISIBLE
        } else {
            View.GONE
        }

        if (obj.logoEnterprise != null) {
            WidgetUtils.loadImageUrl(itemView.circleImageView, obj.logoEnterprise)
        } else {
            WidgetUtils.loadImageUrl(itemView.circleImageView, "")
        }

        if (obj.description != null) {
            itemView.tvDescription.visibility = View.VISIBLE
            itemView.tvDescription.text = if (obj.description!!.length > 135) {
                Html.fromHtml(itemView.context.getString(R.string.sponsor_feed_introtext, obj.description!!.substring(0, 135)))
            } else {
                obj.description!!
            }
        } else {
            itemView.tvDescription.visibility = View.GONE
        }

        if(obj.product!=null){
            itemView.container_product_info.setData(obj.product!!)
        }

    }

}