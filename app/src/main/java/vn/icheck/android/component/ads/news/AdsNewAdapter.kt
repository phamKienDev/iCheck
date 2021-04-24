package vn.icheck.android.component.ads.news

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_grid_ads_news.view.*
import kotlinx.android.synthetic.main.item_horizontal_ads_news.view.*
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.item_slide_ads_news.view.*
import kotlinx.android.synthetic.main.item_slide_ads_news.view.imgImage
import kotlinx.android.synthetic.main.item_slide_ads_news.view.tvContent
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICAdsData
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.kotlin.WidgetUtils

class AdsNewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        val inflater = LayoutInflater.from(parent.context)
        return when (showType) {
            Constant.ADS_SLIDE_TYPE -> {
                SlideAdsNewHolder(inflater.inflate(R.layout.item_slide_ads_news, parent, false))
            }
            Constant.ADS_HORIZONTAL_TYPE -> {
                HorizontalAdsNewHolder(inflater.inflate(R.layout.item_horizontal_ads_news, parent, false))
            }
            else -> {
                GridAdsNewHolder(inflater.inflate(R.layout.item_grid_ads_news, parent, false))
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
            is SlideAdsNewHolder -> {
                holder.bind(listData[position])
            }

            is HorizontalAdsNewHolder -> {
                val item = listData[position]
                holder.bind(item)
            }

            is GridAdsNewHolder -> {
                val item = listData[position]
                holder.bind(item)
            }
        }
    }

    inner class SlideAdsNewHolder constructor(view: View) : BaseVideoViewHolder(view) {
        fun bind(obj: ICAdsData) {
            itemView.imgImage.visibility = View.VISIBLE
            itemView.surfaceView.visibility = View.INVISIBLE

            if (!obj.media.isNullOrEmpty()) {
                if (obj.media!![0].type == Constant.VIDEO) {
                    itemView.imgPlay.visibility = View.VISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()){
                        WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, obj.media!![0].content, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                } else {
                    itemView.imgPlay.visibility = View.INVISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()){
                        WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, obj.media!![0].content, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }                }
            } else {
                itemView.imgPlay.visibility = View.INVISIBLE
                WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }

            if (obj.name?.length!! < 80) {
                itemView.tvContent.text = obj.name
            } else {
                itemView.tvContent.text = Html.fromHtml(obj.name!!.substring(0, 80) + "..." + "<font color=${vn.icheck.android.ichecklibs.Constant.getSecondaryColorCode}>Xem chi tiết</font>")
            }

            itemView.surfaceView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
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
                ICheckApplication.currentActivity()?.let { activity ->
                    if (!obj.targetType.isNullOrEmpty()) {
                        FirebaseDynamicLinksActivity.startTarget(activity, obj.targetType, obj.targetId)
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }

        override fun onPlayVideo(): Boolean {
            return playVideo(itemView.surfaceView, listData[adapterPosition].media)
        }
    }

    inner class HorizontalAdsNewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ICAdsData) {

            if (!item.media.isNullOrEmpty()) {
                if (!item.media!![0].content.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgHorizontalNews, item.media!![0].content, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                } else {
                    WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgHorizontalNews, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgHorizontalNews, null, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }


            itemView.tvTitleHorizontal.text = item.name

            if (item.verified == true) {
                itemView.imgVerifiedHorizontal.visibility = View.VISIBLE
            } else {
                itemView.imgVerifiedHorizontal.visibility = View.GONE
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    if (!item.targetType.isNullOrEmpty()) {
                        FirebaseDynamicLinksActivity.startTarget(activity, item.targetType, item.targetId)
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }
    }

    inner class GridAdsNewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ICAdsData) {
            if (!item.media.isNullOrEmpty()) {
                if (!item.media!![0].content.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgVerticalNews, item.media!![0].content, R.drawable.img_product_shop_default, R.drawable.img_product_shop_default, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                } else {
                    WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgVerticalNews, null, R.drawable.img_product_shop_default, R.drawable.img_product_shop_default, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgVerticalNews, null, R.drawable.img_product_shop_default, R.drawable.img_product_shop_default, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }

            itemView.tvTitleVertical.text = item.name

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    if (!item.targetType.isNullOrEmpty()) {
                        FirebaseDynamicLinksActivity.startTarget(activity, item.targetType, item.targetId)
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }
    }
}