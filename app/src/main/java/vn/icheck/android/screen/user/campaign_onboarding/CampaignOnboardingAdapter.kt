package vn.icheck.android.screen.user.campaign_onboarding

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.util.kotlin.WidgetUtils

class CampaignOnboardingAdapter : RecyclerViewAdapter<String>() {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            if (!listData[position].isNullOrEmpty()){
                holder.bind(listData[position])
            }else{
                holder.bind("")
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<String>(createView(parent.context)) {
        override fun bind(obj: String) {
            (itemView as AppCompatImageView).run {
                WidgetUtils.loadImageUrl(this, obj)
            }
        }
    }

    fun createView(context: Context): AppCompatImageView {
        return AppCompatImageView(context).also {
            it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            it.adjustViewBounds = true
            it.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }
}