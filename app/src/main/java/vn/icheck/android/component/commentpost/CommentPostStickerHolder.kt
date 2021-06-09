package vn.icheck.android.component.commentpost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_comment_post_sticker.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.util.kotlin.WidgetUtils

class CommentPostStickerHolder(parent: ViewGroup, val listener: ICommentPostView) : BaseViewHolder<ICCommentPost>(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_post_sticker, parent, false)) {

    override fun bind(obj: ICCommentPost) {
        itemView.layoutParams = (itemView.layoutParams as RecyclerView.LayoutParams).apply {
            setMargins(obj.marginStart, obj.marginTop, 0, 0)
        }

        if (obj.page != null) {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.page?.avatar, R.drawable.ic_circle_avatar_default)
            itemView.tvTitle.text = obj.page?.name
        } else {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.user?.avatar, R.drawable.ic_circle_avatar_default)
            itemView.tvTitle.text = obj.user?.name
        }

        WidgetUtils.loadImageFitCenterUrl(itemView.imageView, obj.media?.get(0)?.content)

        itemView.tvTime.text = TimeHelper.convertDateTimeSvToCurrentDay(obj.createdAt)
        checkLike(obj.expressive)

        itemView.tvAnswer.visibility = if (!obj.isReply) {
            View.GONE
        } else {
            View.VISIBLE
        }

        itemView.tvAnswer.setOnClickListener {
            listener.onAnswer(obj)
        }

        itemView.setOnLongClickListener {
            showOption(obj)
            true
        }
    }

    private fun checkLike(isLike: String?) {
        itemView.tvLike.setTextColor(if (isLike != null) {
            ContextCompat.getColor(itemView.context, R.color.red_like_question)
        } else {
            ContextCompat.getColor(itemView.context, R.color.black_50)
        })
    }

    private fun showOption(obj: ICCommentPost) {
        ICheckApplication.currentActivity()?.let { activity ->
            object : CommentPostOptionDialog(activity) {
                override fun onAnswer() {
                    itemView.tvAnswer.performClick()
                }

                override fun onEdit() {

                }

                override fun onDelete() {
                    listener.onDelete(obj)
                }
            }.show(obj)
        }
    }
}