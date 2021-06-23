package vn.icheck.android.component.ads.campaign

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.item_ads_campaign_slide_holder.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICAdsData
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.screen.user.campaign_onboarding.CampaignOnboardingActivity
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.util.kotlin.WidgetUtils

class AdsCampaignAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val campaignChangeSubscribe = "campaign_change_subscribe" // Campaign - Chuyển đổi tham gia
    private val campaignApproach = "campaign_approach" // Campaign - Tiếp cận

    private val listData = mutableListOf<ICAdsData>()
    private var adsType = ""
    private var showType = Constant.ADS_SLIDE_TYPE
    private var targetType: String? = null
    private var targetID: String? = null
    private var itemCount: Int? = null

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    fun setData(list: List<ICAdsData>, adsType: String, showType: Int, targetType: String?, targetID: String?, itemCount: Int? = null) {
        listData.clear()
        listData.addAll(list)

        this.adsType = adsType
        this.showType = showType
        this.targetType = targetType
        this.targetID = targetID
        this.itemCount = itemCount
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (showType) {
            Constant.ADS_GRID_TYPE -> {
                ItemCampaignHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ads_campaign_gird_holder, parent, false))
            }
            Constant.ADS_HORIZONTAL_TYPE -> {
                ItemCampaignHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ads_campaign_horizontal_holder, parent, false))
            }
            else -> {
                ItemCampaignSlideHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ads_campaign_slide_holder, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return if (itemCount != null) {
            if (listData.size > itemCount!!) {
                itemCount!!
            } else {
                listData.size
            }
        } else {
            listData.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemCampaignHolder -> {
                holder.bind(listData[position])
            }
            is ItemCampaignSlideHolder -> {
                holder.bind(listData[position])
            }
        }
    }

    inner class ItemCampaignHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(obj: ICAdsData) {
            itemView.findViewById<AppCompatImageView>(R.id.imgCampaign)?.apply {
                if (!obj.media.isNullOrEmpty()) {
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedCenterCrop(this, obj.media!![0].content, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedCenterCrop(this, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                } else {
                    WidgetUtils.loadImageUrlRoundedCenterCrop(this, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                }
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvName).text = obj.name
            itemView.findViewById<AppCompatTextView>(R.id.tvTimeStart).text = ("${TimeHelper.getDayOfWeek(obj.startTime)}, ${TimeHelper.getDayAndMonth(obj.startTime)} - ${TimeHelper.getDayAndMonth(obj.endTime)}")
            itemView.findViewById<AppCompatTextView>(R.id.tvTimeLeft).text = TimeHelper.convertDateTimeSvToCurrentTimeLeftCampaign(obj.endTime)

            itemView.findViewById<AppCompatTextView>(R.id.tvJoin)?.apply {
                background = ViewHelper.bgPrimaryCorners4(itemView.context)
            }

            if (targetType == campaignApproach) {
                itemView.findViewById<AppCompatTextView>(R.id.tvJoin)?.visibility = View.VISIBLE
                itemView.findViewById<AppCompatTextView>(R.id.tvJoin1)?.visibility = View.VISIBLE
            } else {
                itemView.findViewById<AppCompatTextView>(R.id.tvJoin)?.visibility = View.GONE
                itemView.findViewById<AppCompatTextView>(R.id.tvJoin1)?.visibility = View.GONE
            }

            itemView.setOnClickListener {
                if (!obj.id.isNullOrEmpty()) {
                    getInfoCampaign(obj)
                }
            }
        }
    }

    inner class ItemCampaignSlideHolder(view: View) : BaseVideoViewHolder(view) {
        var exoPlayer: SimpleExoPlayer? = null

        fun bind(obj: ICAdsData) {
            itemView.tvJoinSlide.background = ViewHelper.bgPrimaryCorners4(itemView.context)

            itemView.imgImage.visibility = View.VISIBLE
            itemView.mediaView.visibility = View.INVISIBLE
            if (!obj.media.isNullOrEmpty()) {
                if (obj.media!![0].type == Constant.VIDEO) {
                    itemView.imgPlay.visibility = View.VISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedCenterCrop(itemView.imgImage, obj.media!![0].content, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedCenterCrop(itemView.imgImage, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                } else {
                    itemView.imgPlay.visibility = View.INVISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedCenterCrop(itemView.imgImage, obj.media!![0].content, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedCenterCrop(itemView.imgImage, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                }
            } else {
                WidgetUtils.loadImageUrlRoundedCenterCrop(itemView.imgImage, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }

            itemView.tvNameSlide.text = obj.name
            itemView.tvTimeStartSlide.apply {
                text = context.getString(R.string.format_s_s_s, TimeHelper.getDayOfWeek(obj.startTime), TimeHelper.getDayAndMonth(obj.startTime), TimeHelper.getDayAndMonth(obj.endTime))
            }
            itemView.tvTimeLeftSlide.text = TimeHelper.convertDateTimeSvToCurrentTimeLeftCampaign(obj.endTime)

            if (targetType == campaignApproach) {
                itemView.containerJoin.visibility = View.VISIBLE
            } else {
                itemView.containerJoin.visibility = View.GONE
            }

            itemView.mediaView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                itemView.imgPlay.visibility = if (view.visibility == View.VISIBLE) {
                    View.INVISIBLE
                } else {
                    if (!obj.media.isNullOrEmpty() && obj.media!![0].type == Constant.VIDEO) {
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }
                }
            }

            itemView.setOnClickListener {
                if (!obj.id.isNullOrEmpty()) {
                    getInfoCampaign(obj)
                }
            }
        }

        override fun onPlayVideo(): Boolean {
            return playVideo(itemView.mediaView, listData[adapterPosition].media)
        }
    }

    fun getInfoCampaign(objAds: ICAdsData) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            return
        }
        ICheckApplication.currentActivity()?.let { activity ->
            DialogHelper.showLoading(activity)
            ListCampaignInteractor().getInfoCampaign(objAds.id, object : ICNewApiListener<ICResponse<ICCampaign>> {
                override fun onSuccess(obj: ICResponse<ICCampaign>) {
                    DialogHelper.closeLoading(activity)
                    val campaign = obj.data
                    if (campaign != null) {
                        val intent = if (campaign.state.toString().toDouble().toInt() == 0 || campaign.state.toString().toDouble().toInt() == 3) {
                            Intent(activity, CampaignOnboardingActivity::class.java)
                        } else {
                            Intent(activity, if (!campaign.hasOnboarding) CampaignOnboardingActivity::class.java else ListShakeGridBoxActivity::class.java)
                        }
                        intent.putExtra(Constant.DATA_1, campaign)
                        activity.startActivity(intent)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    val message = if (error?.message.isNullOrEmpty()) {
                        ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    } else {
                        error!!.message
                    }
                    ICheckApplication.getInstance().showShortErrorToast(message)
                }
            })
        }

    }
}