package vn.icheck.android.component.product_question_answer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_comment_post.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.commentpost.CommentPostMoreHolder
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.comment.CommentRepository
import vn.icheck.android.network.models.ICProductQuestion
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.getImageSize
import vn.icheck.android.util.ick.setRankUser
import vn.icheck.android.util.ick.visibleOrGone
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ItemQuestionAdapter(val questionListener: ProductDetailListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val commentRepository = CommentRepository()
    private val listData = mutableListOf<Any>()

    private val emptyType = 1
    private val itemType = 2
    private val optionType = 3

    fun setData(list: MutableList<Any>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (listData.isNullOrEmpty())
            1
        else
            listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (!listData.isNullOrEmpty())
            if (listData[position] is ICProductQuestion) {
                itemType
            } else {
                optionType
            }
        else
            emptyType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(parent)
            optionType -> CommentPostMoreHolder(parent)
            else -> NotQuestionHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position] as ICProductQuestion)
            }
            is CommentPostMoreHolder -> {
                holder.bind(listData[position] as ICCommentPostMore, object : ItemClickListener<ICCommentPostMore> {
                    override fun onItemClick(position: Int, item: ICCommentPostMore?) {
                        if (item != null) {
                            questionListener.clickGoQa(item)
                        }
                    }
                })
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_post, parent, false)) {

        fun bind(obj: ICProductQuestion) {
            itemView.layoutParams = (itemView.layoutParams as RecyclerView.LayoutParams).apply {
                setMargins(obj.marginStart, obj.marginTop, 0, 0)
            }

            if (obj.page == null) {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.user?.avatar, R.drawable.ic_avatar_default_84px)
                itemView.imgLevel.setRankUser(obj.user?.rank?.level)
                itemView.tvTitle.apply {
                    text = obj.user?.getName
                    if (obj.user?.kycStatus == 2) {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
            } else {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.page!!.avatar, R.drawable.ic_business_v2)
                itemView.imgLevel.beGone()
                itemView.tvTitle.text = obj.page?.getName
                if (obj.page!!.isVerify) {
                    itemView.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
                } else {
                    itemView.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            if (obj.content.isNullOrEmpty()) {
                itemView.tvContent.visibility = View.GONE
            } else {
                itemView.tvContent.visibility = View.VISIBLE
                itemView.tvContent.text = obj.content
            }

            if (obj.media.isNullOrEmpty()) {
                itemView.imageView.visibility = View.GONE
            } else {
                itemView.imageView.visibility = View.VISIBLE
                WidgetUtils.loadImageUrlRounded(itemView.imageView, obj.media!![0].content.getImageSize(115, 115), SizeHelper.size4)
            }

            itemView.tvTime.text = TimeHelper.convertDateTimeSvToCurrentTimeDate(obj.createdAt)
            checkLike(obj.expressive)

            itemView.tvAnswer.visibleOrGone(obj.isReply)

            itemView.tvAnswer.setOnClickListener {
                questionListener.clickAnswersInQuestion(obj)
            }

            itemView.tvLike.setOnClickListener {
                likeComment(obj)
            }

            val listString = arrayListOf<String?>()
            for (image in obj.media ?: mutableListOf()) {
                listString.add(image.content)
            }
            itemView.imageView.setOnClickListener {
                DetailMediaActivity.start(itemView.context, listString)
            }

            itemView.imgAvatar.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    ICheckApplication.currentActivity()?.let {
                        if (obj.page == null) {
                            IckUserWallActivity.create(obj.user?.id, it)
                        } else {
                            obj.page!!.id?.let { idPage -> PageDetailActivity.start(it, idPage) }
                        }
                    }
                }
            }

            itemView.tvTitle.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    ICheckApplication.currentActivity()?.let {
                        if (obj.page == null) {
                            IckUserWallActivity.create(obj.user?.id, it)
                        } else {
                            obj.page!!.id?.let { idPage -> PageDetailActivity.start(it, idPage) }
                        }
                    }
                }
            }

            itemView.tvTitle.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    IckUserWallActivity.create(obj.user?.id, it)
                }
            }
        }

        private fun checkLike(expressived: String?) {
            if (expressived == null) {
                itemView.tvLike.setTextColor(ContextCompat.getColor(itemView.context, R.color.fast_survey_gray))
            } else {
                itemView.tvLike.setTextColor(ContextCompat.getColor(itemView.context, R.color.red_like_question))
            }
        }

        private fun likeComment(objQuestion: ICProductQuestion) {
            ICheckApplication.currentActivity()?.let { activity ->
                if (NetworkHelper.isNotConnected(activity)) {
                    ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    return
                }

                DialogHelper.showLoading(activity)

                if (objQuestion.parentID == null) {
                    commentRepository.likeQuestion(objQuestion.id, object : ICNewApiListener<ICResponseCode> {
                        override fun onSuccess(obj: ICResponseCode) {
                            DialogHelper.closeLoading(activity)
                            if (objQuestion.expressive.isNullOrEmpty()) {
                                objQuestion.expressive = "like"
                            } else {
                                objQuestion.expressive = null
                            }
                            checkLike(objQuestion.expressive)
                        }

                        override fun onError(error: ICResponseCode?) {
                            DialogHelper.closeLoading(activity)
                            ToastUtils.showShortError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                        }
                    })
                } else {
                    commentRepository.likeAnswer(objQuestion.id, object : ICNewApiListener<ICResponseCode> {
                        override fun onSuccess(obj: ICResponseCode) {
                            DialogHelper.closeLoading(activity)
                            if (objQuestion.expressive.isNullOrEmpty()) {
                                objQuestion.expressive = "like"
                            } else {
                                objQuestion.expressive = null
                            }
                            checkLike(objQuestion.expressive)
                        }

                        override fun onError(error: ICResponseCode?) {
                            DialogHelper.closeLoading(activity)
                            ToastUtils.showShortError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                        }
                    })
                }
            }
        }
    }

    private class NotQuestionHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createNotQuestionHolder(parent.context))
}
