package vn.icheck.android.screen.user.list_product_question.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_comment_post.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.image.DetailImagesActivity
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.commentpost.CommentPostMoreHolder
import vn.icheck.android.component.commentpost.CommentPostOptionDialog
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.comment.CommentRepository
import vn.icheck.android.network.models.ICProductQuestion
import vn.icheck.android.screen.user.list_product_question.view.IListProductQuestionView
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ListProductQuestionAdapter(val callback: IListProductQuestionView) : RecyclerViewCustomAdapter<Any>(callback) {
    private val commentRepository = CommentRepository()

    private val itemType = 3
    private val optionType = 4

    fun addQuestion(obj: ICProductQuestion) {
        if (listData.isNotEmpty()) {
            listData.add(0, obj)
            notifyItemInserted(0)
            notifyItemChanged(0, itemCount)
        } else {
            listData.add(obj)
            notifyDataSetChanged()
        }
    }

    fun addListAnswer(list: MutableList<ICProductQuestion>) {
        if (list.isNotEmpty()) {
            val questionId = list[0].parentID

            for (i in listData.size - 1 downTo 0) {
                if (listData[i] is ICCommentPostMore) {
                    if ((listData[i] as ICCommentPostMore).parentID == questionId) {

                        (listData[i] as ICCommentPostMore).isLoading = false

                        if (list[0].id != -1L) {
                            (listData[i] as ICCommentPostMore).currentCount += list.size
                            (listData[i] as ICCommentPostMore).offset += list.size

                            if ((listData[i] as ICCommentPostMore).currentCount >= (listData[i] as ICCommentPostMore).totalCount) {
                                listData.removeAt(i)
                            }

                            listData.addAll(i, list)
                            notifyDataSetChanged()
                        } else {
                            notifyItemChanged(i)
                        }
                        return
                    }
                }
            }
        }
    }

    fun addAnswer(obj: ICProductQuestion): Int? {
        for (i in listData.size - 1 downTo 0) {
            when (listData[i]) {
                is ICProductQuestion -> {
                    if ((listData[i] as ICProductQuestion).id == obj.parentID) {
                        listData.add(i + 1, obj)
                        notifyItemInserted(i + 1)
                        notifyItemChanged(i + 1, itemCount)
                        return i
                    }
                }
                is ICCommentPostMore -> {
                    if ((listData[i] as ICCommentPostMore).parentID == obj.parentID) {
                        if ((listData[i] as ICCommentPostMore).notIDs.isNotEmpty()) {
                            (listData[i] as ICCommentPostMore).notIDs += ",${obj.id}"
                        } else {
                            (listData[i] as ICCommentPostMore).notIDs = obj.id.toString()
                        }
                    }
                }
            }
        }
        return null
    }

    fun deleteComment(obj: ICProductQuestion) {
        if (obj.parentID == null) {
            deleteQuestion(obj)
        } else {
            deleteAnswer(obj)
        }
    }

    private fun deleteQuestion(obj: ICProductQuestion) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICProductQuestion) {
                if ((listData[i] as ICProductQuestion).parentID != null) {
                    if ((listData[i] as ICProductQuestion).parentID == obj.id) {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                } else {
                    if ((listData[i] as ICProductQuestion).id == obj.id) {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                }
            } else if (listData[i] is ICCommentPostMore) {
                if ((listData[i] as ICCommentPostMore).parentID == obj.id) {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                }
            }
        }

        if (listData.isNullOrEmpty()) {
            setError(R.drawable.ic_empty_questions, "Chưa có câu hỏi cho sản phẩm này.\nHãy đặt câu hỏi để được giải đáp thắc mắc ở đây", -1)
        }
    }


    private fun deleteAllAnswer(parentID: Long) {
        for (i in listData.size - 1 downTo 0) {
            when (listData[i]) {
                is ICProductQuestion -> {
                    if ((listData[i] as ICProductQuestion).parentID == parentID) {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                }
                is ICCommentPostMore -> {
                    if ((listData[i] as ICCommentPostMore).parentID == parentID) {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                }
            }
        }
    }

    private fun deleteAnswer(obj: ICProductQuestion) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICProductQuestion) {
                if ((listData[i] as ICProductQuestion).id == obj.id) {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                    notifyItemChanged(if (i > 0) {
                        i - 1
                    } else 0, itemCount)
                    return
                }
            }
        }
    }

    fun updateComment(obj: ICProductQuestion) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICProductQuestion) {
                if ((listData[i] as ICProductQuestion).id == obj.id) {
                    listData[i] = obj
                    notifyItemChanged(i)
                }
            }
        }
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position] is ICProductQuestion) {
            itemType
        } else {
            optionType
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> QuestionsHolder(parent)
            else -> CommentPostMoreHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        when (holder) {
            is QuestionsHolder -> {
                if (listData[position] is ICProductQuestion) {
                    holder.bind(listData[position] as ICProductQuestion)
                }
            }
            is CommentPostMoreHolder -> {
                holder.bind(listData[position] as ICCommentPostMore, object : ItemClickListener<ICCommentPostMore> {
                    override fun onItemClick(position: Int, item: ICCommentPostMore?) {
                        callback.onLoadMoreAnswer(listData[position] as ICCommentPostMore)
                        notifyItemChanged(position)
                    }
                })
            }
        }
    }

    inner class QuestionsHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_post, parent, false)) {

        fun bind(obj: ICProductQuestion) {
            itemView.layoutParams = (itemView.layoutParams as RecyclerView.LayoutParams).apply {
                setMargins(obj.marginStart, obj.marginTop, 0, 0)
            }

            if (obj.page == null) {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.user?.avatar, R.drawable.ic_circle_avatar_default)
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

            itemView.tvTime.text = TimeHelper.convertDateTimeSvToCurrentDay(obj.createdAt)

            checkLike(obj.expressive)

            itemView.tvAnswer.visibleOrGone(obj.isReply)

            itemView.tvLike.setOnClickListener {
                likeComment(obj)
            }

            itemView.setOnClickListener {
                callback.hideKeyboard()
            }

            itemView.tvAnswer.setOnClickListener {
                callback.onAnswer(obj)
            }

            val listString = arrayListOf<String?>()
            for (image in obj.media ?: mutableListOf()) {
                listString.add(image.content)
            }
            itemView.imageView.setOnClickListener {
                DetailImagesActivity.start(listString, itemView.context)
            }

            itemView.imgAvatar.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    if (obj.page == null) {
                        IckUserWallActivity.create(obj.user?.id, it)
                    } else {
                        PageDetailActivity.start(it, obj.page!!.id!!)
                    }
                }
            }
            itemView.tvTitle.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    if (obj.page == null) {
                        IckUserWallActivity.create(obj.user?.id, it)
                    } else {
                        PageDetailActivity.start(it, obj.page!!.id!!)
                    }
                }
            }

            itemView.setOnLongClickListener {
                showOption(obj)
                true
            }
        }

        private fun checkLike(expressived: String?) {
            if (expressived == null) {
                itemView.tvLike.setTextColor(Constant.getSecondTextColor(itemView.context))
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

        private fun showOption(obj: ICProductQuestion) {
            if (!obj.isReply && obj.user?.id != SessionManager.session.user?.id && obj.content.isNullOrEmpty()) {
                return
            }

            ICheckApplication.currentActivity()?.let { activity ->
                object : CommentPostOptionDialog(activity) {
                    override fun onAnswer() {
                        callback.onAnswer(obj)
                    }

                    override fun onEdit() {
                        callback.onEdit(obj)
                    }

                    override fun onDelete() {
                        callback.onDelete(obj)
                    }
                }.show(obj)
            }
        }
    }
}