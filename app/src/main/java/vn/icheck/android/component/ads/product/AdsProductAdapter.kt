package vn.icheck.android.component.ads.product

import android.graphics.Paint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_ads_product_grid.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemAdsProductGridBinding
import vn.icheck.android.databinding.ItemAdsProductHorizontalBinding
import vn.icheck.android.databinding.ItemAdsProductHorizontalMoreBinding
import vn.icheck.android.databinding.ItemAdsProductSlideBinding
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TextHelper.setDrawbleNextEndText
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICAdsData
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewPointText

class AdsProductAdapter(var fullScreen: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICAdsData>()
    private var adsType = ""
    private var showType = Constant.ADS_SLIDE_TYPE
    private var targetType: String? = null
    private var targetID: String? = null
    private var itemCount: Int? = null

    fun clearData() {
        listData.clear()
        adsType = ""
        showType = -1
        targetType = null
        targetID = null
        notifyDataSetChanged()
    }

    fun setData(
        list: List<ICAdsData>,
        adsType: String,
        showType: Int,
        targetType: String?,
        targetID: String?,
        itemCount: Int? = null
    ) {
        listData.clear()
        listData.addAll(list)

        this.adsType = adsType
        this.showType = showType
        this.targetType = targetType
        this.targetID = targetID
        this.itemCount = itemCount
        notifyDataSetChanged()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (showType) {
            Constant.ADS_SLIDE_TYPE -> {
                ViewHolderSlide(
                    ItemAdsProductSlideBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            Constant.ADS_HORIZONTAL_TYPE -> {
                if(fullScreen){
                    ViewHolderHorizontalMore(ItemAdsProductHorizontalMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                }else{
                    ViewHolderHorizontal(ItemAdsProductHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                }
            }
            else -> {
                ViewHolderGrid(
                    ItemAdsProductGridBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderSlide -> {
                holder.bind(listData[position])
            }
            is ViewHolderGrid -> {
                holder.bind(listData[position])
            }
            is ViewHolderHorizontal -> {
                holder.bind(listData[position])
            }
            is ViewHolderHorizontalMore ->{
                holder.bind(listData[position])
            }
        }
    }

    inner class ViewHolderSlide(val binding: ItemAdsProductSlideBinding) :
        BaseVideoViewHolder(binding.root) {
        fun bind(obj: ICAdsData) {
            binding.imgImage.visibility = View.VISIBLE
            binding.surfaceView.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE

            if (!obj.media.isNullOrEmpty()) {
                if (obj.media!![0].type == Constant.VIDEO) {
                    binding.imgPlay.visibility = View.VISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        binding.imgImage.scaleType = ImageView.ScaleType.FIT_CENTER
                        WidgetUtils.loadImageUrlRoundedTransformation(
                            binding.imgImage,
                            obj.media!![0].content,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    } else {
                        binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
                        binding.imgImage.setImageResource(R.drawable.img_default_product_big)
                    }
                } else {
                    binding.imgPlay.visibility = View.INVISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        binding.imgImage.scaleType = ImageView.ScaleType.FIT_CENTER
                        WidgetUtils.loadImageUrlRoundedTransformation(
                            binding.imgImage,
                            obj.media!![0].content,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    } else {
                        binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
                        binding.imgImage.setImageResource(R.drawable.img_default_product_big)
                    }
                }
            } else {
                binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.imgImage.setImageResource(R.drawable.img_default_product_big)
            }

            if (!obj.name.isNullOrEmpty()) {
                binding.tvName.beVisible()
                binding.tvName.text = obj.name
                binding.tvTenSpUpdating.beGone()
            } else {
                binding.tvTenSpUpdating.beVisible()
                binding.tvName.beInvisible()
            }

            binding.surfaceView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                if (view.visibility == View.VISIBLE) {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                } else {
                    if (!obj.media.isNullOrEmpty() && obj.media!![0].type == Constant.VIDEO) {
                        binding.imgPlay.visibility = View.VISIBLE
                    } else {
                        binding.imgPlay.visibility = View.INVISIBLE
                    }
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }

            if (obj.price != null || obj.sellPrice != null) {
                binding.tvPriceUpdating.beGone()
                if (obj.price != null) {
                    binding.tvPriceSpecial.apply {
                        beVisible()
                        text = context.getString(R.string.format_s_d, TextHelper.formatMoney((obj.price?:0.0).toLong()))
                    }
                }

                if (obj.sellPrice != null) {
                    binding.tvPriceOriginal.apply {
                        beVisible()
                        text = context.getString(R.string.format_s_d, TextHelper.formatMoney(obj.sellPrice))
                        paintFlags = binding.tvPriceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                }
            } else {
                binding.tvPriceUpdating.beVisible()
                binding.tvPriceSpecial.beGone()
                binding.tvPriceOriginal.beGone()
            }

            if (adsType == Constant.PRODUCT_APPROACH) {
                setButtonText(binding.btnAction, obj.isFollow, 0, true)
            } else {
                setButtonText(binding.btnAction, obj.isFollow, 0)
            }
            if (obj.rating != null) {
                binding.tvPoint.beVisible()
                binding.tvRatingUpdating.beGone()
                binding.tvRatingText.beVisible()
                binding.tvPoint.text = Math.round((obj.rating!! * 2) * 10).div(10.0).toString()
                ReviewPointText.setText(binding.tvRatingText, obj.rating!!)
            } else {
                binding.tvRatingUpdating.beVisible()
                binding.tvPoint.beGone()
                binding.tvRatingText.beGone()
            }

            binding.btnAction.setOnClickListener {
                actionButton(this, obj)
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    if (!obj.targetType.isNullOrEmpty()) {
                        FirebaseDynamicLinksActivity.startTarget(
                            activity,
                            obj.targetType,
                            obj.targetId
                        )
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }

        override fun onPlayVideo(): Boolean {
            return if (playVideo(binding.surfaceView, listData[adapterPosition].media)) {
                if (ExoPlayerManager.player?.isPlaying == true) {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                } else {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                }
                true
            } else {
                false
            }
        }
    }



    inner class ViewHolderHorizontal(val binding: ItemAdsProductHorizontalBinding) : BaseVideoViewHolder(binding.root) {
        fun bind(obj: ICAdsData) {
            binding.imgImage.visibility = View.VISIBLE
            binding.surfaceView.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE

            if (!SettingManager.themeSetting?.theme?.productOverlayImage.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlFitCenter(
                    binding.productOverlayImage,
                    SettingManager.themeSetting?.theme?.productOverlayImage,
                    android.R.color.transparent,
                    android.R.color.transparent
                )
            }

            if (!obj.media.isNullOrEmpty()) {
                if (obj.media!![0].type == Constant.VIDEO) {
                    binding.imgPlay.visibility = View.VISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformationCenterCrop(
                            binding.imgImage,
                            obj.media!![0].content,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    } else {
                        binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
                        WidgetUtils.loadImageUrlRoundedTransformationCenterCrop(
                            binding.imgImage,
                            null,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    }
                } else {
                    binding.imgPlay.visibility = View.INVISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformationCenterCrop(
                            binding.imgImage,
                            obj.media!![0].content,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformationCenterCrop(
                            binding.imgImage,
                            null,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    }
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformationCenterCrop(
                    binding.imgImage,
                    null,
                    R.drawable.img_default_product_big,
                    R.drawable.img_default_product_big,
                    SizeHelper.size4,
                    RoundedCornersTransformation.CornerType.TOP
                )
            }

            if (!obj.owner?.avatar?.content.isNullOrEmpty()) {
                WidgetUtils.loadImageUrl(
                    binding.imgAvatar,
                    obj.owner?.avatar?.content,
                    R.drawable.ic_business_v2
                )
            } else {
                WidgetUtils.loadImageUrl(binding.imgAvatar, null, R.drawable.ic_business_v2)
            }



            if (obj.owner?.verified == true) {
                itemView.tvName.setDrawbleNextEndText(obj.owner?.name, R.drawable.ic_verified_16px)
                Handler().postDelayed({
                    itemView.tvName.setDrawbleNextEndText(
                        obj.owner?.name,
                        R.drawable.ic_verified_16px
                    )
                }, 100)
            } else {
                binding.tvName.text = if (obj.owner?.name.isNullOrEmpty()) {
                    itemView.context.getString(R.string.dang_cap_nhat)
                } else {
                    obj.owner?.name
                }
            }

            if (!obj.name.isNullOrEmpty()) {
                binding.tvContent.text = obj.name
                binding.tvContent.beVisible()
                binding.tvTenSpUpdating.beInvisible()
            } else {
                binding.tvContent.beInvisible()
                binding.tvTenSpUpdating.beVisible()
            }

            if (obj.price == null && obj.sellPrice == null) {
                binding.tvPriceSpecial.beGone()
                binding.tvPriceOriginal.beGone()
                binding.tvPriceUpdating.beVisible()
            } else {
                binding.tvPriceUpdating.beGone()
                if (obj.price != null) {
                    binding.tvPriceSpecial.apply {
                        beVisible()
                        text = context.getString(R.string.format_s_d, TextHelper.formatMoney((obj.price?:0.0).toLong()))
                    }
                }

                if (obj.sellPrice != null) {
                    binding.tvPriceOriginal.apply {
                        beVisible()
                        text = context.getString(R.string.format_s_d, TextHelper.formatMoney(obj.sellPrice))
                        paintFlags = binding.tvPriceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                }
            }

            if (adsType == Constant.PRODUCT_APPROACH) {
                setButtonText(binding.btnAction, obj.isFollow, 0, true)
            } else {
                setButtonText(binding.btnAction, obj.isFollow, 0)
            }
            if (obj.rating != null) {
                binding.tvRatingUpdating.beGone()
                binding.tvPoint.beVisible()
                binding.tvRatingText.beVisible()
                binding.tvPoint.text = Math.round((obj.rating!! * 2) * 10).div(10.0).toString()
                ReviewPointText.setText(binding.tvRatingText, obj.rating!!)
            } else {
                binding.tvRatingUpdating.beVisible()
                binding.tvPoint.beGone()
                binding.tvRatingText.beGone()
            }

            binding.surfaceView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                if (view.visibility == View.VISIBLE) {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                } else {
                    if (!obj.media.isNullOrEmpty() && obj.media!![0].type == Constant.VIDEO) {
                        binding.imgPlay.visibility = View.VISIBLE
                    } else {
                        binding.imgPlay.visibility = View.INVISIBLE
                    }
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }

            binding.btnAction.setOnClickListener {
                actionButton(this, obj)
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    if (!obj.targetType.isNullOrEmpty()) {
                        FirebaseDynamicLinksActivity.startTarget(
                            activity,
                            obj.targetType,
                            obj.targetId
                        )
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }

        override fun onPlayVideo(): Boolean {
            return if (playVideo(binding.surfaceView, listData[adapterPosition].media)) {
                if (ExoPlayerManager.player?.isPlaying == true) {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                } else {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                }
                true
            } else {
                false
            }
        }
    }


    inner class ViewHolderHorizontalMore(val binding: ItemAdsProductHorizontalMoreBinding) : BaseVideoViewHolder(binding.root) {
        fun bind(obj: ICAdsData) {
            binding.imgImage.visibility = View.VISIBLE
            binding.surfaceView.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE

            if (!SettingManager.themeSetting?.theme?.productOverlayImage.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlFitCenter(
                    binding.productOverlayImage,
                    SettingManager.themeSetting?.theme?.productOverlayImage,
                    android.R.color.transparent,
                    android.R.color.transparent
                )
            }

            if (!obj.media.isNullOrEmpty()) {
                if (obj.media!![0].type == Constant.VIDEO) {
                    binding.imgPlay.visibility = View.VISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformation(
                            binding.imgImage,
                            obj.media!![0].content,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    } else {
                        binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
                        WidgetUtils.loadImageUrlRoundedTransformation(
                            binding.imgImage,
                            null,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    }
                } else {
                    binding.imgPlay.visibility = View.INVISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformation(
                            binding.imgImage,
                            obj.media!![0].content,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    } else {
                        binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
                        WidgetUtils.loadImageUrlRoundedTransformation(
                            binding.imgImage,
                            null,
                            R.drawable.img_default_product_big,
                            R.drawable.img_default_product_big,
                            SizeHelper.size4,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    }
                }
            } else {
                binding.imgImage.scaleType = ImageView.ScaleType.CENTER_CROP
                WidgetUtils.loadImageUrlRoundedTransformation(
                    binding.imgImage,
                    null,
                    R.drawable.img_default_product_big,
                    R.drawable.img_default_product_big,
                    SizeHelper.size4,
                    RoundedCornersTransformation.CornerType.TOP
                )
            }

            if (!obj.owner?.avatar?.content.isNullOrEmpty()) {
                WidgetUtils.loadImageUrl(
                    binding.imgAvatar,
                    obj.owner?.avatar?.content,
                    R.drawable.ic_business_v2
                )
            } else {
                WidgetUtils.loadImageUrl(binding.imgAvatar, null, R.drawable.ic_business_v2)
            }



            if (obj.owner?.verified == true) {
                itemView.tvName.setDrawbleNextEndText(obj.owner?.name, R.drawable.ic_verified_16px)
                Handler().postDelayed({
                    itemView.tvName.setDrawbleNextEndText(
                        obj.owner?.name,
                        R.drawable.ic_verified_16px
                    )
                }, 100)
            } else {
                binding.tvName.text = if (obj.owner?.name.isNullOrEmpty()) {
                    itemView.context.getString(R.string.dang_cap_nhat)
                } else {
                    obj.owner?.name
                }
            }

            if (!obj.name.isNullOrEmpty()) {
                binding.tvContent.text = obj.name
                binding.tvContent.beVisible()
                binding.tvTenSpUpdating.beInvisible()
            } else {
                binding.tvContent.beInvisible()
                binding.tvTenSpUpdating.beVisible()
            }

            if (obj.price == null && obj.sellPrice == null) {
                binding.tvPriceSpecial.beGone()
                binding.tvPriceOriginal.beGone()
                binding.tvPriceUpdating.beVisible()
            } else {
                binding.tvPriceUpdating.beGone()
                if (obj.price != null) {
                    binding.tvPriceSpecial.apply {
                        beVisible()
                        text = context.getString(R.string.format_s_d, TextHelper.formatMoney((obj.price?:0.0).toLong()))
                    }
                }

                if (obj.sellPrice != null) {
                    binding.tvPriceOriginal.apply {
                        beVisible()
                        text = context.getString(R.string.format_s_d, TextHelper.formatMoney(obj.sellPrice))
                        paintFlags = binding.tvPriceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                }
            }

            if (adsType == Constant.PRODUCT_APPROACH) {
                setButtonText(binding.btnAction, obj.isFollow, 0, true)
            } else {
                setButtonText(binding.btnAction, obj.isFollow, 0)
            }
            if (obj.rating != null) {
                binding.tvRatingUpdating.beGone()
                binding.tvPoint.beVisible()
                binding.tvRatingText.beVisible()
                binding.tvPoint.text = Math.round((obj.rating!! * 2) * 10).div(10.0).toString()
                ReviewPointText.setText(binding.tvRatingText, obj.rating!!)
            } else {
                binding.tvRatingUpdating.beVisible()
                binding.tvPoint.beGone()
                binding.tvRatingText.beGone()
            }

            binding.surfaceView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                if (view.visibility == View.VISIBLE) {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                } else {
                    if (!obj.media.isNullOrEmpty() && obj.media!![0].type == Constant.VIDEO) {
                        binding.imgPlay.visibility = View.VISIBLE
                    } else {
                        binding.imgPlay.visibility = View.INVISIBLE
                    }
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }

            binding.btnAction.setOnClickListener {
                actionButton(this, obj)
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    if (!obj.targetType.isNullOrEmpty()) {
                        FirebaseDynamicLinksActivity.startTarget(
                            activity,
                            obj.targetType,
                            obj.targetId
                        )
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }

        override fun onPlayVideo(): Boolean {
            return if (playVideo(binding.surfaceView, listData[adapterPosition].media)) {
                if (ExoPlayerManager.player?.isPlaying == true) {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                } else {
                    binding.imgPlay.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                }
                true
            } else {
                false
            }
        }
    }



    inner class ViewHolderGrid(val binding: ItemAdsProductGridBinding) :
        BaseViewHolder<ICAdsData>(binding.root) {
        override fun bind(obj: ICAdsData) {
            binding.imgImage.visibility = View.VISIBLE

            if (!SettingManager.themeSetting?.theme?.productOverlayImage.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlFitCenter(
                    binding.productOverlayImage,
                    SettingManager.themeSetting?.theme?.productOverlayImage,
                    android.R.color.transparent,
                    android.R.color.transparent
                )
            }

            if (!obj.media.isNullOrEmpty()) {
                if (!obj.media!![0].content.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlRoundedTransformation(
                        binding.imgImage,
                        obj.media!![0].content,
                        R.drawable.img_default_product_big,
                        R.drawable.img_default_product_big,
                        SizeHelper.size4,
                        RoundedCornersTransformation.CornerType.TOP
                    )
                } else {
                    WidgetUtils.loadImageUrlRoundedTransformation(
                        binding.imgImage,
                        null,
                        R.drawable.img_default_product_big,
                        R.drawable.img_default_product_big,
                        SizeHelper.size4,
                        RoundedCornersTransformation.CornerType.TOP
                    )
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformation(
                    binding.imgImage,
                    null,
                    R.drawable.img_default_product_big,
                    R.drawable.img_default_product_big,
                    SizeHelper.size4,
                    RoundedCornersTransformation.CornerType.TOP
                )
            }

            if (!obj.name.isNullOrEmpty()) {
                binding.tvName.beVisible()
                binding.tvName.text = obj.name
                binding.tvTenSpUpdating.beGone()
            } else {
                binding.tvTenSpUpdating.beVisible()
                binding.tvName.beGone()
            }

            if (obj.price != null) {
                binding.tvPrice.apply {
                    beVisible()
                    text = context.getString(R.string.format_s_d, TextHelper.formatMoney((obj.price?:0.0).toLong()))
                }
                binding.tvPriceUpdating.beGone()
            } else {
                binding.tvPriceUpdating.beVisible()
                binding.tvPrice.beGone()
            }

            if (obj.verified == true) {
                binding.tvVerified.visibility = View.VISIBLE
            } else {
                binding.tvVerified.visibility = View.GONE
            }

            if (adsType == Constant.PRODUCT_APPROACH) {
                setButtonText(binding.tvAction, obj.isFollow, 0, true)
            } else {
                setButtonText(binding.tvAction, obj.isFollow, 0)
            }
            if (obj.rating != null) {
                binding.tvRatingUpdating.beGone()
                binding.ratingbar.beVisible()
                binding.tvRatingText.beVisible()
                binding.tvRatingCount.beVisible()

                binding.ratingbar.rating = (obj.rating)!!.toFloat()
                binding.tvRatingText.text = ((obj.rating!! * 2).toFloat()).toString()
                binding.tvRatingCount.text = ("(${obj.reviewCount?.toString() ?: "0"})")
            } else {
                binding.tvRatingUpdating.beVisible()
                binding.ratingbar.beGone()
                binding.tvRatingText.beGone()
                binding.tvRatingCount.beGone()
            }

            binding.tvAction.setOnClickListener {
                actionButton(this, obj)
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    if (!obj.targetType.isNullOrEmpty()) {
                        FirebaseDynamicLinksActivity.startTarget(
                            activity,
                            obj.targetType,
                            obj.targetId
                        )
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }
    }

    private fun setButtonText(
        tvButton: AppCompatTextView,
        isFollow: Boolean,
        drawable: Int,
        isGone: Boolean = false
    ) {
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
}