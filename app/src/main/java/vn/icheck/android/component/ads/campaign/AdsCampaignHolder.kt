package vn.icheck.android.component.ads.campaign

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.android.synthetic.main.layout_ads_campaign_holder.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICAdsNew
import vn.icheck.android.screen.user.list_campaign.ListCampaignActivity
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils

class AdsCampaignHolder(parent: ViewGroup) : BaseVideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_ads_campaign_holder, parent, false)) {

    private var adsAdapter = AdsCampaignAdapter()

    fun bind(obj: ICAdsNew) {
        itemView.tvTitle.text = obj.name

        itemView.rcvCampaign.apply {
            onFlingListener = null
            adsAdapter.clearData()
            adapter = null

            if (!obj.data.isNullOrEmpty()) {
                itemView.layoutData.beVisible()
                when (obj.type) {
                    Constant.SLIDE -> {
                        itemView.layoutTitle.visibility = View.GONE
                        itemView.btnArrowLeft.visibility = View.VISIBLE
                        itemView.btnArrowRight.visibility = View.VISIBLE

                        layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                        setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(itemView.context))
                        itemView.layoutParent.setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(itemView.context))
                        setPadding(SizeHelper.size6, SizeHelper.size10, SizeHelper.size6, SizeHelper.size10)
                        PagerSnapHelper().attachToRecyclerView(itemView.rcvCampaign)

                        adapter = adsAdapter
                        adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_SLIDE_TYPE, obj.targetType, obj.targetId)
                    }

                    Constant.HORIZONTAL -> {
                        itemView.layoutTitle.visibility = View.VISIBLE
                        itemView.btnArrowLeft.visibility = View.GONE
                        itemView.btnArrowRight.visibility = View.GONE

                        layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                        setPadding(SizeHelper.size5, 0, SizeHelper.size5, 0)
                        setBackgroundColor(Color.TRANSPARENT)
                        itemView.layoutParent.setBackgroundColor(Color.TRANSPARENT)

                        adapter = adsAdapter
                        adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_HORIZONTAL_TYPE, obj.targetType, obj.targetId, 5)
                    }

                    Constant.GRID -> {
                        itemView.layoutTitle.visibility = View.VISIBLE
                        itemView.btnArrowLeft.visibility = View.GONE
                        itemView.btnArrowRight.visibility = View.GONE

                        layoutManager = CustomGridLayoutManager(itemView.context, 2)
                        setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(itemView.context))
                        itemView.layoutParent.setBackgroundColor(Color.TRANSPARENT)
                        setPadding(SizeHelper.size8, SizeHelper.size4, SizeHelper.size8, SizeHelper.size4)

                        adapter = adsAdapter
                        adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_GRID_TYPE, obj.targetType, obj.targetId, 6)
                    }
                }
                setupRecyclerView(this)
            } else {
                itemView.layoutTitle.beGone()
                itemView.layoutData.beGone()
            }
        }

        itemView.btnMore.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                ActivityUtils.startActivity<ListCampaignActivity>(activity)
            }
        }

        itemView.btnArrowRight.setOnClickListener {
            itemView.rcvCampaign.layoutManager?.let {
                if (it is LinearLayoutManager) {
                    val position = it.findFirstVisibleItemPosition()
                    if (position < adsAdapter.itemCount - 1) {
                        itemView.rcvCampaign.smoothScrollToPosition(position + 1)
                    } else {
                        itemView.rcvCampaign.smoothScrollToPosition(0)

                    }
                }
            }
        }

        itemView.btnArrowLeft.setOnClickListener {
            itemView.rcvCampaign.layoutManager?.let {
                if (it is LinearLayoutManager) {
                    val position = it.findFirstVisibleItemPosition()
                    if (position > 0) {
                        itemView.rcvCampaign.smoothScrollToPosition(position - 1)
                    } else {
                        itemView.rcvCampaign.smoothScrollToPosition(adsAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    override fun onPlayVideo(): Boolean {
        return ExoPlayerManager.checkPlayVideoHorizontal(itemView.rcvCampaign)
    }
}