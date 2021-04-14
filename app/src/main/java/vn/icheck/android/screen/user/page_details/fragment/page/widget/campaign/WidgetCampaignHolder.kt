package vn.icheck.android.screen.user.page_details.fragment.page.widget.campaign

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.screen.user.list_campaign.ListCampaignActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class WidgetCampaignHolder(parent: ViewGroup,recycledViewPool: RecyclerView.RecycledViewPool?) : BaseViewHolder<MutableList<ICCampaign>>(ViewHelper.createRecyclerViewWithTitleHolderV2(parent.context, null)) {

    override fun bind(obj: MutableList<ICCampaign>) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as ViewGroup).run {
                (getChildAt(0) as AppCompatTextView).run {
                    setText(R.string.chuong_trinh)
                }

                (getChildAt(1) as AppCompatTextView).run {
                    setOnClickListener {
                        ICheckApplication.currentActivity()?.let { act ->
                            ActivityUtils.startActivity<ListCampaignActivity>(act)
                        }
                    }
                }
            }

            (getChildAt(1) as RecyclerView).run {
                layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = WidgetCampaignAdapter(obj)
            }
        }
    }
}