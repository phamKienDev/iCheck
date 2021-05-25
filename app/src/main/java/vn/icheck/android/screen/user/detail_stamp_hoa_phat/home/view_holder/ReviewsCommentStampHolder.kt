package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.avatar_user_28px.view.*
import kotlinx.android.synthetic.main.comment_holder_v1.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.CommentTimeUtil
import vn.icheck.android.util.ui.GlideUtil

class ReviewsCommentStampHolder(parent: ViewGroup) : BaseViewHolder<ICProductReviews.Comments>(LayoutInflater.from(parent.context).inflate(R.layout.comment_holder_v1, parent, false)) {

    override fun bind(obj: ICProductReviews.Comments) {
        WidgetUtils.loadImageUrl(itemView.ava, obj.owner?.avatarThumb?.small, R.drawable.ic_circle_avatar_default)
        itemView.ava.setOnClickListener {
            showDetailUser(obj.customerId)
        }

        itemView.tv_name.text = obj.owner?.name
        itemView.tv_name.setOnClickListener {
            showDetailUser(obj.customerId)
        }

        itemView.tv_time.text = CommentTimeUtil(obj).getTime()
        itemView.tv_comment.text = obj.activityValue
        if (obj.ownerType == "page") {
            itemView.ava.borderWidth = 1
            itemView.ava.borderColor = vn.icheck.android.ichecklibs.Constant.getSecondaryColor(itemView.context)
            itemView.tv_name.setTextColor(itemView.ava.borderColor)
            itemView.img_verified.visibility = View.VISIBLE
        } else {
            itemView.ava.borderWidth = 0
            itemView.tv_name.setTextColor(Color.BLACK)
            itemView.img_verified.visibility = View.GONE
        }
        if (obj.imageThumbs.isNotEmpty()) {
            itemView.img_cm_1.setOnClickListener {
                showDetailImages(obj.imageThumbs.map {
                    it.original.toString()
                })
            }
            itemView.img_cm_2.setOnClickListener {
                showDetailImages(obj.imageThumbs.map {
                    it.original.toString()
                })
            }
            itemView.img_cm_3.setOnClickListener {
                showDetailImages(obj.imageThumbs.map {
                    it.original.toString()
                })
            }
            when (obj.imageThumbs.size) {
                1 -> {
                    GlideUtil.loading(obj.imageThumbs.first().small, itemView.img_cm_1)
                }
                2 -> {
                    GlideUtil.loading(obj.imageThumbs.first().small, itemView.img_cm_1)
                    GlideUtil.loading(obj.imageThumbs.last().small, itemView.img_cm_2)
                }
                3 -> {
                    GlideUtil.loading(obj.imageThumbs.first().small, itemView.img_cm_1)
                    GlideUtil.loading(obj.imageThumbs.get(1).small, itemView.img_cm_2)
                    GlideUtil.loading(obj.imageThumbs.last().small, itemView.img_cm_3)
                }
                else -> {
                    GlideUtil.loading(obj.imageThumbs.first().small, itemView.img_cm_1)
                    GlideUtil.loading(obj.imageThumbs.get(1).small, itemView.img_cm_2)
                    GlideUtil.bind(R.drawable.group_more_product, itemView.img_cm_3)
                }
            }
        }
    }

    private fun showDetailUser(id: Long) {
//        ICheckApplication.currentActivity()?.let { activity ->
//            UserInteractor().getUserChatByID(id, object : ICApiListener<ICUserId> {
//                override fun onSuccess(obj: ICUserId) {
//                    if (obj.type == "user") {
//                        ActivityUtils.startActivity<ProfileActivity, Long>(activity, Constant.DATA_1, obj.id!!)
//                    }
//                    if (obj.type == "page") {
//                        BusinessActivity.start(obj.id, activity)
//                    }
//                    if (obj.type == "shop") {
//                        ShopDetailActivity.start(obj.id, activity)
//                    }
//                }
//
//                override fun onError(error: ICBaseResponse?) {
//
//                }
//            })
//        }
    }

    fun showDetailImages(list: List<String?>) {
        ICheckApplication.currentActivity()?.let { activity ->
            val arr = arrayListOf<String?>()
            arr.addAll(list)
            DetailMediaActivity.start(activity,arr)
        }
    }
}