package vn.icheck.android.component.product_review.list_review

import android.content.Context
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.avatar_user.AvatarUserComponent
import vn.icheck.android.component.image.LayoutImageInPostComponent
import vn.icheck.android.component.rating_star.RatingStarComponent
import vn.icheck.android.component.review.ReviewBottomSheet
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.criterias.ICReviewBottom
import vn.icheck.android.network.models.post.ICImageInPost
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ToastUtils

class ItemListReviewHolder(context: Context) : RecyclerView.ViewHolder(ViewHelper.createItemListReviewHolder(context)) {
    lateinit var imgAvatar: AvatarUserComponent
    lateinit var tvName: AppCompatTextView
    lateinit var tvTime: AppCompatTextView
    lateinit var tvLike: AppCompatTextView
    lateinit var tvComment: AppCompatTextView
    lateinit var itemRating: RatingStarComponent
    lateinit var tvReview: AppCompatTextView
    lateinit var imageReview: LayoutImageInPostComponent

    fun bind(objReview: ItemListReviewModel) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as LinearLayout).run {
                imgAvatar = getChildAt(0) as AvatarUserComponent
                (getChildAt(1) as LinearLayout).run {
                    tvName = getChildAt(0) as AppCompatTextView
                    tvTime = getChildAt(1) as AppCompatTextView
                }
                tvLike = getChildAt(2) as AppCompatTextView
                tvComment = getChildAt(3) as AppCompatTextView
            }
            itemRating = getChildAt(1) as RatingStarComponent
            tvReview = getChildAt(2) as AppCompatTextView
            imageReview = getChildAt(3) as LayoutImageInPostComponent
        }
        val data = objReview.data

        itemView.layoutParams = if (itemView.layoutParams is RecyclerView.LayoutParams) {
            (itemView.layoutParams as RecyclerView.LayoutParams).apply {
                setMargins(0, data.marginTop, 0, 0)
            }
        } else {
            (itemView.layoutParams as LinearLayout.LayoutParams).apply {
                setMargins(0, data.marginTop, 0, 0)
            }
        }
        if (data.page != null) {
            imgAvatar.setData(data.page!!.avatar, null, R.drawable.ic_business_v2, true)
            tvName.text = data.page!!.name
        } else {
            imgAvatar.setData(data.user?.avatar, data.user?.rank?.level, R.drawable.ic_avatar_default_84dp)
            tvName.text = data.user?.getName ?: ""
        }


        tvTime.text = TimeHelper.convertDateTimeSvToCurrentDate(data.createdAt)

        checkLike(objReview)

        tvLike.setOnClickListener {
            onLikeReview(objReview)
        }

        tvComment.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_POST, data))
        }

        tvComment.text = if (data.commentCount > 100) {
            "100+"
        } else {
            "${data.commentCount}"
        }

        itemRating.setData(data.avgPoint, data.avgPoint)

        if (data.content.isNullOrEmpty()) {
            tvReview.visibility = View.GONE
        } else {
            tvReview.visibility = View.VISIBLE
            tvReview.text = if (data.content!!.length > 130) {
                Html.fromHtml(vn.icheck.android.ichecklibs.ViewHelper.setSecondaryHtmlString(itemView.context.getString(R.string.xxx_xem_them, data.content!!.substring(0, 130)),itemView.context))
            } else {
                data.content
            }
        }

        if (!data.media.isNullOrEmpty()) {
            imageReview.beVisible()
            val arr = arrayListOf<ICImageInPost>()
            for (item in data.media!!) {
                if (item.content != null && item.type != null) {
                    arr.add(ICImageInPost(item.content!!, item.type!!, null, null))
                }
            }
            imageReview.setImageInPost(arr)
        } else {
            imageReview.beGone()
        }

        imageReview.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                MediaInPostActivity.start(data.id, it)
            }
        }

        itemRating.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                val name = if (data.page != null) {
                    data.page!!.getName
                } else {
                    data.user?.getName
                }
                ReviewBottomSheet.show((it as AppCompatActivity).supportFragmentManager, true,
                        ICReviewBottom(name, data.avgPoint, data.customerCriteria))
            }
        }

        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_POST, data.id))
            }
        }

        tvReview.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_POST, data.id))
            }
        }

        imgAvatar.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                IckUserWallActivity.create(data.user?.id, it)
            }
        }
        tvName.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                IckUserWallActivity.create(data.user?.id, it)
            }
        }
    }

    private fun checkLike(objReview: ItemListReviewModel) {
        tvLike.text = if (objReview.data.expressiveCount > 100) {
            "100+"
        } else {
            "${objReview.data.expressiveCount}"
        }

        tvLike.setCompoundDrawablesWithIntrinsicBounds(if (objReview.data.expressive == null) {
            R.drawable.ic_gray_like_12_px
        } else {
            R.drawable.ic_like_12px
        }, 0, 0, 0)
    }


    private fun onLikeReview(objReview: ItemListReviewModel) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        objReview.data.id.let {
            ProductReviewInteractor().postLikeReview(it, null, object : ICNewApiListener<ICResponse<ICNotification>> {
                override fun onSuccess(obj: ICResponse<ICNotification>) {
                    if (obj.data?.objectId == null) {
                        objReview.data.expressiveCount--
                        objReview.data.expressive = null
                    } else {
                        objReview.data.expressiveCount++
                        objReview.data.expressive = "like"
                    }
                    checkLike(objReview)
                }

                override fun onError(error: ICResponseCode?) {
                    ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }

    }
}