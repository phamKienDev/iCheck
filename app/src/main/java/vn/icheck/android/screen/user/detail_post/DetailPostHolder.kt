package vn.icheck.android.screen.user.detail_post

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_post_detail.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.image.LayoutImageInPostComponent
import vn.icheck.android.component.review.ReviewBottomSheet
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemPostDetailBinding
import vn.icheck.android.helper.*
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper.setTextNameProductInPost
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.criterias.ICReviewBottom
import vn.icheck.android.network.models.post.ICImageInPost
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.setRankUser
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewPointText

class DetailPostHolder(val binding: ItemPostDetailBinding, val listener: IDetailPostListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(obj: ICPost) {
        setupHeader(obj)
        setupContent(obj)
        setupImage(obj)
        setupProduct(obj)
        setupLikeComment(obj)
        listener(obj)
    }

    private fun setupHeader(obj: ICPost) {
        if (obj.page != null) {
            WidgetUtils.loadImageUrl(binding.imgLogo, obj.page?.avatar, R.drawable.img_default_business_logo_big)
            binding.tvName.text = obj.page?.getName
            binding.imgRank.beGone()
            if (obj.page!!.isVerify) {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_verified_16px,0)
            } else {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            }
        } else {
            WidgetUtils.loadImageUrl(binding.imgLogo, obj.user?.avatar, R.drawable.ic_avatar_default_84px)
            binding.tvName.text = obj.user?.getName
            binding.imgRank.beVisible()
            binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            binding.imgRank.setRankUser(obj.user?.rank?.level)
        }

        if (obj.involveType == Constant.REVIEW) {
            binding.layoutRating.beVisible()
            ReviewPointText.setText(itemView.tvRating, obj.avgPoint)
            binding.ratingStar.rating = obj.avgPoint
        } else {
            binding.layoutRating.beGone()
            binding.tvTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    private fun setupContent(obj: ICPost) {
        binding.tvTime.text = TimeHelper.convertDateTimeSvToCurrentDay2(obj.createdAt)
        if (obj.content.isNullOrEmpty()) {
            binding.tvContent.beGone()
        } else {
            binding.tvContent.text = obj.content!!.trim()
            binding.tvContent.beVisible()
        }
    }

    private fun setupImage(obj: ICPost) {
        val list = mutableListOf<ICImageInPost>()
        for (image in obj.media ?: mutableListOf()) {
            list.add(ICImageInPost(image.content ?: "", image.type ?: Constant.IMAGE, null, null))
        }
        val imageBanner = ((binding.root).getChildAt(1) as ViewGroup).getChildAt(0) as LayoutImageInPostComponent
        if (list.isNotEmpty()) {
            imageBanner.visibility = View.VISIBLE
            imageBanner.setImageInPost(list)

            imageBanner.onClickImageDetail(object : ItemClickListener<MutableList<ICImageInPost>> {
                override fun onItemClick(position: Int, item: MutableList<ICImageInPost>?) {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_MEDIA_IN_POST, obj.also {
                        it.positionMedia = position
                    }))
                }
            })
        } else {
            imageBanner.visibility = View.GONE
        }
    }

    private fun setupLikeComment(obj: ICPost) {
        checkLike(obj)
        binding.tvViewComment.text = TextHelper.formatMoneyPhay(obj.commentCount)
        binding.tvView.text = TextHelper.formatMoneyPhay(obj.viewCount)
        binding.tvShare.text = TextHelper.formatMoneyPhay(obj.shareCount)
    }

    private fun setupProduct(obj: ICPost) {
        if (obj.meta?.product != null) {
            binding.layoutProduct.visibility = View.VISIBLE

            if (obj.meta?.product?.media.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlRounded(binding.imgProduct, "", R.drawable.img_default_product_big, SizeHelper.size4)
            } else {
                WidgetUtils.loadImageUrlRounded(binding.imgProduct, obj.meta?.product?.media!![0].content, R.drawable.img_default_product_big, SizeHelper.size4)
            }
            binding.tvProduct.setTextNameProductInPost(obj.meta?.product?.name)

            if (obj.meta?.product?.owner != null) {
                binding.tvShopName.text = obj.meta?.product!!.owner!!.name
            }

        } else {
            binding.layoutProduct.visibility = View.GONE
        }
    }

    private fun listener(obj: ICPost) {
        binding.layoutRating.setOnClickListener {
            binding.layoutRating.isClickable = false
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity is FragmentActivity) {
                    val name = if (obj.page != null) {
                        obj.page!!.getName
                    } else {
                        obj.user?.getName
                    }
                    ReviewBottomSheet.show(activity.supportFragmentManager, true, ICReviewBottom(name, obj.avgPoint, obj.customerCriteria))
                }
            }
            Handler().postDelayed({
                binding.layoutRating.isClickable = true
            }, 1000)
        }

        binding.imgShowRating.setOnClickListener {
            binding.layoutRating.performClick()
        }

        binding.layoutProduct.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                obj.meta?.product?.id?.let { id -> IckProductDetailActivity.start(activity, id) }
            }
        }

        binding.tvLike.onDelayClick({
            listener.onLikePostDetail()
        })

        binding.tvShare.setOnClickListener {
            sharePost(obj)
        }

        binding.imgLogo.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                if (obj.page == null) {
                    IckUserWallActivity.create(obj.user?.id, it)
                } else {
                    PageDetailActivity.start(it, obj.page!!.id)
                }
            }
        }
        binding.tvName.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                if (obj.page == null) {
                    IckUserWallActivity.create(obj.user?.id, it)
                } else {
                    PageDetailActivity.start(it, obj.page!!.id)
                }
            }
        }
    }

    fun checkLike(obj: ICPost) {
        binding.tvLike.text = TextHelper.formatMoneyPhay(obj.expressiveCount)

        binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(if (obj.expressive == null) {
            R.drawable.ic_like_off_24dp
        } else {
            R.drawable.ic_like_on_24dp
        }, 0, 0, 0)
    }

    private fun sharePost(obj: ICPost) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            DialogHelper.showLoading(activity)

            PostInteractor().getShareLinkOfPost(obj.id, object : ICNewApiListener<ICResponse<String>> {
                override fun onSuccess(obj: ICResponse<String>) {
                    DialogHelper.closeLoading(activity)

                    obj.data?.let { shareLink ->
                        ShareCompat.IntentBuilder.from(activity)
                                .setType("text/plain")
                                .setChooserTitle(activity.getString(R.string.chia_se))
                                .setText(shareLink)
                                .startChooser()
                        binding.tvShare.text = (binding.tvShare.text.toString().toInt() + 1).toString()
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    val message = error?.message
                            ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    ToastUtils.showLongError(activity, message)
                }
            })
        }
    }

    companion object {
        fun create(parent: ViewGroup, callback: IDetailPostListener): DetailPostHolder {
            return DetailPostHolder(ItemPostDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false), callback)
        }
    }
}