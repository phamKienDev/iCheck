package vn.icheck.android.component.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_enterprise_feed.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.enterprise_in_feed.ICEnterpriseInFeed
import vn.icheck.android.util.kotlin.WidgetUtils

class EnterpriseFeedHolder (view: View) : BaseViewHolder<ICEnterpriseInFeed>(view) {

    override fun bind(obj: ICEnterpriseInFeed) {
        WidgetUtils.loadImageUrl(itemView.imgAvatar,obj.image)
        itemView.tvName.text = obj.name
        if (obj.isVerify == false){
            itemView.tvIconVerified.visibility = View.GONE
        }else{
            itemView.tvIconVerified.visibility = View.VISIBLE
        }
    }

    companion object {
        fun createHolder(parent: ViewGroup): EnterpriseFeedHolder {
            return EnterpriseFeedHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_enterprise_feed, parent, false))
        }
    }
}