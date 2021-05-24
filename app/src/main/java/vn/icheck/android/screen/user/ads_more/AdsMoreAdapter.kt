package vn.icheck.android.screen.user.ads_more

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_ads_product_grid.view.*
import kotlinx.android.synthetic.main.item_ads_product_grid.view.imgImage
import kotlinx.android.synthetic.main.item_ads_product_grid.view.layoutRating
import kotlinx.android.synthetic.main.item_ads_product_grid.view.tvName
import kotlinx.android.synthetic.main.item_ads_product_grid.view.tvPriceUpdating
import kotlinx.android.synthetic.main.item_ads_product_grid.view.tvRatingText
import kotlinx.android.synthetic.main.item_ads_product_grid.view.tvRatingUpdating
import kotlinx.android.synthetic.main.item_ads_product_grid.view.tvTenSpUpdating
import kotlinx.android.synthetic.main.item_ads_product_vertical.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TextHelper.setDrawbleNextEndText
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.network.models.ICAdsData
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewPointText

class AdsMoreAdapter : RecyclerViewCustomAdapter<ICAdsData>() {
    private var targetType: String? = null
    private var targetID: String? = null
    private var adsType = ""

    fun setData(obj: MutableList<ICAdsData>, adsType: String, targetType: String?, targetID: String?) {
        listData.clear()

        this.adsType = adsType
        this.targetType = targetType
        this.targetID = targetID

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        adsType = ""
        targetType = null
        targetID = null
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return ICKViewType.ITEM_TYPE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (adsType == Constant.GRID) {
            ViewHolder(parent)
        } else {
            AdsVertical(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ViewHolder -> {
                holder.itemView.setBackgroundResource(R.drawable.bg_transparent_outline_gray_0_5)
                holder.bind(listData[position])
            }
            is AdsVertical -> {
                holder.bind(listData[position])
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    private fun setButtonText(tvButton: AppCompatTextView, isFollow: Boolean, drawable: Int, isGone: Boolean = false) {
        when (adsType) {
            Constant.PRODUCT_CHANGE_BUY -> { // Mua sản phẩm
                tvButton.visibility = View.VISIBLE
                tvButton.setText(R.string.mua_ngay)
            }
            Constant.PRODUCT_APPROACH -> { // Tiếp cận sản phẩm
                if (isGone) {
                    tvButton.visibility = View.GONE
                } else {
                    tvButton.visibility = View.INVISIBLE
                }
            }
            else -> { // Liên hệ
                tvButton.setText(R.string.lien_he)
                tvButton.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
            }
        }
    }

    private fun actionButton(viewHolder: RecyclerView.ViewHolder, obj: ICAdsData) {
        when (adsType) {
            Constant.PRODUCT_CHANGE_BUY -> {

            }
            Constant.PRODUCT_APPROACH -> {

            }
            else -> { // Liên hệ

            }
        }
    }

    inner class AdsVertical(parent: ViewGroup) : BaseVideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ads_product_vertical, parent, false)) {

        @SuppressLint("SetTextI18n")
        fun bind(obj: ICAdsData) {
            itemView.imgImage.visibility = View.VISIBLE
            itemView.surfaceView.visibility = View.INVISIBLE
            itemView.progressBar.visibility = View.INVISIBLE

            if (!obj.media.isNullOrEmpty()) {
                if (obj.media!![0].type == Constant.VIDEO) {
                    itemView.imgPlay.visibility = View.VISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, obj.media!![0].content, R.drawable.img_error_ads_product, R.drawable.img_error_ads_product, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, null, R.drawable.img_error_ads_product, R.drawable.img_error_ads_product, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                } else {
                    itemView.imgPlay.visibility = View.INVISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, obj.media!![0].content, R.drawable.img_error_ads_product, R.drawable.img_error_ads_product, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, null, R.drawable.img_error_ads_product, R.drawable.img_error_ads_product, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, null, R.drawable.img_error_ads_product, R.drawable.img_error_ads_product, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }

            if (!obj.owner?.avatar?.content.isNullOrEmpty()) {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.owner?.avatar?.content, R.drawable.ic_business_v2)
            } else {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.owner?.avatar?.content, R.drawable.ic_business_v2)
            }


            if (obj.owner?.verified == true) {
                itemView.tvName.setDrawbleNextEndText(obj.owner?.name ?: "",R.drawable.ic_verified_16px)
                Handler().postDelayed({
                    itemView.tvName.setDrawbleNextEndText(obj.owner?.name ?: "",R.drawable.ic_verified_16px)
                },100)
            } else {
                itemView.tvName.text = obj.owner?.name ?: ""
            }


            if (!obj.name.isNullOrEmpty()) {
                itemView.tvContent.text = obj.name
                itemView.tvContent.beVisible()
                itemView.tvTenSpUpdating.beInvisible()
            } else {
                itemView.tvContent.beInvisible()
                itemView.tvTenSpUpdating.beVisible()
            }

            if (obj.price == null && obj.sellPrice == null) {
                itemView.tvPriceSpecial.beGone()
                itemView.tvPriceOriginal.beGone()
                itemView.tvPriceUpdating.beVisible()
            } else {
                itemView.tvPriceUpdating.beGone()
                if (obj.price != null) {
                    itemView.tvPriceSpecial.beVisible()
                    itemView.tvPriceSpecial.text = TextHelper.formatMoney((obj.price?:0.0).toLong()) + "đ"
                }

                if (obj.sellPrice != null) {
                    itemView.tvPriceOriginal.beVisible()
                    itemView.tvPriceOriginal.text = TextHelper.formatMoney(obj.sellPrice) + "đ"
                    itemView.tvPriceOriginal.paintFlags = itemView.tvPriceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    itemView.tvPriceOriginal.beGone()
                }
            }

            if (adsType == Constant.PRODUCT_APPROACH) {
                itemView.layoutRating.visibility = View.GONE
                itemView.tvPriceOriginal.visibility = View.VISIBLE
                setButtonText(itemView.btnAction, obj.isFollow, 0, true)
            } else {
                itemView.tvPriceOriginal.visibility = View.GONE
                itemView.layoutRating.visibility = View.VISIBLE
                setButtonText(itemView.btnAction, obj.isFollow, 0)

                if (obj.rating != null) {
                    itemView.tvPoint.beVisible()
                    itemView.tvRatingText.beVisible()
                    itemView.tvPoint.text = obj.rating.toString()
                    ReviewPointText.setText(itemView.tvRatingText, obj.rating!!)
                } else {
                    itemView.tvRatingUpdating.beVisible()
                    itemView.tvPoint.beGone()
                    itemView.tvRatingText.beGone()
                }
            }

            itemView.surfaceView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                if (view.visibility == View.VISIBLE) {
                    itemView.imgPlay.visibility = View.INVISIBLE
                    itemView.progressBar.visibility = View.INVISIBLE
                } else {
                    if (!obj.media.isNullOrEmpty() && obj.media!![0].type == Constant.VIDEO) {
                        itemView.imgPlay.visibility = View.VISIBLE
                    } else {
                        itemView.imgPlay.visibility = View.INVISIBLE
                    }
                    itemView.progressBar.visibility = View.INVISIBLE
                }
            }

            itemView.btnAction.setOnClickListener {
                actionButton(this, obj)
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
            return if (playVideo(itemView.surfaceView, listData[adapterPosition].media)) {
                if (ExoPlayerManager.player?.isPlaying == true) {
                    itemView.imgPlay.visibility = View.INVISIBLE
                    itemView.progressBar.visibility = View.INVISIBLE
                } else {
                    itemView.imgPlay.visibility = View.INVISIBLE
                    itemView.progressBar.visibility = View.VISIBLE
                }
                true
            } else {
                false
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICAdsData>(R.layout.item_ads_product_grid, parent) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICAdsData) {
            itemView.imgImage.visibility = View.VISIBLE

            if (!obj.media.isNullOrEmpty()) {
                if (!obj.media!![0].content.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, obj.media!![0].content, R.drawable.img_error_ads_product, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                } else {
                    WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, null, R.drawable.img_error_ads_product, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgImage, null, R.drawable.img_error_ads_product, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }

            if (!obj.name.isNullOrEmpty()) {
                itemView.tvName.beVisible()
                itemView.tvName.text = obj.name
                itemView.tvTenSpUpdating.beGone()
            } else {
                itemView.tvTenSpUpdating.beVisible()
                itemView.tvName.beGone()
            }

            if (obj.price != null) {
                itemView.tvPrice.beVisible()
                itemView.tvPrice.text = TextHelper.formatMoney((obj.price?:0.0).toLong()) + "đ"
                itemView.tvPriceUpdating.beGone()
            } else {
                itemView.tvPriceUpdating.beVisible()
                itemView.tvPrice.beGone()
            }

            if (obj.verified == true) {
                itemView.tvVerified.visibility = View.VISIBLE
            } else {
                itemView.tvVerified.visibility = View.GONE
            }

            if (adsType == Constant.PRODUCT_APPROACH) {
                setButtonText(itemView.tvAction, obj.isFollow, 0, true)
                itemView.layoutRating.visibility = View.GONE
            } else {
                setButtonText(itemView.tvAction, obj.isFollow, 0)
                itemView.layoutRating.visibility = View.VISIBLE

                if (obj.rating != null) {
                    itemView.tvRatingUpdating.beGone()
                    itemView.ratingbar.beVisible()
                    itemView.tvRatingText.beVisible()

                    itemView.ratingbar.rating = (obj.rating)!!.toFloat()
                    itemView.tvRatingText.text = ((obj.rating!! * 2).toFloat()).toString()
                } else {
                    itemView.tvRatingUpdating.beVisible()
                    itemView.ratingbar.beGone()
                    itemView.tvRatingText.beGone()
                }
            }

            itemView.tvAction.setOnClickListener {
//                actionButton(this, obj)
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
    }
}