package vn.icheck.android.component.ads.page

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemAdsPageGridBinding
import vn.icheck.android.databinding.ItemAdsPageHorizontalBinding
import vn.icheck.android.databinding.ItemAdsPageSlideBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICAdsData
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICMyFollowingPage
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class AdsPageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICAdsData>()
    private var adsType = ""
    private var showType = Constant.ADS_SLIDE_TYPE
    private var targetType: String? = null
    private var targetID: String? = null
    private var itemCount: Int? = null

    private val repository = PageRepository()
    private val appDao = AppDatabase.getDatabase(ICheckApplication.getInstance()).pageFollowsDao()

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
                ViewHolderSlide(ItemAdsPageSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            Constant.ADS_HORIZONTAL_TYPE -> {
                ViewHolderHorizontal(ItemAdsPageHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else -> {
                ViewHolderGrid(ItemAdsPageGridBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
        }
    }

    inner class ViewHolderSlide(val binding: ItemAdsPageSlideBinding) : BaseVideoViewHolder(binding.root) {

        fun bind(obj: ICAdsData) {
            binding.imgImage.visibility = View.VISIBLE
            binding.surfaceView.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE

            if (!obj.media.isNullOrEmpty()) {
                if (obj.media!![0].type == Constant.VIDEO) {
                    binding.imgPlay.visibility = View.VISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, obj.media!![0].content, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, null, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                } else {
                    binding.imgPlay.visibility = View.INVISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, obj.media!![0].content, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, null, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, null, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }

            WidgetUtils.loadImageUrl(binding.imgAvatar, obj.avatar?.content, R.drawable.img_default_business_logo)

            if (obj.verified == true) {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            binding.tvName.text = obj.name

            if (obj.followCount != null && obj.followCount!! > 0) {
                binding.tvStatus.text = binding.tvStatus.context.getString(R.string.xxx_nguoi_theo_doi, TextHelper.formatMoney(obj.followCount))
            } else {
                binding.tvStatus.text = "Chưa có người theo dõi"
            }
            binding.tvContent.text = obj.description

            setButtonText(binding.btnAction, obj.isFollow, 0)

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
                        FirebaseDynamicLinksActivity.startTarget(activity, obj.targetType, obj.targetId)
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }

        fun updateFollow(isFollow: Boolean) {
            setButtonText(binding.btnAction, isFollow, 0)
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

    inner class ViewHolderHorizontal(val binding: ItemAdsPageHorizontalBinding) : BaseVideoViewHolder(binding.root) {

        fun bind(obj: ICAdsData) {
            binding.imgImage.visibility = View.VISIBLE
            binding.surfaceView.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE

            if (!obj.media.isNullOrEmpty()) {
                if (obj.media!![0].type == Constant.VIDEO) {
                    binding.imgPlay.visibility = View.VISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, obj.media!![0].content, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, null, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                } else {
                    binding.imgPlay.visibility = View.INVISIBLE
                    if (!obj.media!![0].content.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, obj.media!![0].content, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    } else {
                        WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, null, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                    }
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, null, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }

            WidgetUtils.loadImageUrl(binding.imgAvatar, obj.avatar?.content, R.drawable.img_default_business_logo)
            if (obj.verified == true) {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            binding.tvName.text = obj.name

            if (obj.followCount != null && obj.followCount!! > 0) {
                binding.tvStatus.text = binding.tvStatus.context.getString(R.string.xxx_nguoi_theo_doi, TextHelper.formatMoney(obj.followCount))
            } else {
                binding.tvStatus.text = "Chưa có người theo dõi"
            }

            setButtonText(binding.btnAction, obj.isFollow, 0)

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
                        FirebaseDynamicLinksActivity.startTarget(activity, obj.targetType, obj.targetId)
                    } else {
                        FirebaseDynamicLinksActivity.startTarget(activity, targetType, targetID)
                    }
                }
            }
        }

        fun updateFollow(isFollow: Boolean) {
            setButtonText(binding.btnAction, isFollow, 0)
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

    inner class ViewHolderGrid(val binding: ItemAdsPageGridBinding) : BaseViewHolder<ICAdsData>(binding.root) {

        override fun bind(obj: ICAdsData) {
            binding.imgImage.visibility = View.VISIBLE

            if (!obj.media.isNullOrEmpty()) {
                if (!obj.media!![0].content.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, obj.media!![0].content, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                } else {
                    WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, null, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
                }
            } else {
                WidgetUtils.loadImageUrlRoundedTransformation(binding.imgImage, null, R.drawable.bg_default_page, R.drawable.bg_default_page, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)
            }

            WidgetUtils.loadImageUrl(binding.imgAvatar, obj.avatar?.content, R.drawable.img_default_business_logo)
            if (obj.verified == true) {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            binding.tvName.text = obj.name

            if (obj.followCount != null && obj.followCount!! > 0) {
                binding.tvStatus.text = binding.tvStatus.context.getString(R.string.xxx_nguoi_theo_doi, TextHelper.formatMoney(obj.followCount))
            } else {
                binding.tvStatus.text = "Chưa có người theo dõi"
            }

            setButtonText(binding.tvAction, obj.isFollow, 0)

            binding.tvAction.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                binding.btnAction.isEnabled = binding.tvAction.isEnabled
            }

            binding.btnAction.setOnClickListener {
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

        fun updateFollow(isFollow: Boolean) {
            setButtonText(binding.tvAction, isFollow, 0)
        }
    }

    private fun setButtonText(tvButton: AppCompatTextView, isFollow: Boolean, drawable: Int) {
        when (adsType) {
            Constant.PAGE_APPROACH -> { // Tiếp cận
                tvButton.setText(R.string.ghe_tham)
            }
            Constant.PAGE_CHANGE_SUBCRIBE -> { // Chuyển đổi tham gia
                if (isFollow) {
                    tvButton.setTextColor(Color.parseColor("#757575"))
                    tvButton.setText(R.string.dang_theo_doi)
                } else {
                    tvButton.setTextColor(Color.parseColor("#057DDA"))
                    tvButton.setText(R.string.theo_doi)
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
            Constant.PAGE_APPROACH -> { // Tiếp cận
                ICheckApplication.currentActivity()?.let { activity ->
                    ActivityUtils.startActivity<PageDetailActivity, Long>(activity, Constant.DATA_1, obj.id.toLong())
                }
            }
            Constant.PAGE_CHANGE_SUBCRIBE -> { // Chuyển đổi tham gia
                followPage(viewHolder, obj.id.toLong())
            }
            else -> { // Liên hệ

            }
        }
    }

    private fun followPage(viewHolder: RecyclerView.ViewHolder, pageID: Long) {
        ICheckApplication.currentActivity()?.let { activity ->
            DialogHelper.showLoading(activity)

            repository.followPage(pageID, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)

                    appDao.insertPageFollow(ICMyFollowingPage(pageID))

                    for (i in listData.size - 1 downTo 0) {
                        if (listData[i].id.toLong() == pageID) {
                            listData[i].isFollow = true
                            when (viewHolder) {
                                is ViewHolderSlide -> {
                                    viewHolder.updateFollow(listData[i].isFollow)
                                }
                                is ViewHolderHorizontal -> {
                                    viewHolder.updateFollow(listData[i].isFollow)
                                }
                                is ViewHolderGrid -> {
                                    viewHolder.updateFollow(listData[i].isFollow)
                                }
                            }
                        }
                    }

                    for (parent in Constant.getlistAdsNew()) {
                        if (parent.objectType == Constant.PAGE) {
                            for (child in parent.data) {
                                if (child.id.toLong() == pageID) {
                                    child.isFollow = true
                                }
                            }
                        }
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(ICheckApplication.getInstance(), error?.message
                            ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }
}